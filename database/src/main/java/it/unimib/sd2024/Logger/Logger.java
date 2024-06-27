package it.unimib.sd2024.Logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Logger {
    private final LogLevelType level;
    private final boolean withTimestamp;
    private final TimeZone tz;
    private final DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");

    public Logger(LogLevelType level) {
        this(level, true, TimeZone.getDefault());
    }

    public Logger(LogLevelType level, boolean withTimestamp, TimeZone tz) {
        this.level = level;
        this.withTimestamp = withTimestamp;
        this.tz = tz;
        df.setTimeZone(this.tz);
    }

    public void log(LogLevelType level, String message) {
        if (level.getLevel() >= this.level.getLevel()) {
            if (withTimestamp)
                System.out.println(LogLevelType.fromLevelToString(level.getLevel()) + "[" + df.format(new Date()) + "] " + message);
            else
                System.out.println(LogLevelType.fromLevelToString(level.getLevel()) + "[0000]" + message);
        }
    }

    public void Debug(String message) {
        log(LogLevelType.DEBUG, message);
    }

    public void Info(String message) {
        log(LogLevelType.INFO, message);
    }

    public void Warning(String message) {
        log(LogLevelType.WARNING, message);
    }

    public void Error(String message) {
        log(LogLevelType.ERROR, message);
    }
}

