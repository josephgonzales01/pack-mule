package dev.sugbo4j.packmule.model;

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

    // Available options
    public static final String[] MULE_RUNTIMES = { "4.4.0", "4.5.0", "4.6.0" };
    public static final String[] JAVA_VERSIONS = { "8", "11", "17" };

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
}
