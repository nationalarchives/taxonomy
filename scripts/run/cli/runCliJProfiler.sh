source ../load-vars.sh;
mv ${iaViewIndexFolder}/write.lock ${iaViewIndexFolder}/write.lock.backup; 
java -jar -Dcom.sun.management.jmxremote -agentpath:/home/jcharlet/apps/jprofiler8/bin/linux-x64/libjprofilerti.so=port=8849 ${cliPackageFolder}/taxonomy-cli-0.0.1-SNAPSHOT.jar --spring.profiles.active=${profile} $@