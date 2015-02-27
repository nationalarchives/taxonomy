#!/bin/bash
cd "$(dirname "$0")"

source ../../conf/conf.sh;
ps aux | grep taxonomy | grep ws | awk "{print \$2}" |  xargs kill;
#jar -uvf taxonomy-ws-0.0.1-SNAPSHOT.war WEB-INF
java -jar -Dspring.profiles.active=${profile},tsetBased $@ ${wsPackageFolder}/taxonomy-ws-0.0.1-SNAPSHOT.war &
tail -f ${logsFolder}/ws/*
