#!/bin/bash
cd $( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd );

source ../../conf/environmentVariables/exportEnvVar.sh taxonomy-global taxonomy-cli;
mv ${iaViewIndexFolder}/write.lock ${iaViewIndexFolder}/write.lock.backup; 
$javaBinary -jar -Dcom.sun.management.jmxremote -agentpath:/home/jcharlet/apps/jprofiler8/bin/linux-x64/libjprofilerti.so=port=8849 ${cliPackageFolder}/taxonomy-cli-${taxonomyJarVersion}.jar --spring.profiles.active=${profile} $@
