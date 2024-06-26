package org.chiu.micro.user.controller;

import org.chiu.micro.user.req.UserEntityRegisterReq;
import org.chiu.micro.user.service.UserRoleService;
import org.chiu.micro.user.service.UserService;
import org.chiu.micro.user.req.UserEntityReq;
import org.chiu.micro.user.lang.Result;
import org.chiu.micro.user.page.PageAdapter;
import lombok.RequiredArgsConstructor;
import org.chiu.micro.user.vo.UserEntityVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(value = "/sys/user")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    private final UserRoleService userRoleService;

    @GetMapping("/auth/register/page")
    public Result<String> getRegisterPage(@RequestParam String username) {
        return Result.success(() -> userService.getRegisterPage(username));
    }

    @GetMapping("/register/check")
    public Result<Boolean> checkRegisterPage(String token) {
        return Result.success(() -> userService.checkRegisterPage(token));
    }


    @PostMapping("/register/save")
    public Result<Void> saveRegisterPage(@RequestParam String token,
                                         @RequestBody @Valid UserEntityRegisterReq userEntityRegisterReq) {
        return Result.success(() -> userRoleService.saveRegisterPage(token, userEntityRegisterReq));
    }

    @PostMapping("/register/image/upload")
    public Result<String> imageUpload(@RequestParam MultipartFile image,
                                      @RequestParam String token) {
        return Result.success(() -> userService.imageUpload(token, image));
    }

    @GetMapping("/register/image/delete")
    public Result<Void> imageDelete(@RequestParam String url,
                                    @RequestParam String token) {
        return Result.success(() -> userService.imageDelete(token, url));
    }

    @PostMapping("/save")
    public Result<Void> saveOrUpdate(@RequestBody @Valid UserEntityReq userEntityReq) {
        return Result.success(() -> userRoleService.saveOrUpdate(userEntityReq));
    }

    @GetMapping("/page/{currentPage}")
    public Result<PageAdapter<UserEntityVo>> page(@PathVariable(value = "currentPage") Integer currentPage,
                                                  @RequestParam(value = "size", defaultValue = "5") Integer size) {
        return Result.success(() -> userRoleService.listPage(currentPage, size));
    }

    @PostMapping("/delete")
    public Result<Void> delete(@RequestBody @NotEmpty List<Long> ids) {
        return Result.success(() -> userRoleService.deleteUsers(ids));
    }

    @GetMapping("/info/{id}")
    public Result<UserEntityVo> info(@PathVariable(value = "id") Long id) {
        return Result.success(() -> userRoleService.findById(id));
    }

    @GetMapping("/download")
    public Result<byte[]> download() {
        byte[] data = userService.download();
        return Result.success(data);
    }
}
