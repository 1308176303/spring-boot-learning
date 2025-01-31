package org.example.controller;

import jakarta.validation.constraints.Pattern;
import org.apache.tomcat.util.http.parser.Authorization;
import org.example.pojo.Result;
import org.example.pojo.User;
import org.example.service.UserService;
import org.example.utils.JwtUtil;
import org.example.utils.Md5Util;
import org.example.utils.ThreadLocalUtil;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.TimeoutUtils;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Validated
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @PostMapping("/register")
    public Result register(@Pattern(regexp = "^\\S{5,16}$") String username, @Pattern(regexp = "^\\S{5,16}$") String password){

        User u = userService.findByUsername(username);
        if(u == null ){
            //用户不存在
            // 注册
            userService.register(username,password);
            return Result.success();
        }else{
            //已存在
            return Result.error("用户名已存在");
        }
    }

    @PostMapping("/login")
    public Result<String> login(@Pattern(regexp = "^\\S{5,16}$") String username, @Pattern(regexp = "^\\S{5,16}$") String password){
        //根据用户名查询用户
        User loginUser = userService.findByUsername(username);
        //判断用户名是否存在
        if (loginUser == null){
            return Result.error("用户名不存在");
        }
        //判断密码是否正确
        if(Md5Util.getMD5String(password).equals(loginUser.getPassword())){
            //登陆成功
            Map<String, Object> claims = new HashMap<>();
            claims.put("id",loginUser.getId());
            claims.put("username",loginUser.getUsername());
            String token =  JwtUtil.genToken(claims);
            //把token存到redis中
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            operations.set(token,token, 1, TimeUnit.HOURS);
            return Result.success(token);
        }

        return Result.error("密码错误");
    }
    @RequestMapping("/userInfo")
    public Result<User> userInfo(/*@RequestHeader("Authorization") String token*/){
        //根据用户名查询用户
//        Map<String, Object> map = JwtUtil.parseToken(token);
//        String username = (String)map.get("username");
        Map<String,Object> map = ThreadLocalUtil.get();
        String username = (String) map.get("username");
        User user = userService.findByUsername(username);
        return Result.success(user);
    }

    @PutMapping("/update")
    public Result update(@RequestBody @Validated User user){
        userService.update(user);
        return Result.success();
    }

    @PatchMapping("updateAvatar")
    public Result updateAvatar(@RequestParam @URL String avatarUrl){
        userService.updateAvatar(avatarUrl);
        return Result.success();
    }

    @PatchMapping("/updatePwd")
    public Result updatePwd(@RequestBody Map<String,String> params,@RequestHeader("Authorization") String token){
        //1.校验参数
        String oldPwd = params.get("old_pwd");
        String newPwd = params.get("new_pwd");
        String rePwd = params.get("re_pwd");
        if(!StringUtils.hasLength(oldPwd)||!StringUtils.hasLength(newPwd)||!StringUtils.hasLength(rePwd)){
            return Result.error("缺少必要的参数");
        }
        //校验原密码是否正确
        //根据user service层拿到原密码跟oldpwd比对
        Map<String,Object> map = ThreadLocalUtil.get();
        String username = (String)map.get("username");
        User loginUser = userService.findByUsername(username);
        if(!loginUser.getPassword().equals(Md5Util.getMD5String(oldPwd))){
            return Result.error("原密码填写不正确");
        }
        if (!rePwd.equals(newPwd)){
            return Result.error("两次填写的新密码不一样");
        }
        //2.调用service完成密码更新
        userService.updatePwd(newPwd);
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        operations.getOperations().delete(token);
        return Result.success();
    }
}
