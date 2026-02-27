package dev.sugbo4j.packmule.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Mutable state holding the current project configuration for Pack Mule.
 * Stores all user selections for project generation.
 */
public class ProjectConfig {

    // Project Information
    private String projectName = "";
    private String groupId = "com.mycompany";
    private String outputDirectory = ".";

    // Runtime and JDK Version
    private String muleRuntime = "4.6.0";
    private String javaVersion = "11";
    private String trigger = null;

    // Queue Type (only applicable when trigger = "Messaging / Queue")
    private String queueType = null;

    // Additional Capabilities (list of capability keys)
    private List<String> capabilities = new ArrayList<>();

    // Available options (triggers moved to ProjectTriggerAndCapabilities)
    public static final String[] MULE_RUNTIMES = { "4.9.0", "4.10.0", "4.11.0" };
    public static final String[] JAVA_VERSIONS = { "11", "17" };

    // Getters and Setters
    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public String getMuleRuntime() {
        return muleRuntime;
    }

    public void setMuleRuntime(String muleRuntime) {
        this.muleRuntime = muleRuntime;
    }

    public String getJavaVersion() {
        return javaVersion;
    }

    public void setJavaVersion(String javaVersion) {
        this.javaVersion = javaVersion;
    }

    /**
     * Get the index of the currently selected Mule runtime.
     */
    public int getMuleRuntimeIndex() {
        for (int i = 0; i < MULE_RUNTIMES.length; i++) {
            if (MULE_RUNTIMES[i].equals(muleRuntime)) {
                return i;
            }
        }
        return 0;
    }

    /**
     * Get the index of the currently selected Java version.
     */
    public int getJavaVersionIndex() {
        for (int i = 0; i < JAVA_VERSIONS.length; i++) {
            if (JAVA_VERSIONS[i].equals(javaVersion)) {
                return i;
            }
        }
        return 0;
    }

    /**
     * Set Mule runtime by index.
     */
    public void setMuleRuntimeByIndex(int index) {
        if (index >= 0 && index < MULE_RUNTIMES.length) {
            this.muleRuntime = MULE_RUNTIMES[index];
        }
    }

    /**
     * Set Java version by index.
     */
    public void setJavaVersionByIndex(int index) {
        if (index >= 0 && index < JAVA_VERSIONS.length) {
            this.javaVersion = JAVA_VERSIONS[index];
        }
    }

    public String getTrigger() {
        return trigger;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    /**
     * Get the index of the currently selected Trigger.
     * Returns -1 if no trigger is selected.
     */
    public int getTriggerIndex() {
        return ProjectTriggerAndCapabilities.getTriggerIndex(trigger);
    }

    /**
     * Set Trigger by index.
     */
    public void setTriggerByIndex(int index) {
        if (index >= 0 && index < ProjectTriggerAndCapabilities.TRIGGERS.length) {
            this.trigger = ProjectTriggerAndCapabilities.TRIGGERS[index];
        } else if (index == -1) {
            this.trigger = null;
        }
    }

    // ========== Queue Type ==========

    public String getQueueType() {
        return queueType;
    }

    public void setQueueType(String queueType) {
        this.queueType = queueType;
    }

    /**
     * Get the index of the currently selected Queue Type.
     * Returns -1 if no queue type is selected.
     */
    public int getQueueTypeIndex() {
        return ProjectTriggerAndCapabilities.getQueueTypeIndex(queueType);
    }

    /**
     * Set Queue Type by index.
     */
    public void setQueueTypeByIndex(int index) {
        QueueType[] queueTypes = QueueType.values();
        if (index >= 0 && index < queueTypes.length) {
            this.queueType = queueTypes[index].getDisplayName();
        } else if (index == -1) {
            this.queueType = null;
        }
    }

    /**
     * Check if the current trigger requires a queue type selection.
     */
    public boolean isQueueTypeRequired() {
        return "Messaging / Queue".equals(trigger);
    }

    // ========== Capabilities ==========

    public List<String> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(List<String> capabilities) {
        this.capabilities = capabilities != null ? capabilities : new ArrayList<>();
    }

    /**
     * Toggle a capability in the list.
     */
    public void toggleCapability(String key) {
        if (capabilities.contains(key)) {
            capabilities.remove(key);
        } else {
            capabilities.add(key);
        }
    }

    /**
     * Check if a capability is selected.
     */
    public boolean hasCapability(String key) {
        return capabilities.contains(key);
    }

    /**
     * Check if the trigger implies Anypoint MQ (when queue type is Anypoint MQ).
     */
    public boolean isAnypointMQFromTrigger() {
        return "Anypoint MQ".equals(queueType);
    }
}
