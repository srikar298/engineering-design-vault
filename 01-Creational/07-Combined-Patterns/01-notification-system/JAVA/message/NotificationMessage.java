package message;

/**
 * <h1>NotificationMessage — Builder Layer</h1>
 *
 * <p>A complex value object that carries all data needed to send a notification.
 * Fields are optional (subject, template, priority) — making Builder the
 * right tool instead of a telescoping constructor.
 *
 * <p><b>Pattern:</b> Builder (via static inner class)
 */
public final class NotificationMessage {

    // Required
    private final String recipient;
    private final String body;

    // Optional
    private final String subject;
    private final String templateId;
    private final Priority priority;
    private final String correlationId;

    public enum Priority { LOW, NORMAL, HIGH, CRITICAL }

    // Private constructor — only the Builder can create this
    private NotificationMessage(Builder builder) {
        this.recipient     = builder.recipient;
        this.body          = builder.body;
        this.subject       = builder.subject;
        this.templateId    = builder.templateId;
        this.priority      = builder.priority;
        this.correlationId = builder.correlationId;
    }

    // --- Accessors ---
    public String   getRecipient()     { return recipient; }
    public String   getBody()          { return body; }
    public String   getSubject()       { return subject; }
    public String   getTemplateId()    { return templateId; }
    public Priority getPriority()      { return priority; }
    public String   getCorrelationId() { return correlationId; }

    @Override
    public String toString() {
        return String.format(
            "NotificationMessage{to='%s', subject='%s', priority=%s, body='%s'}",
            recipient, subject, priority, body
        );
    }

    // =====================================================================
    // Inner Builder
    // =====================================================================
    public static final class Builder {

        // Required fields
        private final String recipient;
        private final String body;

        // Optional fields with defaults
        private String   subject       = "(No Subject)";
        private String   templateId    = null;
        private Priority priority      = Priority.NORMAL;
        private String   correlationId = null;

        public Builder(String recipient, String body) {
            if (recipient == null || recipient.isBlank())
                throw new IllegalArgumentException("recipient must not be blank");
            if (body == null || body.isBlank())
                throw new IllegalArgumentException("body must not be blank");
            this.recipient = recipient;
            this.body      = body;
        }

        public Builder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder templateId(String templateId) {
            this.templateId = templateId;
            return this;
        }

        public Builder priority(Priority priority) {
            this.priority = priority;
            return this;
        }

        public Builder correlationId(String correlationId) {
            this.correlationId = correlationId;
            return this;
        }

        public NotificationMessage build() {
            return new NotificationMessage(this);
        }
    }
}
