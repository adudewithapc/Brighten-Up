package thatmartinguy.brightenup.util;

import org.apache.logging.log4j.*;

public class LogHelper
{
    public static final Marker MOD_MARKER = MarkerManager.getMarker(Reference.MOD_ID);
    private static final Logger LOGGER = LogManager.getLogger(Reference.MOD_ID);

    public static void log(Level level, Marker marker, Object object)
    {
        LOGGER.log(level, marker, object);
    }

    public static void fatal(Object object)
    {
        log(Level.FATAL, MOD_MARKER, object);
    }

    public static void error(Object object)
    {
        log(Level.ERROR, MOD_MARKER, object);
    }

    public static void warn(Object object)
    {
        log(Level.WARN, MOD_MARKER, object);
    }

    public static void info(Object object)
    {
        log(Level.INFO, MOD_MARKER, object);
    }

    public static void debug(Object object)
    {
        log(Level.DEBUG, MOD_MARKER, object);
    }

    public static void trace(Object object)
    {
        log(Level.TRACE, MOD_MARKER, object);
    }
}
