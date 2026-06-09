Copilot instructions - ebms-admin

Scope

- Applies to files under ebms-admin/.
- ebms-admin depends on locally built snapshot artifacts from ebms-core.

Build and test

- Build and verify: mvn -f ebms-admin/pom.xml -B clean verify
- Run tests: mvn -f ebms-admin/pom.xml -B test
- Package quickly: mvn -f ebms-admin/pom.xml -B -DskipTests=true package

High-impact change rules

- Rebuild ebms-core before validating ebms-admin after upstream API changes.
- For service/API changes, validate both tests and generated/served API docs.
- For web asset updates, keep documentation examples in sync.

Review checklist

- Add tests for service behavior changes.
- Document local verification commands and outcomes in PR notes.
- Keep config and logging changes minimal and explicit.

