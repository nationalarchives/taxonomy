source ../load-vars.sh;
mv ${iaViewIndexFolder}/write.lock ${iaViewIndexFolder}/write.lock.backup; 
java -jar -Dspring.profiles.active=${profile} ${cliPackageFolder}/taxonomy-cli-0.0.1-SNAPSHOT.jar $@ 
