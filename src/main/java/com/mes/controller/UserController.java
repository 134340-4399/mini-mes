package com.mes.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mes.LoginRequest;
import com.mes.common.Result;
import com.mes.dto.UserResponseDTO;
import com.mes.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginRequest request) {

            String token = userService.login(request);
            return Result.ok(token);

    }

    @PostMapping("/register")
    public Result<Void> register(@RequestBody LoginRequest request) {
        userService.register(request);
        return Result.ok();
    }
    @GetMapping("/users")
    public Result<IPage<UserResponseDTO>> listUsers(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
        return Result.ok(userService.listUsers(page, size));
    }
}