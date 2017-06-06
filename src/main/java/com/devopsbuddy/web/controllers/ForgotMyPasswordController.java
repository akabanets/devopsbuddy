package com.devopsbuddy.web.controllers;

import com.devopsbuddy.backend.persistence.domain.backend.PasswordResetToken;
import com.devopsbuddy.backend.persistence.domain.backend.User;
import com.devopsbuddy.backend.persistence.repositories.PasswordResetTokenRepository;
import com.devopsbuddy.backend.persistence.repositories.UserRepository;
import com.devopsbuddy.backend.service.EmailService;
import com.devopsbuddy.backend.service.UserService;
import com.devopsbuddy.web.i18n.I18NService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    private static final String FORGOTMYPASSWORD_VIEW = "forgotmypassword/emailForm";
    private static final String ENTERNEWPASSWORD_VIEW = "forgotmypassword/enternewpassword";
    private static final String WRONGTOKENDATA_VIEW = "forgotmypassword/wrongtokendata";
    private static final String PASSWORDUPDATED_VIEW = "forgotmypassword/passwordupdated";

    private final static String ENTER_EMAIL_TO_CREATE_TOKEN_PATH = "/forgotmypassword";
    private final static String VERIFY_TOKEN_PATH = "/changeuserpassword/enternewpassword";
    private static final String UPDATE_PASSWORD_PATH = "/changeuserpassword/updatepassword";

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

    @Autowired
    private UserService userService;

    @Value("${webmaster.email}")
    private String webMasterEmail;

    @RequestMapping(value = ENTER_EMAIL_TO_CREATE_TOKEN_PATH, method = RequestMethod.GET)
    public String prepareEnterEmailForm() {
        return FORGOTMYPASSWORD_VIEW;
    }

    @RequestMapping(value = ENTER_EMAIL_TO_CREATE_TOKEN_PATH, method = RequestMethod.POST)
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

        return FORGOTMYPASSWORD_VIEW;
    }

    @RequestMapping(value = VERIFY_TOKEN_PATH)
    public String processLinkWithToken(@ModelAttribute("id") String userId,
                                       @ModelAttribute("token") String tokenStr,
                                       ModelMap model) {
        try {
            if (!verifyToken(userId, tokenStr))
                return WRONGTOKENDATA_VIEW;
        } catch (Exception e) {
            return WRONGTOKENDATA_VIEW;
        }

        return ENTERNEWPASSWORD_VIEW;
    }

    @RequestMapping(value = UPDATE_PASSWORD_PATH)
    public String updatepassword(@ModelAttribute("id") String userId,
                                 @ModelAttribute("token") String tokenStr,
                                 @ModelAttribute("password1") String password1,
                                 @ModelAttribute("password2") String password2,
                                 ModelMap model) {
        try {
            if (password1.equals("") || !verifyToken(userId, tokenStr)) {
                model.addAttribute("wrongData", true);
                return ENTERNEWPASSWORD_VIEW;
            }
            if (!password1.equals(password2)) {
                model.addAttribute("notSamePassword", true);
                return ENTERNEWPASSWORD_VIEW;
            }

            userService.changePassword(Long.parseLong(userId), password1);

            autoAuthenticateUser(Long.parseLong(userId));

            return PASSWORDUPDATED_VIEW;
        } catch (Exception e) {
            model.addAttribute("wrongData", true);
            return ENTERNEWPASSWORD_VIEW;
        }
    }

    private void autoAuthenticateUser(long userId) {
        User user = userRepository.findOne(userId);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }


//--------------- Private

    private boolean verifyToken(String userId, String tokenStr) {
        PasswordResetToken token = passwordResetTokenRepository.findByToken(tokenStr);
        return token.getUser().getId() == Long.parseLong(userId) && token.getToken().equals(tokenStr);
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
            VERIFY_TOKEN_PATH +
            "?id=" +
            user.getId() +
            "&token=" +
            tokenData;
    }
}
