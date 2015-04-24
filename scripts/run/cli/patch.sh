#!/bin/bash
cd $( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd );

source ../../conf/environmentVariables/exportEnvVar.sh taxonomy-global;
cd ${packageFolder};jar -uf taxonomy-cli-${taxonomyJarVersion}.jar application-${profile}.yml
