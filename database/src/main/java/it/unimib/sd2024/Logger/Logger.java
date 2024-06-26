package it.unimib.sd2024.Logger;

public class Logger {
    private final LogLevelType level;

    public Logger(LogLevelType level) {
        this.level = level;
    }

    public void log(LogLevelType level, String message) {
        if (level.getLevel() >= this.level.getLevel()) {
            System.out.println("[" + LogLevelType.fromLevelToString(level.getLevel()) + "] " + message);
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

