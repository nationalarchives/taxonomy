#!/bin/bash
cd $( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd );

source ../../conf/environmentVariables/exportEnvVar.sh taxonomy-global taxonomy-cli;
mv ${iaViewIndexFolder}/write.lock ${iaViewIndexFolder}/write.lock.backup; 
$javaBinary -jar -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=9003 ${cliPackageFolder}/taxonomy-cli-${taxonomyJarVersion}.jar --spring.profiles.active=${profile} $@
