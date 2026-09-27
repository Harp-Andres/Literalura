package com.alura.literalura.console;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Console UI adapter: user-facing messages go through SLF4J (Logback in Spring Boot).
 */
@Component
public class ConsolePresenter {

    private static final Logger LOG = LoggerFactory.getLogger(ConsolePresenter.class);

    public void line(String message) {
        LOG.info(message);
    }

    public void line(String format, Object... args) {
        LOG.info(format, args);
    }

    public void error(String message) {
        LOG.error(message);
    }

    public void error(String message, Throwable cause) {
        LOG.error(message, cause);
    }
}
