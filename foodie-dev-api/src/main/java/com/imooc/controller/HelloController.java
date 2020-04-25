package com.imooc.controller;

import com.sun.org.apache.bcel.internal.generic.RETURN;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/HELLO")
    public Object hello(){
        return "hello SpringBoot";
}

}
