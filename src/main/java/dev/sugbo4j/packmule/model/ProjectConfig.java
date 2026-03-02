package dev.sugbo4j.packmule.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.InputStream;
import org.yaml.snakeyaml.Yaml;

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

    // Available options loaded from pack-mule.yaml
    private List<String> availableMuleRuntimes = new ArrayList<>();
    private List<String> availableJavaVersions = new ArrayList<>();

    public ProjectConfig() {
        loadDefaultsFromYaml();
    }

    @SuppressWarnings("unchecked")
    private void loadDefaultsFromYaml() {
        try (InputStream inputStream = getClass().getResourceAsStream("/pack-mule.yaml")) {
            //  fallback to hardcoded values if pack-mule.yaml is not found
            if (inputStream == null) {
                System.err.println("Warning: pack-mule.yaml not found on classpath. Using hardcoded fallbacks.");
                // Fallbacks
                availableMuleRuntimes.addAll(List.of("4.9.0", "4.10.0", "4.11.0"));
                availableJavaVersions.addAll(List.of("11", "17"));
                return;
            }

            Yaml yaml = new Yaml();
            Map<String, Object> config = yaml.load(inputStream);

            // Load defaults
            if (config.containsKey("defaults")) {
                Map<String, Object> defaults = (Map<String, Object>) config.get("defaults");
                this.groupId = (String) defaults.getOrDefault("groupId", "com.mycompany");
                this.muleRuntime = (String) defaults.getOrDefault("runtime", "4.6.0");
                this.javaVersion = String.valueOf(defaults.getOrDefault("jdk", "17"));
            }

            // Load runtimes list
            if (config.containsKey("runtime")) {
                List<Map<String, Object>> runtimes = (List<Map<String, Object>>) config.get("runtime");
                for (Map<String, Object> rt : runtimes) {
                    availableMuleRuntimes.add((String) rt.get("version"));
                }
            }

            // Load jdk list
            if (config.containsKey("jdk")) {
                List<Map<String, Object>> jdks = (List<Map<String, Object>>) config.get("jdk");
                for (Map<String, Object> jdk : jdks) {
                    availableJavaVersions.add(String.valueOf(jdk.get("version")));
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading pack-mule.yaml defaults: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<String> getAvailableMuleRuntimes() {
        return availableMuleRuntimes;
    }

    public List<String> getAvailableJavaVersions() {
        return availableJavaVersions;
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
