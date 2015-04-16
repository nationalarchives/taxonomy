#!/bin/bash
cd $( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd );

source ../../conf/environmentVariables/exportEnvVar.sh taxonomy-global taxonomy-ws;
nohup $javaBinary -jar -Dspring.profiles.active=${profile} ${wsPackageFolder}/taxonomy-ws-0.0.1-SNAPSHOT.war $@ 2>> /dev/null >> /dev/null & 