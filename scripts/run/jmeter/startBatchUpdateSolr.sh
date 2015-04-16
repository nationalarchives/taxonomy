#!/bin/bash
directory=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd );
cd $directory;
source ../batch/taxonomyBatchRunTool.sh -bt updateSolr --doNotDisplayLogs -aa -taxonomyUpdateSolrJmeter -ln updateSolr.log
cat /dev/null > ${logsFolder}/batch/updateSolr.log

sh -c 'tail --pid=$$ -f -n 3 '${logsFolder}'/batch/updateSolr.log | { sed "/Started BatchApplication/ q" && kill $$ ;}'
echo APP STARTED
exit 0 