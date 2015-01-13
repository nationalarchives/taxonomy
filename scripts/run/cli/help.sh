source ../load-vars.sh;
java -jar -Dspring.profiles.active=${profile} ${cliPackageFolder}/taxonomy-cli-0.0.1-SNAPSHOT.jar -help
