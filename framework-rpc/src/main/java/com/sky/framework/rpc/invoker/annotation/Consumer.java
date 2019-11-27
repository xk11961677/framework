package com.sky.framework.rpc.invoker.annotation;

import java.lang.annotation.*;

/**
 * @author
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Consumer {

    String group();

    String version() default "1.0.0";

}
