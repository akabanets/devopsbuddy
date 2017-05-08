package com.devopsbuddy.backend.service;

import com.devopsbuddy.web.domain.frontend.FeedbackHolder;
import org.springframework.mail.SimpleMailMessage;

public interface EmailService {
    /**
     * Send feedback email
     * @param feedback
     */
    void sendFeedbackEmail(FeedbackHolder feedback);

    /**
     * Send a generic email
     * @param message
     */
    void sendGenericEmailMessage(SimpleMailMessage message);
}
