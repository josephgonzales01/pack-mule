package dev.sugbo4j.packmule.model;

/**
 * Enum representing different queue/messaging types with their descriptions.
 * Used for project configuration when "Messaging / Queue" trigger is selected.
 */
public enum QueueType {

    JMS("IBM MQ, ActiveMQ — standard JMS protocol"),
    ANYPOINT_MQ("MuleSoft native cloud messaging"),
    AMQP("RabbitMQ — Advanced Message Queuing Protocol"),
    KAFKA("Apache Kafka — event streaming"),
    SOLACE("Solace PubSub+ event broker"),
    AZURE_SERVICE_BUS("Azure Service Bus — cloud messaging service"),
    AMAZON_SQS("Amazon SQS — Simple Queue Service");

    private final String description;

    QueueType(String description) {
        this.description = description;
    }

    /**
     * Get the description of this queue type.
     * 
     * @return the description of the queue type
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the display name of this queue type.
     * This is the name shown in UI components.
     * 
     * @return the display name
     */
    public String getDisplayName() {
        return name().replace("_", " ");
    }

    /**
     * Find a QueueType by its display name (case-insensitive).
     * 
     * @param displayName the display name to search for
     * @return the matching QueueType, or null if not found
     */
    public static QueueType fromDisplayName(String displayName) {
        if (displayName == null) {
            return null;
        }
        String normalized = displayName.toUpperCase().replace(" ", "_");
        for (QueueType type : values()) {
            if (type.name().equals(normalized)) {
                return type;
            }
        }
        return null;
    }

    /**
     * Get all queue type display names as an array.
     * Useful for UI selection components.
     * 
     * @return array of display names
     */
    public static String[] getDisplayNames() {
        QueueType[] types = values();
        String[] names = new String[types.length];
        for (int i = 0; i < types.length; i++) {
            names[i] = types[i].getDisplayName();
        }
        return names;
    }

    /**
     * Get all descriptions as an array.
     * Index corresponds to the ordinal position in the enum.
     * 
     * @return array of descriptions
     */
    public static String[] getDescriptions() {
        QueueType[] types = values();
        String[] descriptions = new String[types.length];
        for (int i = 0; i < types.length; i++) {
            descriptions[i] = types[i].getDescription();
        }
        return descriptions;
    }
}
