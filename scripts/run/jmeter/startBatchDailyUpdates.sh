#!/bin/bash
cd "$(dirname "$0")"
directory="$(dirname "$0")"
cd $directory;
source ../batch/taxonomyBatchRunTool.sh -bt dailyUpdates --doNotDisplayLogs -aa -taxonomyDailyUpdatesJmeter
cd $directoy;
source ../../conf/environmentVariables/exportEnvVar.sh taxonomy-global;

sh -c 'tail --pid=$$ -f -n 3 '${logsFolder}'/batch/logging.log | { sed "/Started BatchApplication/ q" && kill $$ ;}'
echo APP STARTED
exit 0