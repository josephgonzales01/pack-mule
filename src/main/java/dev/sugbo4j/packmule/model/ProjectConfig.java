package dev.sugbo4j.packmule.model;

import java.util.ArrayList;
import java.util.List;

import dev.sugbo4j.packmule.model.config.CapabilityConfig;
import dev.sugbo4j.packmule.model.config.ConfigurationLoader;
import dev.sugbo4j.packmule.model.config.PackMuleConfig;
import dev.sugbo4j.packmule.model.config.TriggerConfig;

/**
 * Mutable state holding the current project configuration for Pack Mule.
 * Stores all user selections for project generation.
 */
public class ProjectConfig {

    private final PackMuleConfig packMuleConfig;

    // Project Information
    private String projectName = "";
    private String groupId;
    private String outputDirectory = ".";

    // Runtime and JDK Version
    private String muleRuntime;
    private String javaVersion;
    private TriggerConfig trigger = null;

    // Queue Type (only applicable when trigger = "Messaging / Queue")
    private String queueType = null;

    // Additional Capabilities (list of capability keys)
    private List<String> capabilities = new ArrayList<>();

    // Available options loaded from pack-mule.yaml
    private List<String> availableMuleRuntimes = new ArrayList<>();
    private List<String> availableJavaVersions = new ArrayList<>();

    public ProjectConfig() {
        // Load dynamically from yaml
        this.packMuleConfig = ConfigurationLoader.loadFromClasspath("/pack-mule.yaml");
        
        // Defaults
        this.groupId = packMuleConfig.defaults().groupId();
        this.muleRuntime = packMuleConfig.defaults().runtime();
        this.javaVersion = packMuleConfig.defaults().jdk();

        // Load runtimes
        packMuleConfig.runtime().forEach(rt -> availableMuleRuntimes.add(rt.version()));
        
        // Load jdks
        packMuleConfig.jdk().forEach(jdk -> availableJavaVersions.add(jdk.version()));
    }

    public PackMuleConfig getPackMuleConfig() {
        return packMuleConfig;
    }

    public List<String> getAvailableMuleRuntimes() {
        return availableMuleRuntimes;
    }

    public List<String> getAvailableJavaVersions() {
        return availableJavaVersions;
    }

    public List<TriggerConfig> getAvailableTriggers() {
        return packMuleConfig.triggers();
    }

    public List<CapabilityConfig> getAvailableCapabilities() {
        return packMuleConfig.capabilities();
    }

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
        for (int i = 0; i < availableMuleRuntimes.size(); i++) {
            if (availableMuleRuntimes.get(i).equals(muleRuntime)) {
                return i;
            }
        }
        return 0;
    }

    /**
     * Get the index of the currently selected Java version.
     */
    public int getJavaVersionIndex() {
        for (int i = 0; i < availableJavaVersions.size(); i++) {
            if (availableJavaVersions.get(i).equals(javaVersion)) {
                return i;
            }
        }
        return 0;
    }

    /**
     * Set Mule runtime by index.
     */
    public void setMuleRuntimeByIndex(int index) {
        if (index >= 0 && index < availableMuleRuntimes.size()) {
            this.muleRuntime = availableMuleRuntimes.get(index);
        }
    }

    /**
     * Set Java version by index.
     */
    public void setJavaVersionByIndex(int index) {
        if (index >= 0 && index < availableJavaVersions.size()) {
            this.javaVersion = availableJavaVersions.get(index);
        }
    }

    public String getTrigger() {
        return trigger != null ? trigger.id() : null;
    }

    public TriggerConfig getTriggerConfig() {
        return trigger;
    }

    public void setTrigger(TriggerConfig trigger) {
        this.trigger = trigger;
    }

    /**
     * Get the index of the currently selected Trigger.
     * Returns -1 if no trigger is selected.
     */
    public int getTriggerIndex() {
        if (trigger == null) {
            return -1;
        }
        List<TriggerConfig> triggers = getAvailableTriggers();
        for (int i = 0; i < triggers.size(); i++) {
            if (triggers.get(i).id().equals(trigger.id())) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Set Trigger by index.
     */
    public void setTriggerByIndex(int index) {
        List<TriggerConfig> triggers = getAvailableTriggers();
        if (index >= 0 && index < triggers.size()) {
            this.trigger = triggers.get(index);
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
        if (queueType == null) {
            return -1;
        }
        QueueType type = QueueType.fromDisplayName(queueType);
        return type != null ? type.ordinal() : -1;
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
        return "ANYPOINT_MQ".equals(getTrigger());
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
        return "ANYPOINT_MQ".equals(getTrigger());
    }
}
