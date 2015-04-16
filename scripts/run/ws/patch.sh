#!/bin/bash
cd $( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd );

source ../../conf/environmentVariables/exportEnvVar.sh taxonomy-global;
jar -uf ${wsPackageFolder}/taxonomy-ws-0.0.1-SNAPSHOT.war WEB-INF
