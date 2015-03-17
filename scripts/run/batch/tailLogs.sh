#!/bin/bash
cd "$(dirname "$0")"

source ../../conf/conf.sh;
tail -f ${logsFolder}/batch/logging.log
