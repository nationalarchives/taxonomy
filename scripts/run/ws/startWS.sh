#!/bin/bash
cd "$(dirname "$0")"

source ../../conf/conf.sh;
nohup $javaBinary -jar -Dspring.profiles.active=${profile} ${wsPackageFolder}/taxonomy-ws-0.0.1-SNAPSHOT.war $@ 2>> /dev/null >> /dev/null & 