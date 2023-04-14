package com.itheima.service;

import com.itheima.dto.User;
import com.itheima.dto.UserDto;

/**
 * @author Dsapa
 * @date 2023/4/1 0:56
 * @Description
 */
public interface ChatService {
    UserDto chat(User user);
}
