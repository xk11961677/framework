package com.sky.framework.rpc.common.spi;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * @author
 */
public class SpiLoader {

    public static <S> S loadFirst(final Class<S> clazz) {
        final ServiceLoader<S> loader = loadAll(clazz);
        final Iterator<S> iterator = loader.iterator();
        if (!iterator.hasNext()) {
            throw new IllegalStateException(String.format(
                    "No implementation defined in /META-INF/services/%s, please check whether the file exists and has the right implementation class!",
                    clazz.getName()));
        }
        return iterator.next();
    }

    public static <S> ServiceLoader<S> loadAll(final Class<S> clazz) {
        return ServiceLoader.load(clazz);
    }
}
