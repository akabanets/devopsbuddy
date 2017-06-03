package com.devopsbuddy.test.integration;

import com.devopsbuddy.DevopsbuddyApplication;
import com.devopsbuddy.backend.persistence.domain.backend.PasswordResetToken;
import com.devopsbuddy.backend.persistence.domain.backend.User;
import com.devopsbuddy.backend.persistence.repositories.PasswordResetTokenRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DevopsbuddyApplication.class)
public class PasswordResetTokenIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Test
    public void testRepository() {
        String token = "1111";

        User user = createNewUser("user1", "user1@email.com");
        passwordResetTokenRepository.save(new PasswordResetToken(token, user, 10));

        Assert.assertNotNull(passwordResetTokenRepository.findByToken(token).getExpiryDate());
        Assert.assertEquals(1, passwordResetTokenRepository.findAllByUserId(user.getId()).size());

        passwordResetTokenRepository.save(new PasswordResetToken("2222", user, 10));
        Assert.assertEquals(2, passwordResetTokenRepository.findAllByUserId(user.getId()).size());
    }
}
