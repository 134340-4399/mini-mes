package com.mes.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mes.LoginRequest;
import com.mes.common.BusinessException;
import com.mes.dto.UserResponseDTO;
import com.mes.entity.User;
import com.mes.mapper.UserMapper;
import com.mes.util.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserMapper userMapper,
                       BCryptPasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }
    public IPage<UserResponseDTO> listUsers(int pageNum, int pageSize) {
        Page<User> page = new Page<>(pageNum, pageSize);
        userMapper.selectPage(page, null);
        List<UserResponseDTO> dtoList = page.getRecords().stream()
                .map(user -> {
                    UserResponseDTO dto = new UserResponseDTO();
                    dto.setId(user.getId());
                    dto.setUsername(user.getUsername());
                    dto.setRealName(user.getRealName());
                    dto.setPhone(user.getPhone());
                    dto.setRole(user.getRole());
                    dto.setStatus(user.getStatus());
                    dto.setCreateTime(user.getCreateTime());
                    return dto;
                })
                .collect(Collectors.toList());
        Page<UserResponseDTO> dtoPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        dtoPage.setRecords(dtoList);
        return dtoPage;
    }
    public String login(LoginRequest request) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", request.getUsername());
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        boolean matches = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!matches) {
            throw new BusinessException("密码错误");
        }

        return jwtUtil.generateToken(user.getUsername());
    }

    public void register(LoginRequest request) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", request.getUsername());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRealName("测试用户");
        user.setPhone("13800000000");
        user.setRole("USER");
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());

        userMapper.insert(user);
    }
}