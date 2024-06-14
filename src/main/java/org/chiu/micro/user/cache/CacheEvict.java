package org.chiu.micro.user.cache;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheEvict {

    Class<? extends CacheEvictHandler>[] handler();
}
