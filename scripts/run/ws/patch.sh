#!/bin/bash
cd "$(dirname "$0")"

source ../../conf/exportEnvVar.sh taxonomy-global;
jar -uf ${wsPackageFolder}/taxonomy-ws-0.0.1-SNAPSHOT.war WEB-INF