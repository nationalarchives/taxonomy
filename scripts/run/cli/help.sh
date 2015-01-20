source ../load-vars.sh;
java -jar ${cliPackageFolder}/taxonomy-cli-0.0.1-SNAPSHOT.jar --spring.profiles.active=${profile} -help
