package com.devopsbuddy.utils;

import com.devopsbuddy.backend.persistence.domain.backend.User;

public class UserUtils {
    public static User createBasicUser(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setPassword("password");
        user.setEmail(email);
        user.setFirstName("Andrey");
        user.setLastName("Space");
        user.setPhoneNumber("111-222-3333");
        user.setCountry("UA");
        user.setEnabled(true);
        user.setDescription("A basic user");
        user.setProfileImageUrl("http://aa.com/image.gif");
        return user;
    }
}
