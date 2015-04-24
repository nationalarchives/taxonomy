#!/bin/bash
cd $( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd );

source ../../conf/environmentVariables/exportEnvVar.sh taxonomy-global taxonomy-ws;
ps aux | grep taxonomy | grep ws | awk "{print \$2}" |  xargs kill;
#jar -uvf taxonomy-ws-${taxonomyJarVersion}.war WEB-INF
$javaBinary -jar -Dspring.profiles.active=${profile},tsetBased $@ ${wsPackageFolder}/taxonomy-ws-${taxonomyJarVersion}.war &
tail -f ${logsFolder}/ws/*
