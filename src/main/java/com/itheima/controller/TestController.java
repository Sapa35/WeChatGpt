package com.itheima.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Dsapa
 * @date 2021/11/24 20:39
 * @Description
 */
@RestController
public class TestController {

    @RequestMapping("/hello")
    public String test(){
        System.out.println("Helloworld,fuck u");
        return "Helloworld,fuck u";
    }
}
