package org.chiu.micro.user.config;

import lombok.SneakyThrows;
import org.chiu.micro.user.cache.mq.CacheEvictMessageListener;
import org.chiu.micro.user.dto.ButtonDto;
import org.chiu.micro.user.dto.MenuDto;
import org.chiu.micro.user.dto.MenusAndButtonsDto;
import org.chiu.micro.user.valid.ListValueConstraintValidator;
import org.chiu.micro.user.valid.MenuValueConstraintValidator;
import org.chiu.micro.user.valid.PhoneConstraintValidator;
import org.chiu.micro.user.valid.UsernameConstraintValidator;
import org.springframework.aot.hint.*;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.springframework.util.ReflectionUtils.*;

@SuppressWarnings("all")
public class CustomRuntimeHints implements RuntimeHintsRegistrar {
    @SneakyThrows
    @Override// Register method for reflection
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        // Register method for reflection
        hints.reflection().registerMethod(findMethod(CacheEvictMessageListener.class, "handleMessage", Set.class), ExecutableMode.INVOKE);

        hints.reflection().registerConstructor(LinkedHashSet.class.getDeclaredConstructor(), ExecutableMode.INVOKE);
        hints.reflection().registerConstructor(ListValueConstraintValidator.class.getDeclaredConstructor(), ExecutableMode.INVOKE);
        hints.reflection().registerConstructor(PhoneConstraintValidator.class.getDeclaredConstructor(), ExecutableMode.INVOKE);
        hints.reflection().registerConstructor(UsernameConstraintValidator.class.getDeclaredConstructor(), ExecutableMode.INVOKE);
        hints.reflection().registerConstructor(MenuValueConstraintValidator.class.getDeclaredConstructor(), ExecutableMode.INVOKE);

        hints.serialization().registerType(MenusAndButtonsDto.class);
        hints.serialization().registerType(MenuDto.class);
        hints.serialization().registerType(ButtonDto.class);

        hints.reflection().registerType(
                TypeReference.of("com.github.benmanes.caffeine.cache.SSMSA"),
                MemberCategory.PUBLIC_FIELDS, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, MemberCategory.INVOKE_PUBLIC_METHODS);
    }
}
