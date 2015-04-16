#!/bin/bash
directory=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd );
cd $directory;
source ../../conf/environmentVariables/exportEnvVar.sh taxonomy-global;
cat /dev/null > ${logsFolder}/batch/dailyUpdates.log

cd $directory;
source ../batch/taxonomyBatchRunTool.sh -bt dailyUpdates --doNotDisplayLogs -aa -taxonomyDailyUpdatesJmeter -ln dailyUpdates.log
sh -c 'tail --pid=$$ -f -n 3 '${logsFolder}'/batch/dailyUpdates.log | { sed "/Started BatchApplication/ q" && kill $$ ;}'
echo APP STARTED
exit 0