package dev.sugbo4j.packmule.model;

/**
 * Constants for project triggers and capabilities.
 * Separates configuration options from the ProjectConfig state class.
 *
 * @see QueueType for queue/messaging types
 */
public final class ProjectTriggerAndCapabilities {

    private ProjectTriggerAndCapabilities() {
        // Prevent instantiation
    }

    // ========== TRIGGERS ==========

    public static final String[] TRIGGERS = {
            "HTTP Listener",
            "Scheduler",
            "Salesforce Event",
            "Messaging / Queue"
    };

    // ========== CAPABILITIES ==========

    public static final String[] CAPABILITY_LABELS = {
            "HTTP Request",
            "Database",
            "SFTP / FTP",
            "Salesforce",
            "Email",
            "Anypoint MQ",
            "Object Store",
            "VM Connector",
            "Secure Properties",
            "JSON Logger"
    };

    public static final String[] CAPABILITY_KEYS = {
            "http-request",
            "database",
            "sftp-ftp",
            "salesforce",
            "email",
            "anypoint-mq",
            "object-store",
            "vm",
            "secure-properties",
            "json-logger"
    };

    public static final String[] CAPABILITY_DESCRIPTIONS = {
            "outbound REST / SOAP calls",
            "JDBC — MySQL, PostgreSQL, Oracle, MSSQL",
            "file transfer",
            "Salesforce upsert, query, streaming",
            "SMTP outbound / IMAP inbound",
            "if not already selected as trigger",
            "persistent key-value storage",
            "in-memory flow messaging",
            "encrypted config properties",
            "structured JSON logging utility"
    };

    // ========== HELPER METHODS ==========

    /**
     * Get the index of a queue type. Returns -1 if not found.
     *
     * @deprecated Use {@link QueueType#fromDisplayName(String)} instead
     * @param queueType the queue type display name
     * @return the ordinal index of the queue type, or -1 if not found
     */
    @Deprecated
    public static int getQueueTypeIndex(String queueType) {
        QueueType type = QueueType.fromDisplayName(queueType);
        return type != null ? type.ordinal() : -1;
    }

    /**
     * Get the index of a trigger. Returns -1 if not found.
     */
    public static int getTriggerIndex(String trigger) {
        if (trigger == null) {
            return -1;
        }
        for (int i = 0; i < TRIGGERS.length; i++) {
            if (TRIGGERS[i].equals(trigger)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Get the capability key at the given index.
     */
    public static String getCapabilityKey(int index) {
        if (index >= 0 && index < CAPABILITY_KEYS.length) {
            return CAPABILITY_KEYS[index];
        }
        return null;
    }

    /**
     * Get the capability label for a given key.
     */
    public static String getCapabilityLabel(String key) {
        for (int i = 0; i < CAPABILITY_KEYS.length; i++) {
            if (CAPABILITY_KEYS[i].equals(key)) {
                return CAPABILITY_LABELS[i];
            }
        }
        return null;
    }

    /**
     * Get the capability description for a given key.
     */
    public static String getCapabilityDescription(String key) {
        for (int i = 0; i < CAPABILITY_KEYS.length; i++) {
            if (CAPABILITY_KEYS[i].equals(key)) {
                return CAPABILITY_DESCRIPTIONS[i];
            }
        }
        return null;
    }

    /**
     * Get the index of a capability key. Returns -1 if not found.
     */
    public static int getCapabilityIndex(String key) {
        for (int i = 0; i < CAPABILITY_KEYS.length; i++) {
            if (CAPABILITY_KEYS[i].equals(key)) {
                return i;
            }
        }
        return -1;
    }
}