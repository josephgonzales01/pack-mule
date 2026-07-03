# Contributing to Pack Mule

First off, thanks for taking the time to contribute! :tada:

Pack Mule is a MuleSoft project initializer TUI built on [TamboUI](https://tamboui.dev) and [JMustache](https://github.com/samskivert/jmustache). This guide explains how to set up your environment and submit changes that can be merged quickly.

> **TL;DR** — Fork the repo, create a branch off `main`, run `mvn clean verify`, push, open a Pull Request. Significant changes should be discussed in an issue first.

---

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Prerequisites](#prerequisites)
- [Getting the Source](#getting-the-source)
- [Building & Running](#building--running)
- [Project Layout](#project-layout)
- [Development Workflow](#development-workflow)
- [Testing](#testing)
- [Coding Standards](#coding-standards)
- [Adding a New Capability or Trigger](#adding-a-new-capability-or-trigger)
- [Commit Messages](#commit-messages)
- [Pull Requests](#pull-requests)
- [Releases](#releases)
- [Questions](#questions)

---

## Code of Conduct

Participation in this project is governed by the [Contributor Covenant Code of Conduct](CODE_OF_CONDUCT.md). By participating, you are expected to uphold this code. Please report unacceptable behaviour to **josephgonzales01@gmail.com**.

---

## Prerequisites

| Tool | Version | Notes |
|---|---|---|
| JDK | 17+ (21 recommended — `pom.xml` targets 21) | Set `JAVA_HOME` accordingly |
| Apache Maven | 3.8+ | Must be on `PATH` |
| Git | any recent version | |
| A terminal with ANSI support | macOS Terminal, iTerm2, Windows Terminal, any Linux terminal | Required to run the TUI |

### MuleSoft Nexus credentials (optional but recommended)

Integration tests scaffold real Mule projects and compile them, which pulls artifacts from MuleSoft's proprietary Nexus. If you have access, configure your `~/.m2/settings.xml`:

```xml
<settings>
  <servers>
    <server>
      <id>mule-ee-releases</id>
      <username>YOUR_NEXUS_USER</username>
      <password>YOUR_NEXUS_PASSWORD</password>
    </server>
  </servers>
</settings>
```

Without these credentials, `mvn verify -Pintegration-tests` may fail to resolve EE artifacts. Unit tests (`mvn test`) run fine without them.

---

## Getting the Source

```bash
# 1. Fork the repo on GitHub, then:
git clone https://github.com/<your-username>/pack-mule.git
cd pack-mule
git remote add upstream https://github.com/josephgonzales01/pack-mule.git
```

Keep your fork in sync:

```bash
git fetch upstream
git checkout main
git rebase upstream/main
```

---

## Building & Running

```bash
# Build the fat JAR, skipping tests for a fast first build
mvn clean package -DskipTests

# Launch the interactive TUI
java -jar target/pack-mule-app.jar

# Use a custom config file
java -jar target/pack-mule-app.jar --config /path/to/pack-mule.yaml
```

### Native image (GraalVM)

```bash
mvn -Pnative package
./target/pack-mule      # Zero JVM startup time
```

---

## Project Layout

```text
pack-mule/
├── src/main/java/dev/sugbo4j/         # Application code (TUI + scaffolder)
├── src/main/resources/
│   ├── pack-mule.yaml                 # Single control plane: runtimes, capabilities, deps
│   └── templates/                     # JMustache templates (base / triggers / capabilities)
├── src/main/resources/docs/           # Solution design & screenshots
├── src/test/                          # Unit + integration tests
├── pom.xml
└── .github/                           # CI, issue/PR templates, dependabot
```

Templates are decoupled from Java logic. Adding a capability rarely requires touching Java — see [Adding a New Capability or Trigger](#adding-a-new-capability-or-trigger).

---

## Development Workflow

1. **Find or open an issue** describing what you want to change.
2. **Create a feature branch** from `main`:
   ```bash
   git checkout -b feat/my-feature
   ```
   Use conventional prefixes: `feat`, `fix`, `docs`, `refactor`, `test`, `chore`, `perf`.
3. **Make your changes** in small, logical commits.
4. **Run tests and checks locally** (see [Testing](#testing)).
5. **Push and open a Pull Request** against `main`. Reference the issue (e.g. `Closes #42`).

---

## Testing

```bash
mvn test                        # Unit tests — fast, no Nexus needed
mvn verify                      # Build + unit tests
mvn verify -Pintegration-tests  # End-to-end generation tests (needs Nexus creds)
```

- All new logic **must** be covered by unit tests.
- Bug fixes should include a regression test.
- Template changes should be validated by an integration test that scaffolds and compiles a project.
- Do not commit generated files under `target/` — the `.gitignore` already excludes them.

---

## Coding Standards

- **Java 21** language features are welcome.
- Follow existing style in the file you're editing; when creating new files match the surrounding conventions (4-space indent, BSD-style braces as used in the repo).
- Keep classes focused; prefer composition over inheritance.
- No commented-out code or `println` debugging in committed code.
- Public API additions require a Javadoc comment.
- Logging uses the framework already wired in `pom.xml` — don't introduce a new logging facade.

### Formatting & lint

There is no enforced formatter committed yet; when in doubt, match the surrounding code. A checkstyle/spotless config may be added in a future PR — contributions welcome.

---

## Adding a New Capability or Trigger

1. **Templates**: create a folder matching the capability/trigger ID:
   ```
   src/main/resources/templates/capabilities/MY_CAP/
       src/main/mule/{{projectName}}-my-cap-flow.xml
   ```
   Use JMustache variables: `{{projectName}}`, `{{groupId}}`, `{{muleVersion}}`, `{{#selectedDependencies}}`.
2. **Register** it in `pack-mule.yaml`:
   ```yaml
   capabilities:
     - id: MY_CAP
       label: "My Capability"
       category: "Connector"
       dependencies: ["my-connector"]
   ```
3. **Dependency catalog** — pin the version in the same file:
   ```yaml
   dependencies:
     my-connector:
       groupId: "org.mule.connectors"
       artifactId: "mule-my-connector"
       version: "1.2.3"
       classifier: "mule-plugin"
   ```
4. **No Java changes required** for a purely template-driven capability.
5. Add a short entry under the appropriate table in `README.md` and, if behaviour is non-obvious, in `src/main/resources/docs/SolutionDesign.md`.

---

## Commit Messages

Use [Conventional Commits](https://www.conventionalcommits.org/):

```
<type>(<scope>): <subject>

<body optional>

<footer optional, e.g. Closes #42>
```

Examples:

```
feat(capabilities): add Kafka publisher capability
fix(scaffolder): resolve dynamic filename when projectName contains spaces
docs: clarify Nexus credential setup in CONTRIBUTING
chore(deps): bump tamboui-bom to 0.2.0-SNAPSHOT-...
```

Keep the subject line under 72 characters, imperative mood ("add", "fix", "update").

---

## Pull Requests

- Target the `main` branch.
- Keep PRs focused — one logical change per PR. Split large work into stacked PRs.
- Fill in the [Pull Request template](.github/PULL_REQUEST_TEMPLATE.md).
- Ensure CI is green before requesting review.
- Be responsive to review feedback; push follow-up commits rather than force-pushing over reviewed commits unless asked.
- If your PR introduces a new dependency, justify it in the PR description and pin the version.

### Review process

A maintainer will review your PR. Expect feedback within a few days. Once approved and CI is green, a maintainer will squash-merge your PR.

---

## Releases

Releases are cut from `main` and tagged as `vX.Y.Z`. The changelog is kept in [CHANGELOG.md](CHANGELOG.md). Release artefacts (fat JAR, native binary) are attached to the GitHub Release.

---

## Questions

- Open a [Discussion](https://github.com/josephgonzales01/pack-mule/discussions) for "how-to" questions.
- Open an [Issue](https://github.com/josephgonzales01/pack-mule/issues) for bugs and feature requests.
- See [SUPPORT.md](SUPPORT.md) for the full list of support channels.

Happy hacking! :mule:
