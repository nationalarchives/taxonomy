#!/bin/bash
cd "$(dirname "$0")"

source ../../conf/environmentVariables/exportEnvVar.sh taxonomy-global;
cd ${packageFolder};jar -uf taxonomy-cli-0.0.1-SNAPSHOT.jar application-${profile}.yml
