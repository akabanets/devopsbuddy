package com.devopsbuddy.utils;

import com.devopsbuddy.backend.persistence.domain.backend.User;

public class UserUtils {
    public static User createBasicUser() {
        User user = new User();
        user.setUsername("basicUser");
        user.setPassword("password");
        user.setEmail("andrey@email.com");
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
