package it.unimib.sd2024.Logger;

public enum LogLevelType {
    DEBUG,
    INFO,
    WARNING,
    ERROR;

    public static LogLevelType fromLevel(int level) {
        return LogLevelType.values()[level];
    }

    public static String fromLevelToString(int level) {
        return LogLevelType.values()[level].toString();
    }

    public int getLevel() {
        return this.ordinal();
    }
}
