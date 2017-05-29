package com.devopsbuddy.test.integration;

import com.devopsbuddy.DevopsbuddyApplication;
import com.devopsbuddy.backend.persistence.domain.backend.Plan;
import com.devopsbuddy.backend.persistence.domain.backend.Role;
import com.devopsbuddy.backend.persistence.domain.backend.User;
import com.devopsbuddy.backend.persistence.domain.backend.UserRole;
import com.devopsbuddy.backend.persistence.repositories.PlanRepository;
import com.devopsbuddy.backend.persistence.repositories.RoleRepository;
import com.devopsbuddy.backend.persistence.repositories.UserRepository;
import com.devopsbuddy.backend.persistence.repositories.UserRoleRepository;
import com.devopsbuddy.enums.PlansEnum;
import com.devopsbuddy.enums.RolesEnum;
import com.devopsbuddy.utils.UserUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DevopsbuddyApplication.class)
public class RepositoriesIntegrationTest {
    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @PersistenceContext
    private EntityManager entityManager;


    @Before
    public void init() {
        Assert.assertNotNull(planRepository);
        Assert.assertNotNull(roleRepository);
        Assert.assertNotNull(userRepository);

        createPlan(PlansEnum.BASIC);
        createPlan(PlansEnum.PRO);

        createRole(RolesEnum.BASIC);
        createRole(RolesEnum.PRO);
        createRole(RolesEnum.ADMIN);
    }

    @Test
    public void testCreateBasicUser() {
        User basicUser = createNewUser();
        verifyBasicUser(basicUser.getId());
    }

    @Test
    @Transactional
    public void testDeleteBasicUser() {
        long usersInTheBeginning = userRepository.count();
        long userRoleInTheBeginning = userRoleRepository.count();

        User basicUser = createNewUser();
        entityManager.flush();

        Assert.assertEquals(usersInTheBeginning+1, userRepository.count());
        Assert.assertEquals(userRoleInTheBeginning+1, userRoleRepository.count());

        userRepository.delete(basicUser.getId());

        Assert.assertEquals(usersInTheBeginning, userRepository.count());
        Assert.assertEquals(userRoleInTheBeginning, userRoleRepository.count());

        Assert.assertEquals(2, planRepository.count());
        Assert.assertEquals(3, roleRepository.count());
    }

    private User createNewUser() {
        User basicUser = UserUtils.createBasicUser();
        basicUser.setPlan(planRepository.findOne(PlansEnum.BASIC.getId()));
        Role basicRole = roleRepository.findOne(RolesEnum.BASIC.getId());
        basicUser.getUserRoles().add(new UserRole(basicUser, basicRole));
        return userRepository.save(basicUser);
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

    private Plan createPlan(PlansEnum plansEnum) {
        return planRepository.save(new Plan(plansEnum));
    }

    private Role createRole(RolesEnum rolesEnum) {
        return roleRepository.save(new Role(rolesEnum));
    }

}
