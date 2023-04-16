package com.itheima.controller;

import com.itheima.dto.User;
import com.itheima.dto.UserDto;
import com.itheima.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Dsapa
 * @date 2023/4/1 0:57
 * @Description
 */
@RestController
@RequestMapping("/chat")
@CrossOrigin//解决跨域问题
public class ChatController {

    @Autowired
    private ChatService chatService;


    @PostMapping("/chatting")
    public UserDto chatResponse(@RequestBody User user) {
        return chatService.chat(user);
    }
}
