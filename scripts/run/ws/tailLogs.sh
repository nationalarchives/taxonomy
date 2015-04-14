#!/bin/bash
cd "$(dirname "$0")"

source ../../conf/environmentVariables/exportEnvVar.sh taxonomy-global;
tail -f ${logsFolder}/ws/logging.log
