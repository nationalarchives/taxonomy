#!/bin/bash
cd "$(dirname "$0")"

source ../../conf/exportEnvVar.sh taxonomy-global taxonomy-cli;
mv ${iaViewIndexFolder}/write.lock ${iaViewIndexFolder}/write.lock.backup; 
$javaBinary -jar ${cliPackageFolder}/taxonomy-cli-0.0.1-SNAPSHOT.jar --spring.profiles.active=${profile} $@ 
