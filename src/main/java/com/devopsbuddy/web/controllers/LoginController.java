package com.devopsbuddy.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LoginController {
    private static final String LOGIN_VIEW_NAME = "user/login";
    private static final String LOGIN_MODEL_KEY = "login";

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {
        return LOGIN_VIEW_NAME;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@ModelAttribute(LOGIN_MODEL_KEY) String loginData) {
        return LOGIN_VIEW_NAME;
    }
}
