package com.devopsbuddy.test.integration;

import com.devopsbuddy.DevopsbuddyApplication;
import com.devopsbuddy.backend.persistence.domain.backend.Plan;
import com.devopsbuddy.backend.persistence.domain.backend.Role;
import com.devopsbuddy.backend.persistence.domain.backend.User;
import com.devopsbuddy.backend.persistence.domain.backend.UserRole;
import com.devopsbuddy.backend.persistence.repositories.PlanRepository;
import com.devopsbuddy.backend.persistence.repositories.RoleRepository;
import com.devopsbuddy.backend.persistence.repositories.UserRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashSet;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DevopsbuddyApplication.class)
public class RepositoriesIntegrationTest {
    private static final int BASIC_PLAN_ID = 1;
    private static final int BASIC_ROLE_ID = 1;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Before
    public void init() {
        Assert.assertNotNull(planRepository);
        Assert.assertNotNull(roleRepository);
        Assert.assertNotNull(userRepository);
    }

    @Test
    public void testCreateNewPlan() {
        Plan basicPlan = createBasicPlan();
        planRepository.save(basicPlan);
        Plan retrievedPlan = planRepository.findOne(BASIC_PLAN_ID);
        Assert.assertNotNull(retrievedPlan);
    }

    @Test
    public void testCreateNewRole() {
        Role basicRole = createBasicRole();
        roleRepository.save(basicRole);
        Role retrievedRole = roleRepository.findOne(BASIC_ROLE_ID);
        Assert.assertNotNull(retrievedRole);
    }

    @Test
    public void createNewUser() {
        // Part 1 -> creation
        Plan basicPlan = createBasicPlan();
        planRepository.save(basicPlan);

        Role basicRole = createBasicRole();
        roleRepository.save(basicRole);

        User basicUser = createBasicUser();
        basicUser.setPlan(basicPlan);
        UserRole userRole = new UserRole();
        userRole.setRole(basicRole);
        userRole.setUser(basicUser);
        basicUser.getUserRoles().add(userRole);

        userRepository.save(basicUser);

        // Part 2 -> Retrieval and comparison
        User retrievedUser = userRepository.findOne(basicUser.getId());
        Assert.assertNotNull(retrievedUser);
        Assert.assertTrue(retrievedUser.getId() != 0);
        Assert.assertNotNull(retrievedUser.getPlan());
        Assert.assertNotNull(retrievedUser.getPlan().getId());
        for (UserRole userRole1 : retrievedUser.getUserRoles()) {
            Assert.assertNotNull(userRole1.getRole());
            Assert.assertNotNull(userRole1.getUser());
        }
    }

    private Plan createBasicPlan() {
        Plan plan = new Plan();
        plan.setId(BASIC_PLAN_ID);
        plan.setName("Basic");
        return plan;
    }

    private Role createBasicRole() {
        Role role = new Role();
        role.setId(BASIC_ROLE_ID);
        role.setName("Basic");
        return role;
    }

    private User createBasicUser() {
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
