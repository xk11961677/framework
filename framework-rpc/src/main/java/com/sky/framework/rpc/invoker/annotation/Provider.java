package com.sky.framework.rpc.invoker.annotation;

import java.lang.annotation.*;

/**
 * @author
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Provider {

    String group();

    String version() default "1.0.0";

}
