# Pack Mule — Solution Design & Guide

Pack Mule is a developer productivity tool that brings a fast, interactive project scaffolding experience to MuleSoft development. It solves the problem of enforcing organizational coding standards by allowing developers to select exactly the capabilities their project needs from the start, via a Terminal User Interface (TUI). 

---

## 1. How the Tool Works

Pack Mule is driven entirely by [JMustache](https://github.com/samskivert/jmustache) templates and powered by [TamboUI](https://tamboui.dev). It requires no external `mvn archetype:generate` subprocess or network calls to scaffold projects.

### Architecture Highlights
- **TUI Framework**: TamboUI provides a declarative DSL and TCSS theming for the terminal experience.
- **Template Engine**: JMustache handles logic-less, whitespace-friendly templating that does not conflict with Mule's native `${var}` syntax.
- **Project Scaffolder**: A pure Java component replaces Maven Archetypes to dynamically assemble project trees offline.
- **Configuration**: A single `pack-mule.yaml` file defines the dependency catalog, capabilities, triggers, and default values.

### The Generation Pipeline

```text
User launches PackMuleApp
         │
         ▼
ConfigurationLoader parses pack-mule.yaml into PackMuleConfig
         │
         ▼
User confirms selections on SummaryScreen
         │
         ▼
PackMuleApp.generateProject()
         │
         ├─▶ DependencyResolver.resolveDependencies()
         │         └─▶ Reads required dependencies from catalog for selected triggers and capabilities
         │
         ├─▶ Builds templateContext Map
         │         └─▶ Injects user inputs and resolved dependencies
         │
         └─▶ ProjectScaffolder.scaffold(context, outputDir)
                   │
                   ├─▶ Navigates /templates/base
                   ├─▶ Navigates /templates/triggers/{triggerId}
                   ├─▶ Navigates /templates/capabilities/{capabilityId}
                   │
                   └─▶ For each file found:
                             ├─▶ TemplateRenderer.evaluateExpression(filename)
                             │         └─▶ Resolves dynamic paths (e.g., {{projectName}}-main.xml)
                             │
                             └─▶ TemplateRenderer.renderClasspathTemplate(content)
                                       └─▶ JMustache evaluates variables and writes to target directory
```
*Note: The `DependencyResolver` dynamically parses dependencies directly from `pack-mule.yaml` based on the user's selected capabilities and triggers, injecting the correct Maven coordinates into the Mustache context.*

### State Management & User Inputs
When the user selects their project name, triggers, and capabilities in the TUI, the Java controller collects these answers into a generic `Map<String, Object> context`. 

```java
Map<String, Object> templateContext = new HashMap<>();
templateContext.put("projectName", "customer-order-api");
templateContext.put("groupId", "com.mycompany");
templateContext.put("flowTrigger", "HTTP_LISTENER"); 
templateContext.put("capabilities", List.of("DATABASE")); 
```

This map is passed directly to the JMustache renderer. This decoupling means that once an input field is added to the TUI and tracked in the context map, the template engine can access it instantly without needing intermediate Java classes to be rebuilt.

### Dynamic File Naming and Routing
Template filenames are evaluated dynamically, which controls the output file path.
1. Java scans the selected template directories.
2. It finds a file like: `src/main/mule/{{projectName}}-global-config.xml`
3. It evaluates the filename string through JMustache, outputting: `src/main/mule/customer-order-api-global-config.xml`
4. The file contents are evaluated through JMustache and written to the target directory.

---

## 2. Project & Template Structure

Instead of using a single monolithic project template cluttered with `{{#hasCapability}}` blocks, Pack Mule uses a **Composition over Configuration** directory strategy. Templates are modularized by foundation files, triggers, and capabilities.

### Directory Strategy

```text
templates/
├── base/                                # Essential files included in EVERY project
│   ├── src/main/resources/
│   │   ├── log4j2.xml               
│   │   └── properties/                  
│   │       ├── {{projectName}}-dev.yaml
│   │       └── {{projectName}}-prd.yaml
│   └── src/main/mule/
│       ├── {{projectName}}-main.xml     # Main flow wrapper
│       └── {{projectName}}-error-handler.xml # Universally required error handling
│
├── triggers/                            # Chosen from TUI Trigger selection
│   ├── HTTP_LISTENER/                   # Included if HTTP Listener is selected
│   │   └── src/main/mule/
│   │       └── {{projectName}}-http-trigger.xml 
│   └── KAFKA_CONSUMER/
│
└── capabilities/                        # Chosen from TUI Capability checkboxes
    └── DATABASE/                        
        ├── src/main/resources/
        │   └── sql/
        │       └── init.sql
        └── src/main/mule/
            └── {{projectName}}-db-common-flow.xml
```

If a user selects `Trigger=HTTP_LISTENER` and `Capabilities=[DATABASE]`, the engine deep-copies and renders everything in `templates/base/`, `templates/triggers/HTTP_LISTENER/`, and `templates/capabilities/DATABASE/`.

---

## 3. Organizations: Getting Started & Customization

Organizations can easily adopt and customize Pack Mule without touching the underlying Java codebase. `pack-mule.yaml` acts as the single control plane for generation behavior. 

### Running the Tool
Ensure you have **Java 17+** and a terminal with ANSI support.

```bash
# Run with the default configuration
java -jar target/pack-mule-app.jar

# Run with your organization's custom configuration file
java -jar target/pack-mule-app.jar --config /path/to/org-pack-mule.yml
```

### Updating TUI Data (Runtimes, JDKs, Triggers, Capabilities)
You can directly edit your `pack-mule.yaml` to update tool options.

**Updating Runtimes and JDK versions:**
```yaml
defaults:
  groupId: "com.mycompany"
  runtime: "4.6.0"
  jdk: "17"

runtime:
  - version: "4.11.0"
  - version: "4.9.0"
  - version: "4.6.0"

jdk:
  - version: 8
  - version: 11
  - version: 17
```

**Adding Triggers:**
Triggers map to Radio buttons in the TUI, corresponding to folders inside `templates/triggers/`.
```yaml
triggers:
  - id: HTTP_LISTENER
    label: "HTTP Listener (RESTful API)"
    dependencies: ["apikit", "http-connector"] # Pulled from the dependency catalog
```

**Adding Capabilities:**
Capabilities map to Checkboxes in the TUI and pull files from `templates/capabilities/`.
```yaml
capabilities:
  - id: DATABASE
    label: "Database (JDBC)"
    category: "Connector"
    dependencies: ["db-connector"]
```

### Adding Custom Templates
To introduce a new capability or trigger:
1. Create a folder matching the ID in `pack-mule.yaml` under the appropriate directory, e.g., `templates/capabilities/WEBSOCKET/`.
2. Add your `.xml` or `.yaml` snippet files. Ensure you utilize JMustache variables like `{{projectName}}`, `{{groupId}}`, and `{{muleVersion}}` so the templates adapt dynamically.
3. Ensure the capability or trigger ID is registered in `pack-mule.yaml`.
4. Register any necessary dependencies to the Dependency Catalog.

### Configuring the Dependency Catalog
The dependency catalog tells Pack Mule how to populate the `<dependencies>` section of the generated `pom.xml`. Operators can version-bump connectors for the whole organization in one place.

```yaml
dependencies:
  db-connector:
    groupId: "org.mule.connectors"
    artifactId: "mule-db-connector"
    version: "1.14.7"
    classifier: "mule-plugin"
```

---

## 4. Contributors: Developer Guide

If you'd like to contribute to the core Pack Mule engine (UI improvements, bug fixes, or entirely new features), here is what you need to know.

### Prerequisites for Building
- **Java 17+**
- **Apache Maven 3.8+** on `PATH`
- **MuleSoft Nexus credentials** in `~/.m2/settings.xml`. This is required because Pack Mule's integration tests (`mvn verify -Pintegration-tests`) scaffold projects and compiles them to ensure the templates work correctly. Maven needs your credentials to download them from MuleSoft's proprietary Nexus repository during the test phase.

### Install & Build Locally
```bash
git clone https://github.com/josephgonzales01/pack-mule.git
cd pack-mule
mvn clean package -DskipTests
```
This produces a fat JAR at `target/pack-mule-app.jar`.

### Compiling to Native Binary (GraalVM)
For zero JVM startup time, compile the application into a native binary:
```bash
mvn -Pnative package
./target/pack-mule    
```

### Running Tests
All logic for parsing configurations, evaluating templates, and resolving dependencies is heavily tested. Ensure tests pass before submitting a Pull Request:
```bash
mvn test                        # Run unit tests
mvn verify -Pintegration-tests  # End-to-end template generation tests
```

### Contribution Guidelines
- **Open an issue**: Please discuss significant architectural changes or large features via a GitHub issue first.
- **TamboUI**: Note that the TUI framework used, TamboUI (`0.2.0-SNAPSHOT`), is under active development. Keep an eye on the [TamboUI changelog](https://github.com/tamboui/tamboui) if you are upgrading BOM versions.
- **Templates**: New capabilities must be accompanied by appropriate JMustache template files demonstrating their use. Dependency coordinates must be pinned to a specific version in `pack-mule.yml`. 
- **License**: Pack Mule is provided under the Apache License 2.0.
