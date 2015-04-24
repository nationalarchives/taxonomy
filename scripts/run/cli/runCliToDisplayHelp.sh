#!/bin/bash
echo $(pwd)
currentDirectory=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd );
cd $currentDirectory;
source ../../conf/environmentVariables/exportEnvVar.sh taxonomy-global taxonomy-cli;

$javaBinary -jar ${cliPackageFolder}/taxonomy-cli-${taxonomyJarVersion}.jar --spring.profiles.active=${profile} -help
