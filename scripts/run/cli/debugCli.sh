source ../load-vars.sh;
mv ${iaViewIndexFolder}/write.lock ${iaViewIndexFolder}/write.lock.backup; 
java -jar -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=9003 ${cliPackageFolder}/taxonomy-cli-0.0.1-SNAPSHOT.jar --spring.profiles.active=${profile} $@
