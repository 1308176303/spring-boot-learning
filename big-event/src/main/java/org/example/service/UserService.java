package org.example.service;

import org.example.pojo.User;

public interface UserService {
    //根据用户名查询user
    User findByUsername(String username);

    //注册
    void register(String username, String password);

    //更新
    void update(User user);

    //更新头像
    void updateAvatar(String avatarUrl);

    void updatePwd(String newPwd);
}
