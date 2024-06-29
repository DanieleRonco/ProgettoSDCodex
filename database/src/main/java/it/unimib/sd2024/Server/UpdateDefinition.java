package it.unimib.sd2024.Server;

public class UpdateDefinition {
    private String key;
    private String value;

    public UpdateDefinition() {
    }

    public UpdateDefinition(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "UpdateDefinition{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    public String toSaveString() {
        return key + "=[REDACTED]";
    }
}
