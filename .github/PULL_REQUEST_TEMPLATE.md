<!-- Thank you for contributing to Pack Mule! Please fill in the sections below. -->

## Summary

<!-- What does this PR do, and why? One or two sentences. -->

Closes #<issue-number>

## Type of change

- [ ] 🐛 Bug fix (non-breaking change that fixes an issue)
- [ ] ✨ New feature (non-breaking change that adds functionality)
- [ ] 💥 Breaking change (fix or feature that would cause existing functionality to not work as expected)
- [ ] 📚 Documentation update
- [ ] ♻️ Refactor / chore / dependency bump
- [ ] 🧹 Template-only change (no Java code modified)

## Changes

<!-- Bullet list of the notable changes. -->
-

## How to test

<!-- Steps a reviewer can follow to verify this works. Include any new test cases. -->

1.
2.

```bash
mvn test
# or: mvn verify -Pintegration-tests
```

## Checklist

- [ ] I have read [CONTRIBUTING.md](../CONTRIBUTING.md) and followed the workflow.
- [ ] This change is discussed in a linked issue (or is a small, obvious fix).
- [ ] I added/updated tests for new behaviour or bug fixes.
- [ ] `mvn test` (and `mvn verify -Pintegration-tests` where relevant) passes locally.
- [ ] I updated `README.md` / `CHANGELOG.md` / `src/main/resources/docs/SolutionDesign.md` where relevant.
- [ ] If I added a dependency, I pinned its version in `pack-mule.yaml` and justified it below.
- [ ] I have **not** committed anything under `target/` or other generated artefacts.
- [ ] My commits follow [Conventional Commits](https://www.conventionalcommits.org/).

## New dependencies (if any)

<!-- groupId:artifactId:version, why it's needed, and its license. Leave empty if none. -->

## Screenshots / output (if applicable)

<!-- For TUI or template-output changes, attach a screenshot or a generated file snippet. -->
