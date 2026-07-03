# Changelog

All notable changes to this project are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Open-source governance files: `LICENSE`, `CONTRIBUTING.md`, `CODE_OF_CONDUCT.md`,
  `SECURITY.md`, `SUPPORT.md`, and this `CHANGELOG.md`.
- GitHub issue templates (bug report, feature request) and a pull request template.
- GitHub Actions CI workflow (`ci.yml`) running build + tests on push/PR.
- Dependabot configuration for Maven dependency updates.

### Changed
- README contributing section now points to `CONTRIBUTING.md`; fixed "FreeMarker"
  reference (project uses JMustache).

## [1.0.0-SNAPSHOT] - in development

Initial public release of Pack Mule — a MuleSoft project initializer TUI.

### Added
- Interactive TUI built on TamboUI Toolkit DSL with TCSS theming.
- Spring Initializr-style capability selector with auto-injected Maven dependencies.
- Project types: RESTful API, Messaging, Batch/File Processing, Scheduled Job.
- Pure JMustache template engine — no `mvn archetype:generate`, no network calls.
- Single-file configuration via `pack-mule.yaml` (runtimes, JDKs, capabilities, dependency catalog).
- External `templates/` directory override for organisation-specific standards.
- GraalVM native image build profile.
- Unit and integration test suites.

[Unreleased]: https://github.com/josephgonzales01/pack-mule/compare/main...HEAD
[1.0.0-SNAPSHOT]: https://github.com/josephgonzales01/pack-mule/releases/tag/v1.0.0-SNAPSHOT
