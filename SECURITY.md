# Security Policy

## Supported Versions

Pack Mule is in active development (`1.0.0-SNAPSHOT`). Security fixes are applied to the latest `main` branch and the most recent release tag only.

| Version | Supported          |
|---------|--------------------|
| latest `main` | :white_check_mark: |
| latest release tag | :white_check_mark: |
| older tags | :x:                |

## Reporting a Vulnerability

**Please do not open a public GitHub issue for security vulnerabilities.**

Instead, report vulnerabilities privately:

- **Preferred**: use GitHub's [private vulnerability reporting](https://github.com/josephgonzales01/pack-mule/security/advisories/new).
- **Alternatively**: email **joseph.gonzales@fbu.com** with the subject `Security: Pack Mule`.

Please include the following in your report:

1. A description of the vulnerability and its impact.
2. Affected version / commit SHA.
3. Steps to reproduce, including a proof-of-concept if possible.
4. Any suggested mitigations.

### Response timeline

- **Acknowledgement**: within 48 hours.
- **Initial assessment**: within 5 business days.
- **Fix or mitigation**: targeted for the next release; you'll be kept informed of progress.
- **Credit**: reporters will be credited in the advisory unless they prefer to remain anonymous.

## Scope

In scope:
- Vulnerabilities in Pack Mule's Java code, TUI, template engine, or scaffolder that could lead to code execution, file overwrite outside the chosen output directory, or disclosure of secrets.
- Vulnerabilities in the CI/CD workflows in this repository.

Out of scope:
- Vulnerabilities in dependencies you've vendored or overridden yourself.
- Issues in generated MuleSoft projects (those are templates; report template correctness via a normal GitHub issue).
- Social engineering or phishing against maintainers.

## Disclosure

We follow coordinated disclosure. Once a fix is released we publish a GitHub Security Advisory crediting the reporter (unless anonymity is requested).

## Hardening notes for users

- Pack Mule writes generated projects to the working directory or a directory you choose. Run it from a dedicated, writable folder.
- Custom templates placed in an external `templates/` folder are treated as trusted code — they are evaluated as JMustache templates, so do not load templates from untrusted sources.
- Do not commit your organisation's `pack-mule.yaml` if it embeds sensitive connector credentials — use property placeholders and a separate, gitignored environment file.
