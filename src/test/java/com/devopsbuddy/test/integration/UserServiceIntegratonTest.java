package com.devopsbuddy.test.integration;

import com.devopsbuddy.DevopsbuddyApplication;
import com.devopsbuddy.backend.persistence.domain.backend.Role;
import com.devopsbuddy.backend.persistence.domain.backend.User;
import com.devopsbuddy.backend.persistence.domain.backend.UserRole;
import com.devopsbuddy.backend.service.UserService;
import com.devopsbuddy.enums.PlansEnum;
import com.devopsbuddy.enums.RolesEnum;
import com.devopsbuddy.utils.UserUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashSet;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DevopsbuddyApplication.class)
public class UserServiceIntegratonTest {
    @Autowired
    private UserService userService;

    @Test
    public void testCreateNewUser() {
        User transientUser = UserUtils.createBasicUser("basicUser");
        Set<UserRole> userRoles = new HashSet<>();
        userRoles.add(new UserRole(transientUser, new Role(RolesEnum.BASIC)));

        User persistedUser = userService.createUser(transientUser, PlansEnum.BASIC, userRoles);
        Assert.assertNotNull(persistedUser);
        Assert.assertNotNull(persistedUser.getId());
    }
}
