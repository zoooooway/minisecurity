package org.hzw.minisecurity.start.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hzw
 * @version 1.0
 * @date 2021/9/29 17:59
 */
@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {

    @GetMapping("/me")
    public Authentication me(Authentication authentication) {
        return authentication;
    }
}
