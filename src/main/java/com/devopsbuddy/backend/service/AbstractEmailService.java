package com.devopsbuddy.backend.service;

import com.devopsbuddy.web.domain.frontend.FeedbackHolder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;

public abstract class AbstractEmailService implements EmailService {
    @Value("${default.to.address}")
    private String defaultToAddress;

    /**
     * Prepares {@link SimpleMailMessage} from {@link FeedbackHolder}
     * @param feedback Feedback object
     * @return Prepared {@link SimpleMailMessage}
     */
    private SimpleMailMessage prepareSimpleMailMessage(FeedbackHolder feedback) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(defaultToAddress);
        message.setFrom(feedback.getEmail());
        message.setSubject("Feedback received from " + feedback.getFirstName() + " " + feedback.getLastName());
        message.setText(feedback.getFeedback());
        return message;
    }

    @Override
    public void sendFeedbackEmail(FeedbackHolder feedback) {
        sendGenericEmailMessage(prepareSimpleMailMessage(feedback));
    }
}
