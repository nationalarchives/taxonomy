#!/bin/bash
cd "$(dirname "$0")"

source ../../conf/scripts/exportGlobalEnvVar.sh;
tail -f ${logsFolder}/ws/logging.log
