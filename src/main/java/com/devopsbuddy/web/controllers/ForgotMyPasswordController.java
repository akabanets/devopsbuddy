package com.devopsbuddy.web.controllers;

import com.devopsbuddy.backend.persistence.domain.backend.PasswordResetToken;
import com.devopsbuddy.backend.persistence.domain.backend.User;
import com.devopsbuddy.backend.persistence.repositories.PasswordResetTokenRepository;
import com.devopsbuddy.backend.persistence.repositories.UserRepository;
import com.devopsbuddy.backend.service.EmailService;
import com.devopsbuddy.web.i18n.I18NService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.UUID;

@Controller
public class ForgotMyPasswordController {
    private final static Logger logger = LoggerFactory.getLogger(ForgotMyPasswordController.class);

    private final static String EMAIL_MESSAGE_TEXT_PROPERTY_NAME = "forgotmypassword.email.text";
    private static final String FORGOTMYPASSWORD_VIEW_NAME = "forgotmypassword/emailForm";
    private final static String CHANGE_PASSWORD_PATH = "/changeuserpassword";

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${token.expiration.length.minutes}")
    private int tokenExpirationLengthMinutes;

    @Autowired
    private EmailService emailService;

    @Autowired
    private I18NService i18NService;

    @Value("${webmaster.email}")
    private String webMasterEmail;

    @RequestMapping(value = "/forgotmypassword", method = RequestMethod.GET)
    public String prepareEnterEmailForm() {
        return FORGOTMYPASSWORD_VIEW_NAME;
    }

    @RequestMapping(value = "/forgotmypassword", method = RequestMethod.POST)
    public String processForgotPasswordRequest(HttpServletRequest request, @ModelAttribute("email") String email, ModelMap model) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            String tokenData = UUID.randomUUID().toString();
            PasswordResetToken passwordResetToken = new PasswordResetToken(tokenData, user, tokenExpirationLengthMinutes);
            passwordResetTokenRepository.save(passwordResetToken);
            logger.debug("Successfully created token {} for user {}", tokenData, user.getUsername());

            String resetPasswordUrl = createResetPasswordUrl(request, user, tokenData);
            sendEmailWithRestUrl(resetPasswordUrl, user, request.getLocale());
        }

        model.addAttribute("mailSent", true);

        return FORGOTMYPASSWORD_VIEW_NAME;
    }

    private void sendEmailWithRestUrl(String resetPasswordUrl, User user, Locale locale) {
        String emailText = i18NService.getMessage(EMAIL_MESSAGE_TEXT_PROPERTY_NAME, locale);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("[Devopsbuddy]: How to Reset Your Password");
        mailMessage.setText(emailText + "\r\n" + resetPasswordUrl);
        mailMessage.setFrom(webMasterEmail);

        emailService.sendGenericEmailMessage(mailMessage);
    }

    private String createResetPasswordUrl(HttpServletRequest request, User user, String tokenData) {
        return request.getScheme() +
            "://" +
            request.getServerName() +
            ":" +
            request.getServerPort() +
            request.getContextPath() +
            CHANGE_PASSWORD_PATH +
            "?id=" +
            user.getId() +
            "&token=" +
            tokenData;
    }
}
