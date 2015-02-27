#!/bin/bash
cd "$(dirname "$0")"

source ../../conf/conf.sh;
java -jar ${cliPackageFolder}/taxonomy-cli-0.0.1-SNAPSHOT.jar --spring.profiles.active=${profile} -help
