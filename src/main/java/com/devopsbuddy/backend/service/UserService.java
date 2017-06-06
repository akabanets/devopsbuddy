package com.devopsbuddy.backend.service;

import com.devopsbuddy.backend.persistence.domain.backend.Plan;
import com.devopsbuddy.backend.persistence.domain.backend.User;
import com.devopsbuddy.backend.persistence.domain.backend.UserRole;
import com.devopsbuddy.backend.persistence.repositories.PlanRepository;
import com.devopsbuddy.backend.persistence.repositories.RoleRepository;
import com.devopsbuddy.backend.persistence.repositories.UserRepository;
import com.devopsbuddy.enums.PlansEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Transactional(readOnly = true)
public class UserService {
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public User createUser(User user, PlansEnum plansEnum, Set<UserRole> userRoles) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Plan plan = new Plan(plansEnum);
        plan = planRepository.save(plan);
        user.setPlan(plan);

        for (UserRole userRole : userRoles) {
            userRole.setRole(roleRepository.save(userRole.getRole()));
        }
        user.getUserRoles().addAll(userRoles);

        user = userRepository.save(user);
        return user;
    }

    @Transactional
    public void changePassword(long userId, String newPassword) {
        User user = userRepository.findOne(userId);
        if (user != null) {
            user.setPassword(passwordEncoder.encode(newPassword));
        }
    }
}
