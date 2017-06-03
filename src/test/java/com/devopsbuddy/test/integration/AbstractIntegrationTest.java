package com.devopsbuddy.test.integration;

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
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class AbstractIntegrationTest {
    @Autowired
    protected PlanRepository planRepository;
    @Autowired
    protected RoleRepository roleRepository;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected UserRoleRepository userRoleRepository;

    @PersistenceContext
    protected EntityManager entityManager;

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

    protected User createNewUser(String username, String email) {
        User basicUser = UserUtils.createBasicUser(username, email);
        basicUser.setPlan(planRepository.findOne(PlansEnum.BASIC.getId()));
        Role basicRole = roleRepository.findOne(RolesEnum.BASIC.getId());
        basicUser.getUserRoles().add(new UserRole(basicUser, basicRole));
        return userRepository.save(basicUser);
    }

    protected Plan createPlan(PlansEnum plansEnum) {
        return planRepository.save(new Plan(plansEnum));
    }

    protected Role createRole(RolesEnum rolesEnum) {
        return roleRepository.save(new Role(rolesEnum));
    }
}
