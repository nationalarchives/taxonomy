#!/bin/bash
cd $( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd );

source ../../conf/environmentVariables/exportEnvVar.sh taxonomy-global;
tail -f ${logsFolder}/ws/logging.log
