
## Roadmap

- [ ] **CLI Argument Parsing** — allow organizations to provide custom configuration files via `--config /path/to/org-pack-mule.yml`
- [ ] **API spec import** — paste a RAML/OAS URL and Pack Mule scaffolds flows matching the spec endpoints
- [ ] **Plugin ecosystem** — third-party capability packs (`.jar` + YAML fragment on classpath) to add connectors and templates without forking
- [ ] **Post-generation hooks** — run optional shell scripts or Java hooks after generation (e.g., `git init`, copy shared config files)

---