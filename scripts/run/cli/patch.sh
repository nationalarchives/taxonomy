#!/bin/bash
cd $( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd );

source ../../conf/environmentVariables/exportEnvVar.sh taxonomy-global;
cd ${packageFolder};jar -uf taxonomy-cli-0.0.1-SNAPSHOT.jar application-${profile}.yml
