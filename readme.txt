mvn archetype:generate -DarchetypeGroupId=org.apache.wicket -DarchetypeArtifactId=wicket-archetype-quickstart -DarchetypeVersion=6.9.1 -DgroupId=nl.clockwork.ebms.admin -DartifactId=ebms-admin -DarchetypeRepository=https://repository.apache.org/ -DinteractiveMode=false
mvn package
mvn license:format