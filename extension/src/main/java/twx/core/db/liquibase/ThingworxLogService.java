package twx.core.db.liquibase;

import java.util.Properties;

import com.thingworx.logging.LogUtilities;

import liquibase.logging.Logger;
import liquibase.logging.core.AbstractLogService;
import twx.core.db.DatabaseTS;

public class ThingworxLogService extends AbstractLogService {
    
    private static final int DEFAULT_PRIORITY = 5;
    private static final String PRIORITY_PROPERTY_NAME = ThingworxLogService.class.getName() + ".priority";
    private int priority;

    /**
     * Default constructor is needed for ServiceLoader to work.
     *
     * @see liquibase.servicelocator.StandardServiceLocator
     */
    public ThingworxLogService() {
        this(System.getProperties());
    }

    /**
     * Constructor visible for testing.
     */
    ThingworxLogService(final Properties systemProps) {
        priority = DEFAULT_PRIORITY;
        String priorityPropertyValue = systemProps.getProperty(PRIORITY_PROPERTY_NAME);
        if (priorityPropertyValue != null && !priorityPropertyValue.isEmpty()) {
            try {
                priority = Integer.parseInt(priorityPropertyValue);
            } catch (NumberFormatException e) {
                // Do nothing
            }
        }
    }
    /**
     * Gets the logger priority for this logger. The priority is used by Liquibase to determine which LogService to use.
     * The LogService with the highest priority will be selected. This implementation's priority is set to 5. Remove loggers
     * with higher priority numbers if needed.
     *
     * @return The priority integer. Defaults to 5 if no override is given.
     */
    @Override
    public int getPriority() {
        return priority;
    }
    /**
     * Takes the given class argument and associates it with a SLF4J logger.
     *
     * @param clazz The class to create an SLF4J logger for
     */
    @Override
    public Logger getLog(Class clazz) {
        return new ThingworxLogger( LogUtilities.getInstance().getDatabaseLogger(clazz) ); 
    }

}
