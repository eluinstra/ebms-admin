=====================================
= Start EbMS Admin Console standalone
=====================================
show help:
> java -cp ebms-admin-2.x.x.jar nl.clockwork.ebms.admin.Start -h

start with hsqldb server:
> java -cp ebms-admin-2.x.x.jar nl.clockwork.ebms.admin.Start -hsqldb

=====================================================
= Start EbMS Admin Console with embedded EbMS adapter
=====================================================
show help:
> java -cp ebms-admin-2.x.x.jar nl.clockwork.ebms.admin.StartEmbedded -h

start with hsqldb server:
> java -cp ebms-admin-2.x.x.jar nl.clockwork.ebms.admin.StartEmbedded -hsqldb

================
= Build project
================
mvn package

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
