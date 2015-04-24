#!/bin/bash
cd $( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd );

source ../../conf/environmentVariables/exportEnvVar.sh taxonomy-global;
jar -uf ${wsPackageFolder}/taxonomy-ws-${taxonomyJarVersion}.war WEB-INF
