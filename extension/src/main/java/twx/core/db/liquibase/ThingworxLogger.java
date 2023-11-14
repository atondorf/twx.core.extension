package twx.core.db.liquibase;

import java.util.logging.Level;

import org.slf4j.Logger;

import liquibase.logging.core.AbstractLogger;

public class ThingworxLogger extends AbstractLogger {

    private static final int TRACE_THRESHOLD = Level.FINEST.intValue();
    private static final int DEBUG_THRESHOLD = Level.FINE.intValue();
    private static final int INFO_THRESHOLD = Level.INFO.intValue();
    private static final int WARN_THRESHOLD = Level.WARNING.intValue();

    private final Logger logger;

    ThingworxLogger(Logger logger) {
        super();
        this.logger = logger;
    }

    @Override
    public void log(Level level, String message, Throwable e) {
        int levelValue = level.intValue();
        if (levelValue <= TRACE_THRESHOLD) {
            logger.trace(message, e);
        } else if (levelValue <= DEBUG_THRESHOLD) {
            logger.debug(message, e);
        } else if (levelValue <= INFO_THRESHOLD) {
            logger.info(message, e);
        } else if (levelValue <= WARN_THRESHOLD) {
            logger.warn(message, e);
        } else {
            logger.error(message, e);
        }
    }

    /**
     * Logs an severe message. Calls SLF4J {@link Logger#error(String)}.
     *
     * @param message
     *            The message to log.
     */
    @Override
    public void severe(String message) {
        if (logger.isErrorEnabled()) {
            logger.error(message);
        }
    }

    /**
     * Logs a severe message. Calls SLF4J {@link Logger#error(String, Throwable)}.
     *
     * @param message
     *            The message to log
     * @param e
     *            The exception to log.
     */
    @Override
    public void severe(String message, Throwable e) {
        if (logger.isErrorEnabled()) {
            logger.error(message, e);
        }
    }

    /**
     * Logs a warning message. Calls SLF4J {@link Logger#warn(String)}
     *
     * @param message
     *            The message to log.
     */
    @Override
    public void warning(String message) {
        if (logger.isWarnEnabled()) {
            logger.warn(message);
        }
    }

    /**
     * Logs a warning message. Calls SLF4J {@link Logger#warn(String, Throwable)}.
     *
     * @param message
     *            The message to log.
     * @param e
     *            The exception to log.
     */
    @Override
    public void warning(String message, Throwable e) {
        if (logger.isWarnEnabled()) {
            logger.warn(message, e);
        }
    }

    /**
     * Log an info message. Calls SLF4J {@link Logger#info(String)}.
     *
     * @param message
     *            The message to log.
     */
    @Override
    public void info(String message) {
        if (logger.isInfoEnabled()) {
            logger.info(message);
        }
    }

    /**
     * Log an info message. Calls SLF4J {@link Logger#info(String, Throwable)}.
     *
     * @param message
     *            The message to log.
     * @param e
     *            The exception to log.
     */
    @Override
    public void info(String message, Throwable e) {
        if (logger.isInfoEnabled()) {
            logger.info(message, e);
        }
    }

    /**
     * Log a config message. Calls SLF4J {@link Logger#info(String)}.
     *
     * @param message
     *            The message to log.
     */
    @Override
    public void config(String message) {
        if (logger.isInfoEnabled()) {
            logger.info(message);
        }
    }

    /**
     * Log a config message. Calls SLF4J {@link Logger#info(String, Throwable)}.
     *
     * @param message
     *            The message to log
     * @param e
     *            The exception to log
     */
    @Override
    public void config(String message, Throwable e) {
        if (logger.isInfoEnabled()) {
            logger.info(message, e);
        }
    }

    /**
     * Log a fine message. Calls SLF4J {@link Logger#debug(String)}.
     *
     * @param message
     *            The message to log.
     */
    @Override
    public void fine(String message) {
        if (logger.isDebugEnabled()) {
            logger.debug(message);
        }
    }

    /**
     * Log a fine message. Calls SLF4J {@link Logger#debug(String, Throwable)}.
     *
     * @param message
     *            The message to log.
     * @param e
     *            The exception to log.
     */
    @Override
    public void fine(String message, Throwable e) {
        if (logger.isDebugEnabled()) {
            logger.debug(message, e);
        }
    }
}
