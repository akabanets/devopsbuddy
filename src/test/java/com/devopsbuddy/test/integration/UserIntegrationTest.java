package com.devopsbuddy.test.integration;

import com.devopsbuddy.DevopsbuddyApplication;
import com.devopsbuddy.backend.persistence.domain.backend.User;
import com.devopsbuddy.backend.persistence.domain.backend.UserRole;
import com.devopsbuddy.backend.service.UserSecurityService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DevopsbuddyApplication.class)
public class UserIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private UserSecurityService userSecurityService;

    @Before
    public void init() {
        super.init();
    }

    @Test
    public void testCreateBasicUser() {
        User basicUser = createNewUser("testCreateBasicUser", "testCreateBasicUser@email.com");
        verifyBasicUser(basicUser.getId());
    }

    @Test
    @Transactional
    public void testDeleteBasicUser() {
        long usersInTheBeginning = userRepository.count();
        long userRoleInTheBeginning = userRoleRepository.count();

        User basicUser = createNewUser("testDeleteBasicUser", "testDeleteBasicUser@email.com");
        entityManager.flush();

        Assert.assertEquals(usersInTheBeginning + 1, userRepository.count());
        Assert.assertEquals(userRoleInTheBeginning + 1, userRoleRepository.count());

        userRepository.delete(basicUser.getId());

        Assert.assertEquals(usersInTheBeginning, userRepository.count());
        Assert.assertEquals(userRoleInTheBeginning, userRoleRepository.count());

        Assert.assertEquals(2, planRepository.count());
        Assert.assertEquals(3, roleRepository.count());
    }

    @Test
    public void testUserSecurityService() {
        createNewUser("testUserSecurityService", "testUserSecurityService@email.com");
        UserDetails retrievedUser = userSecurityService.loadUserByUsername("testUserSecurityService");
        Assert.assertNotNull(retrievedUser);
    }

    private void verifyBasicUser(Long id) {
        User retrievedUser = userRepository.findOne(id);
        Assert.assertNotNull(retrievedUser);
        Assert.assertTrue(retrievedUser.getId() != 0);
        Assert.assertNotNull(retrievedUser.getPlan());
        Assert.assertNotNull(retrievedUser.getPlan().getId());
        for (UserRole userRole1 : retrievedUser.getUserRoles()) {
            Assert.assertNotNull(userRole1.getRole());
            Assert.assertNotNull(userRole1.getUser());
        }
    }

}
