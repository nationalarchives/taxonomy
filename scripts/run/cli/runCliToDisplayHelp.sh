#!/bin/bash
echo $(pwd)
currentDirectory=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd );
cd $currentDirectory;
source ../../conf/scripts/exportAppEnvVar.sh taxonomy-cli;

$javaBinary -jar ${cliPackageFolder}/taxonomy-cli-0.0.1-SNAPSHOT.jar --spring.profiles.active=${profile} -help
