package it.unimib.sd2024.QueryBuilder.V1;

import jakarta.json.bind.JsonbBuilder;

public class UpdateDefinition {
    private UpdateContent content;
    private UpdateDefinition next;
    private UpdateDefinition before;

    public UpdateDefinition() {
    }

    public UpdateDefinition add(String key, Object value) {
        this.content = new UpdateContent(key, value);
        this.next = new UpdateDefinition();
        this.next.before = this;
        return this.next;
    }

    public UpdateDefinition append(UpdateDefinition definition) {
        UpdateDefinition current = this;
        while (current.next != null) {
            current = current.next;
        }
        current.next = definition;
        definition.before = current;
        return this;
    }

    private UpdateContent getUpdateContent() {
        return this.content;
    }

    public String build() {
        StringBuilder sb = new StringBuilder();
        UpdateDefinition current = this;
        if (this.content == null) {
            if (this.before == null) {
                throw new IllegalArgumentException("Update content must not be null");
            } else {
                current = this.before;
            }
        }

        do {
            sb.append(JsonbBuilder.create().toJson(current.getUpdateContent(), UpdateContent.class));
            current = current.before;
            if (current != null && current.next != null) {
                sb.append(",");
            }
        } while (current != null);
        return "[" + sb.toString() + "]";
    }

}
