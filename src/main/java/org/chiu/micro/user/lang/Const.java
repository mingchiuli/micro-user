package org.chiu.micro.user.lang;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author mingchiuli
 * @create 2021-12-14 11:58 AM
 */
@Getter
@AllArgsConstructor
public enum Const {

    ROLE_PREFIX("ROLE_"),

    BLOCK_USER("block_user:"),

    REGISTER_PREFIX("register_prefix:"),

    USER("user"),
    
    CACHE_EVICT_FANOUT_EXCHANGE("cache.user.evict.fanout.exchange"),
    
    CACHE_USER_EVICT_EXCHANGE("cache.user.direct.exchange"),
    
    CACHE_USER_EVICT_BINDING_KEY("cache.user.evict.binding");

    private final String info;
}

