#!/bin/bash
cd "$(dirname "$0")"

source ../../conf/exportEnvVar.sh taxonomy-global taxonomy-ws;
nohup $javaBinary -jar -Dspring.profiles.active=${profile} ${wsPackageFolder}/taxonomy-ws-0.0.1-SNAPSHOT.war $@ 2>> /dev/null >> /dev/null & 