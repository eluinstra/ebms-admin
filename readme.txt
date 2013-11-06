===========================
= Start EbMS Admin Console
===========================
> java -jar ebms-admin-1.0.0.jar
> java -cp ebms-admin-1.0.0.jar nl.clockwork.ebms.admin.Main

> java -cp ebms-admin-1.0.0.jar nl.clockwork.ebms.admin.Start
> java -cp ebms-admin-1.0.0.jar nl.clockwork.ebms.admin.Start -h

================
= Build project
================
mvn package
mvn license:format

==========
= Eclipse
==========
Import -> Existing Maven Projects

resolve js validation errors:
- Properties -> JavaScript -> Include Path -> Source
- Edit ebms-admin/src/main/webapp -> Excluded
- Add Exclusion Pattern: js/jquery-min.js
- Remove file js/jquery-min.js from project and add it again
OR
- Properties -> Builders
- Uncheck JavaScript Validator
- Remove file js/jquery-min.js from project and add it again
