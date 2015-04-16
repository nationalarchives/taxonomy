#!/bin/bash
cd $( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd );

source ../../conf/environmentVariables/exportEnvVar.sh taxonomy-global taxonomy-cli;
mv ${iaViewIndexFolder}/write.lock ${iaViewIndexFolder}/write.lock.backup; 
$javaBinary -jar ${cliPackageFolder}/taxonomy-cli-0.0.1-SNAPSHOT.jar --spring.profiles.active=${profile} $@ 
