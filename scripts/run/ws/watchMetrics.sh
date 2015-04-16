#!/bin/bash
cd $( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd );

source ../../conf/environmentVariables/exportEnvVar.sh taxonomy-global;
watch -n 1 --differences=cumulative 'curl -s http://localhost:'${port}'/metrics --noproxy localhost | python -mjson.tool'
