
package ru.peacepie.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PeacepieLogger {

    private final Logger logger;

    public PeacepieLogger(Class<?> clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
    }

    // INFO

    public void info(String message) {
        logger.info(message);
    }

    public void info(String message, Throwable throwable) {
        logger.info(message, throwable);
    }

    // WARN

    public void warn(String message) {
        logger.warn(message);
    }

    public void warn(String message, Throwable throwable) {
        logger.warn(message, throwable);
    }

    // ERROR

    public void error(String message) {
        logger.error(message);
    }

    public void error(String message, Throwable throwable) {
        logger.error(message, throwable);
    }

    // DEBUG

    public void debug(String message) {
        logger.debug(message);
    }

    public void debug(String message, Throwable throwable) {
        logger.debug(message, throwable);
    }

    // TRACE

    public void trace(String message) {
        logger.trace(message);
    }

    public void trace(String message, Throwable throwable) {
        logger.trace(message, throwable);
    }

    // LEVEL CHECKS

    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }

    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }

    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }
}
