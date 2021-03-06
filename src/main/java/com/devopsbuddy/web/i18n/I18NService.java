package com.devopsbuddy.web.i18n;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class I18NService {
    private static final Logger logger = LoggerFactory.getLogger(I18NService.class);

    private final MessageSource messageSource;

    @Autowired
    public I18NService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMessage(String messageId) {
        logger.info("Returning i18n message for messageId {}", messageId);
        Locale locale = LocaleContextHolder.getLocale();
        return getMessage(messageId, locale);
    }

    private String getMessage(String messageId, Locale locale) {
        return messageSource.getMessage(messageId, null, locale);
    }
}
