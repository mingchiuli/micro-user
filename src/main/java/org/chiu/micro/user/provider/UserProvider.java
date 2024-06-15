package org.chiu.micro.user.provider;

import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import org.chiu.micro.user.lang.Result;
import org.chiu.micro.user.service.UserService;
import org.chiu.micro.user.vo.UserEntityRpcVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



/**
 * UserProvider
 */
@RestController
@RequestMapping(value = "/inner")
@RequiredArgsConstructor
@Validated
public class UserProvider {

    private final UserService userService;

    @GetMapping("/user/{userId}")
    public Result<UserEntityRpcVo> findById(@PathVariable Long userId) {
        return Result.success(() -> userService.findById(userId));
    }
    
}