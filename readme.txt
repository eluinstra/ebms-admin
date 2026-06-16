EbMS Admin

SOAP/REST server and web application module.

Prerequisite:

- Install ebms-core first to publish required local snapshot artifacts.

Build from repository root:

	mvn -f ebms-admin/pom.xml -B clean verify

Run tests only:

	mvn -f ebms-admin/pom.xml -B test

Package without tests:

	mvn -f ebms-admin/pom.xml -B -DskipTests=true package

Notes:

- When ebms-core APIs change, rebuild ebms-core before testing ebms-admin.
- Keep web/static changes aligned with documentation updates.
- For full docs, see https://eluinstra.github.io/ebms-admin/

Dependency Vulnerability Check:

Run dependency check to scan for known vulnerabilities in dependencies:

	mvn -f ebms-admin/pom.xml -B dependency-check:aggregate

The check fails if any vulnerability with CVSS score >= 7 (HIGH) is found.
Suppressions are defined in the module's root directory (dependency-check-suppressions.xml).
