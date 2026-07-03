---
name: Bug report
about: Something is broken or producing the wrong output
title: "[Bug]: "
labels: bug, triage
assignees: ''
---

## Describe the bug

A clear and concise description of what the bug is.

## To reproduce

Steps to reproduce the behaviour:

1. Run `java -jar target/pack-mule-app.jar ...`
2. Select ... (project type / trigger / capabilities)
3. Confirm on the summary screen
4. Observe: ...

```bash
# Exact command you ran
java -jar target/pack-mule-app.jar --config ...
```

## Expected behaviour

What you expected to happen.

## Actual behaviour

What actually happened (error message, wrong file generated, missing dependency, etc.).

## Generated output / logs

If applicable, paste the relevant snippet from:

- The generated `pom.xml` or Mule XML flow.
- Stack trace or TUI error output.

<details><summary>Logs</summary>

```
paste here
```

</details>

## Environment

- **Pack Mule version**: e.g. `1.0.0-SNAPSHOT` (commit SHA if built from source):
- **OS**: e.g. Windows 11 / macOS 14 / Ubuntu 22.04
- **Java version**: output of `java -version`
- **Maven version** (if building): output of `mvn -version`
- **Terminal**: e.g. Windows Terminal, iTerm2, GNOME Terminal

## `pack-mule.yaml` (if customised)

<details><summary>config</summary>

```yaml
paste relevant sections
```

</details>

## Additional context

Anything else — screenshots, workarounds you tried.
