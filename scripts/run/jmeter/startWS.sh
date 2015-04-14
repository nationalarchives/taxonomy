#!/bin/bash
directory="$(dirname "$0")"
cd $directory;
source ../ws/startWS.sh -taxonomyWSJmeter
cd $directoy;
source ../../conf/environmentVariables/exportEnvVar.sh taxonomy-global;
sh -c 'tail --pid=$$ -f -n 3 '${logsFolder}'/ws/logging.log | { sed "/Started WSApplication/ q" && kill $$ ;}'
echo APP STARTED
exit 0