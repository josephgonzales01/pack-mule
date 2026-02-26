# Pack Mule — MuleSoft Project Initializer TUI

> A terminal user interface (TUI) for scaffolding MuleSoft Anypoint projects — inspired by Spring Initializr, powered by [TamboUI](https://tamboui.dev), and driven entirely by FreeMarker templates.

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Configuration Reference](#configuration-reference)
- [Project Types & Auto-Dependencies](#project-types--auto-dependencies)
- [Adding Custom Templates](#adding-custom-templates)
- [Getting Started](#getting-started)
- [Building & Running](#building--running)
- [Roadmap](#roadmap)

---

## Overview

Pack Mule is a developer productivity tool that brings the Spring Initializr experience to MuleSoft development. Instead of manually copying boilerplate XML flows and hunting for connector dependency coordinates, you launch a TUI in your terminal, answer a few questions, and get a fully scaffolded, ready-to-import Anypoint Studio project in seconds.

```
╔══════════════════════════════════════════════════════╗
║            🔧  Pack Mule — Project Initializer       ║
╠══════════════════════════════════════════════════════╣
║  Project Name  : [customer-order-api              ]  ║
║  Group ID      : [com.acme                        ]  ║
║  Mule Runtime  : [ 4.6.0 ▼]                         ║
║  Project Type  : [ RESTful API ▼]                    ║
║                                                      ║
║  Capabilities (space to toggle):                     ║
║  [x] HTTP Listener      [ ] Database (JDBC)          ║
║  [x] APIkit Router      [x] Error Handling           ║
║  [ ] Salesforce         [ ] File / FTP               ║
║  [ ] Kafka / AMQ        [ ] Scheduler / Batch        ║
║                                                      ║
║          [ Generate Project ]   [ Cancel ]           ║
╚══════════════════════════════════════════════════════╝
```

All project files — `pom.xml`, Mule XML flows, `log4j2.xml`, and property placeholders — are generated from [FreeMarker](https://freemarker.apache.org/) templates bundled inside the application. Generation is fast, fully offline, and requires no external tooling to scaffold.

---

## Features

- **Interactive TUI** — keyboard-driven navigation using TamboUI's Toolkit DSL; declarative, component-based screens with automatic focus management and CSS theming via TCSS files.
- **Spring Initializr-style capability selector** — toggle connectors and modules; the tool automatically injects the correct Maven dependency blocks into the generated `pom.xml`.
- **Pluggable project types** — RESTful API, Message Subscriber/Publisher, Batch/File Processing, Scheduled Jobs, and more.
- **Pure FreeMarker generation** — all output files are rendered from `.ftl` templates; no external `mvn archetype:generate` subprocess, no network calls, no Maven daemon required to scaffold.
- **Configurable via YAML** — a single `pack-mule.yml` controls runtime versions, XML file naming conventions, dependency catalogs, and default values.
- **Separated template layer** — Mule XML flow templates live in their own module, completely decoupled from application logic.
- **GraalVM native image ready** — compile Pack Mule to a native binary for instant startup with no JVM warm-up.
- **Post-generation hooks** — run optional shell scripts or Java hooks after generation (e.g., `git init`, copy shared config files).

---

## Architecture

```
┌──────────────────────────────────────────────────────────────────┐
│                        Pack Mule TUI App                         │
│                                                                  │
│  ┌──────────────────────┐   ┌───────────────┐   ┌────────────┐  │
│  │     TUI Layer        │   │  Core Engine  │   │  Template  │  │
│  │  (TamboUI Toolkit)   │──▶ (Orchestrate) ──▶ │   Layer    │  │
│  │                      │   │               │   │(FreeMarker)│  │
│  │ - ToolkitApp screens │   │ - UserSelection │   │            │  │
│  │ - Element DSL panels │   │ - DepResolver │   │ - Flow XML │  │
│  │ - TCSS theming       │   │ - Scaffolder  │   │ - pom.xml  │  │
│  │ - Focus management   │   │ - ConfigLoader│   │ - log4j2   │  │
│  │ - Inline Display     │   │ - HookRunner  │   │ - props    │  │
│  └──────────────────────┘   └───────────────┘   └────────────┘  │
│                                    │                             │
│                    ┌───────────────┴──────────────┐             │
│                    │       pack-mule.yml           │             │
│                    │  (runtimes, types, deps,      │             │
│                    │   file naming, hooks)         │             │
│                    └──────────────────────────────┘              │
└──────────────────────────────────────────────────────────────────┘
```

### Key Architectural Decisions

| Concern | Decision | Rationale |
|---|---|---|
| TUI framework | TamboUI (`tamboui-toolkit` + `tamboui-jline`) | Declarative DSL, TCSS theming, GraalVM native support |
| Template engine | FreeMarker | Mature, whitespace-safe XML rendering with full conditional and loop support |
| Project scaffolding | `ProjectScaffolder` (pure Java) | Replaces Maven Archetype; in-process, offline, testable |
| Configuration | SnakeYAML | Human-friendly, no code changes needed to update connector versions |
| Dependency catalog | Declared in `pack-mule.yml` | Operators can add or version-bump connectors without touching Java |
| Distribution | Fat JAR + optional GraalVM native | Fat JAR for simplicity; native binary for CI/CD agent use |

---

## Project Structure

```
pack-mule/
│
├── src/main/java/dev/sugbo4j/packmule/      # Main application source
│   ├── PackMuleApp.java             # Entry point — bootstraps TamboUI ToolkitApp
│   ├── config/
│   │   ├── PackMuleConfig.java      # Root config POJO (maps pack-mule.yml)
│   │   └── ConfigLoader.java        # Loads & validates YAML config at startup
│   ├── model/
│   │   ├── UserSelection.java       # Captures all user selections
│   │   ├── ProjectType.java         # Enum: REST_API, MESSAGING, BATCH, etc.
│   │   └── Capability.java          # Represents a toggleable module/connector
│   ├── engine/
│   │   ├── ProjectGenerator.java    # Orchestrates the full generation pipeline
│   │   ├── ProjectScaffolder.java   # Creates directory tree, writes rendered files
│   │   ├── TemplateRenderer.java    # FreeMarker: resolves + renders .ftl → file
│   │   ├── DependencyResolver.java  # Maps selected capabilities → dep objects
│   │   └── PostHookRunner.java      # Runs optional post-generation shell/Java hooks
│   └── tui/
│       ├── PackMuleToolkitApp.java  # Root ToolkitApp — screen routing & state      
│       ├── ProjectInfoScreen.java   # Name, groupId, runtime version inputs
│       ├── CapabilityScreen.java    # Checkbox list for connectors/modules
│       ├── SummaryScreen.java       # Review all selections before generate
│       ├── ProgressScreen.java      # Inline Display — live scaffolding output
│       └── theme/
│           └── pack-mule.tcss           # TamboUI CSS theme (colors, borders)
│
├── src/main/resources/              # Application resources
│   ├── pack-mule.yml                # ← Primary configuration file
│   └── pack-mule.tcss               # TamboUI CSS theme
│
├── templates/                        # FreeMarker templates — separated from app logic
│   └── src/main/resources/templates/
│       ├── rest-api/
│       │   ├── main-flow.xml.ftl
│       │   ├── error-handler.xml.ftl
│       │   └── api-console.xml.ftl
│       ├── messaging/
│       │   ├── subscriber-flow.xml.ftl
│       │   └── publisher-flow.xml.ftl
│       ├── batch/
│       │   └── batch-job.xml.ftl
│       ├── scheduled/
│       │   └── scheduler-flow.xml.ftl
│       ├── common/
│       │   ├── global-config.xml.ftl
│       │   └── log4j2.xml.ftl
│       └── pom/
│           └── pom.xml.ftl           # Full pom with conditional dependency blocks
│
├── integration-test/
│   └── src/test/java/com/pack-mule/
│       └── GenerationIntegrationTest.java
│
├── pom.xml                           # Parent Maven POM (multi-module)
└── README.md
```

### Generation Pipeline

```
User confirms on SummaryScreen
         │
         ▼
ProjectGenerator.generate(UserSelection)
         │
         ├─▶ DependencyResolver.resolve(capabilities)
         │         └─▶ returns List<Dependency> from pack-mule.yml catalog
         │
         ├─▶ TemplateRenderer.render("pom/pom.xml.ftl", model)
         │         └─▶ FreeMarker writes pom.xml with only selected <dependency> blocks
         │
         ├─▶ TemplateRenderer.render("{projectType}/*.xml.ftl", model)
         │         └─▶ FreeMarker writes Mule flow XML files
         │
         ├─▶ ProjectScaffolder.scaffold(spec, renderedFiles)
         │         └─▶ Creates target directory tree, writes all rendered files
         │              with names resolved from pack-mule.yml fileNaming rules
         │
         └─▶ PostHookRunner.run(spec)
                   └─▶ Optional shell hooks (git init, etc.)
```


## Configuration Reference

`src/main/resources/pack-mule.yml` is the single control plane for all generation behavior.

```yaml
# ─────────────────────────────────────────────
# Pack Mule Configuration,
# supported versions and default selections
# ─────────────────────────────────────────────

defaults:
  groupId: "com.mycompany"
  runtime: "4.6.0"  
  jdk: "17"
  outputDirectory: "."

runtime:
  - version: "4.11.0"
  - version: "4.9.0"
  - version: "4.6.0"

jdk:
  - version: 8   
  - version: 11    
  - version: 17

# ─────────────────────────────────────────────
# XML file naming conventions
# Supported tokens: {projectName}, {type}
# ─────────────────────────────────────────────
fileNaming:
  mainFlow: "{projectName}-main.xml"
  errorHandler: "{projectName}-error-handler.xml"
  globalConfig: "global-config.xml"
  apiConsole: "{projectName}-api-console.xml"
  log4j2: "log4j2.xml"

# ─────────────────────────────────────────────
# Project types → template folder mapping
# ─────────────────────────────────────────────
projectTypes:
  - id: REST_API
    label: "RESTful API"
    templateDir: "rest-api"
    defaultCapabilities: [HTTP_LISTENER, APIKIT, ERROR_HANDLING]

  - id: MESSAGING
    label: "Message Subscriber / Publisher"
    templateDir: "messaging"
    defaultCapabilities: [ANYPOINT_MQ, ERROR_HANDLING]

  - id: BATCH
    label: "Batch / File Processing"
    templateDir: "batch"
    defaultCapabilities: [FILE_CONNECTOR, BATCH_MODULE, ERROR_HANDLING]

  - id: SCHEDULED
    label: "Scheduled Job"
    templateDir: "scheduled"
    defaultCapabilities: [SCHEDULER, ERROR_HANDLING]

# ─────────────────────────────────────────────
# Capability → Maven dependency mapping
# Add new connectors here
# ─────────────────────────────────────────────
capabilities:
  - id: HTTP_LISTENER
    label: "HTTP Listener"
    category: "Core"
    dependencies:
      - groupId: "org.mule.connectors"
        artifactId: "mule-http-connector"
        version: "1.9.2"
        classifier: "mule-plugin"

  - id: APIKIT
    label: "APIkit Router"
    category: "Core"
    dependencies:
      - groupId: "org.mule.modules"
        artifactId: "mule-apikit-module"
        version: "1.9.0"
        classifier: "mule-plugin"

  - id: DATABASE
    label: "Database (JDBC)"
    category: "Connector"
    dependencies:
      - groupId: "org.mule.connectors"
        artifactId: "mule-db-connector"
        version: "1.14.7"
        classifier: "mule-plugin"

  - id: SALESFORCE
    label: "Salesforce"
    category: "Connector"
    dependencies:
      - groupId: "com.mulesoft.connectors"
        artifactId: "mule-salesforce-connector"
        version: "10.18.0"
        classifier: "mule-plugin"

  - id: ANYPOINT_MQ
    label: "Anypoint MQ"
    category: "Connector"
    dependencies:
      - groupId: "com.mulesoft.connectors"
        artifactId: "anypoint-mq-connector"
        version: "4.0.7"
        classifier: "mule-plugin"

  - id: FILE_CONNECTOR
    label: "File / FTP"
    category: "Connector"
    dependencies:
      - groupId: "org.mule.connectors"
        artifactId: "mule-file-connector"
        version: "1.5.2"
        classifier: "mule-plugin"
      - groupId: "org.mule.connectors"
        artifactId: "mule-ftp-connector"
        version: "1.8.5"
        classifier: "mule-plugin"

  - id: KAFKA
    label: "Kafka"
    category: "Connector"
    dependencies:
      - groupId: "org.mule.connectors"
        artifactId: "mule-kafka-connector"
        version: "4.7.3"
        classifier: "mule-plugin"

  - id: BATCH_MODULE
    label: "Batch Module"
    category: "Module"
    dependencies:
      - groupId: "com.mulesoft.modules"
        artifactId: "mule-batch-module"
        version: "2.3.0"
        classifier: "mule-plugin"

  - id: ERROR_HANDLING
    label: "Error Handling (Global)"
    category: "Core"
    dependencies: []        # Template-only, no extra Maven dependency

  - id: SCHEDULER
    label: "Scheduler"
    category: "Core"
    dependencies: []        # Built into Mule runtime

# ─────────────────────────────────────────────
# Post-generation hooks (optional)
# ─────────────────────────────────────────────
hooks:
  postGenerate:
    - type: shell
      command: "git init {outputDir}/{projectName}"
      enabled: false
    - type: shell
      command: "cp shared/log4j2.xml {outputDir}/{projectName}/src/main/resources/"
      enabled: false
```

---

## Project Types & Auto-Dependencies

When a developer selects a project type and toggles capabilities, `DependencyResolver` builds the full dependency list from `pack-mule.yml` and `TemplateRenderer` injects only the selected ones into `pom.xml` via a FreeMarker `<#list>` block — no manual copy-paste required.

| Project Type | Default Capabilities | Common Add-ons |
|---|---|---|
| **RESTful API** | HTTP Listener, APIkit, Error Handling | Database, Salesforce, Kafka |
| **Messaging** | Anypoint MQ, Error Handling | Kafka, Database |
| **Batch / File Processing** | File Connector, Batch Module, Error Handling | FTP, Salesforce, Database |
| **Scheduled Job** | Scheduler, Error Handling | Database, File Connector, HTTP |

---

## Adding Custom Templates

All output files are FreeMarker (`.ftl`) templates inside the `templates/` module.
To add a new project type:

1. Create a folder under `templates/src/main/resources/templates/` (e.g., `websocket/`).
2. Add `.xml.ftl` files. The following variables are available in every template:

```
${projectName}               → "customer-order-api"
${groupId}                   → "com.acme"
${muleVersion}               → "4.6.0"
${capabilities}              → List<Capability>
${hasCapability("DATABASE")} → true / false
```

3. Register the new type in `pack-mule.yml` under `projectTypes` with a `templateDir` pointing at your folder.
4. No Java changes required.

---

## Getting Started

### Prerequisites

- **Java 17+**
- **Apache Maven 3.8+** on `PATH` — needed to build and run the generated Mule project
- **MuleSoft Nexus credentials** (EE projects only) in `~/.m2/settings.xml`
- A terminal with ANSI support — macOS Terminal, iTerm2, Windows Terminal, any Linux terminal

### Install & Build

```bash
git clone https://github.com/your-org/pack-mule.git
cd pack-mule
mvn clean package -DskipTests
```

Produces a fat JAR at `target/pack-mule-app.jar`.

---

## Building & Running

```bash
# Interactive TUI
java -jar target/pack-mule-app.jar

# Use a custom config file
java -jar target/pack-mule-app.jar --config /path/to/pack-mule.yml

# Headless mode — for CI/CD pipelines or scripting
java -jar target/pack-mule-app.jar \
  --headless \
  --name customer-order-api \
  --groupId com.acme \
  --runtime 4.6.0 \
  --type REST_API \
  --capabilities HTTP_LISTENER,APIKIT,DATABASE,ERROR_HANDLING \
  --output ./generated
```

### Compile to Native Binary (GraalVM)

```bash
mvn -Pnative package
./target/pack-mule    # Zero JVM startup time
```

### Running Tests

```bash
mvn test                        # Unit tests
mvn verify -Pintegration-tests  # End-to-end generation tests
```
---

## Contributing

Pull requests are welcome. Please open an issue first to discuss changes. New capabilities should be accompanied by a FreeMarker template demonstrating their use, and dependency coordinates must be pinned to a specific version in `pack-mule.yml`.

> **Note:** TamboUI (`0.2.0-SNAPSHOT`) is under active development and its APIs may change between releases. If upgrading the BOM version causes compilation errors, check the [TamboUI changelog](https://github.com/tamboui/tamboui) and update affected screen classes.

---

## License

Apache License 2.0 — see [LICENSE](LICENSE) for details.
