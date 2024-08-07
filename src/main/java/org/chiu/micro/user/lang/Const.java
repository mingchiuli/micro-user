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

    USER("user");

    private final String info;
}

