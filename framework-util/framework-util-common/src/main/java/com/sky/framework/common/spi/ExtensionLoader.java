/*
 * The MIT License (MIT)
 * Copyright © 2019-2020 <sky>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the “Software”), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.sky.framework.common.spi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 扩展类加载器,并交给spring管理
 *
 * @author
 */
@Slf4j
public class ExtensionLoader implements ApplicationContextAware {

    private ApplicationContext context;

    private static final ConcurrentMap<Class<?>, TreeMap<String/*SpiMetadata.name()_SpiMetadata.priority()*/, Object>> EXTENSION_LOADERS = new ConcurrentHashMap<>();

    private static final String SERVICES_DIRECTORY = "META-INF/spi/";

    /**
     * @param type
     * @return
     */
    public TreeMap<String, Object> getExtensionLoader(Class<?> type) {
        if (type == null) {
            throw new IllegalArgumentException("Extension type == null");
        }
        if (!type.isInterface()) {
            throw new IllegalArgumentException("Extension type (" + type + ") is not an interface!");
        }
        TreeMap<String, Object> loader = EXTENSION_LOADERS.get(type);
        if (loader == null) {
            synchronized (ExtensionLoader.class) {
                loader = EXTENSION_LOADERS.get(type);
                if (loader == null) {
                    EXTENSION_LOADERS.putIfAbsent(type, loadExtensionClass(type.getName()));
                    loader = EXTENSION_LOADERS.get(type);
                }
            }
        }
        return loader;
    }

    /**
     * 根据type加载扩展类
     *
     * @param type
     * @return
     */
    private TreeMap<String, Object> loadExtensionClass(String type) {
        TreeMap<String, Object> extensionClasses = new TreeMap<>((o1, o2) -> {
            int first = Integer.parseInt(o1.substring(o1.lastIndexOf("_") + 1));
            int two = Integer.parseInt(o2.substring(o2.lastIndexOf("_") + 1));
            return first - two;
        });
        loadDirectory(extensionClasses, SERVICES_DIRECTORY, type);
        return extensionClasses;
    }

    /**
     * 加载目录
     *
     * @param extensionClasses
     * @param dir
     * @param type
     */
    private void loadDirectory(TreeMap<String, Object> extensionClasses, String dir, String type) {
        String fileName = dir + type;
        try {
            Enumeration<URL> urls;
            ClassLoader classLoader = findClassLoader();
            if (classLoader != null) {
                urls = classLoader.getResources(fileName);
            } else {
                urls = ClassLoader.getSystemResources(fileName);
            }
            if (urls != null) {
                while (urls.hasMoreElements()) {
                    URL resourcesURL = urls.nextElement();
                    loadResources(extensionClasses, classLoader, resourcesURL);
                }
            }
        } catch (Throwable t) {
            log.error("Exception occurred when loading extension class (interface: " + type + ", description file: " + fileName + ").", t);
        }
    }

    /**
     * 读取资源文件,
     *
     * @param extensionClasses
     * @param classLoader
     * @param resourceURL
     */
    private void loadResources(TreeMap<String, Object> extensionClasses, ClassLoader classLoader, URL resourceURL) {
        try {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceURL.openStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    final int ci = line.indexOf('#');
                    if (ci >= 0) {
                        line = line.substring(0, ci);
                    }
                    line = line.trim();
                    if (line.length() > 0) {
                        try {
                            loadClass(extensionClasses, resourceURL, Class.forName(line, true, classLoader));
                        } catch (Throwable t) {
                            log.error("Failed to load extension class (class line: " + line + ") in " + resourceURL + ", cause: " + t.getMessage(), t);
                        }
                    }
                }
            }
        } catch (Throwable t) {
            log.error("Exception occurred when loading extension class (class file: " + resourceURL + ") in " + resourceURL, t);
        }
    }

    /**
     * 获取类加载器
     *
     * @return
     */
    private static ClassLoader findClassLoader() {
        return DefaultListableBeanFactory.class.getClassLoader();
    }

    /**
     * 加载class
     *
     * @param extensionClasses
     * @param resourceURL
     * @param clazz
     * @throws NoSuchMethodException
     */
    private void loadClass(TreeMap<String, Object> extensionClasses, URL resourceURL, Class<?> clazz) throws IllegalStateException {
        SpiMetadata spiMetadata = clazz.getAnnotation(SpiMetadata.class);
        if (spiMetadata == null || StringUtils.isEmpty(spiMetadata.name())) {
            throw new IllegalStateException("No such extension name for the class " + clazz.getSimpleName() + " in the config " + resourceURL);
        }
        saveInExtensionClass(extensionClasses, clazz);
    }

    /**
     * 将扩展类存入map中
     *
     * @param extensionClasses
     * @param clazz
     */
    private void saveInExtensionClass(TreeMap<String, Object> extensionClasses, Class<?> clazz) {
        SpiMetadata spiMetadata = clazz.getAnnotation(SpiMetadata.class);
        String key = spiMetadata.name() + "_" + spiMetadata.priority();
        Object o = extensionClasses.get(key);
        if (o == null) {
            Object bean = parseClassToSpringBean(clazz);
            extensionClasses.put(key, bean);
        } else {
            throw new IllegalStateException("Duplicate extension name " + spiMetadata.name() + " on " + clazz.getName() + " and " + clazz.getName());
        }
    }

    /**
     * 根据class交给spring管理
     *
     * @param obj
     * @return
     */
    private Object parseClassToSpringBean(Class<?> obj) {
        SpiMetadata spiMetadata = obj.getAnnotation(SpiMetadata.class);
        String beanName = obj.getSimpleName().concat(spiMetadata.name());

        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(obj);
        GenericBeanDefinition definition = (GenericBeanDefinition) builder.getRawBeanDefinition();
        definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_NAME);
        getRegistry().registerBeanDefinition(beanName, definition);

        return context.getBean(beanName);
    }

    /**
     * 获取spring注册器
     *
     * @return
     */
    private BeanDefinitionRegistry getRegistry() {
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) context;
        return (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
