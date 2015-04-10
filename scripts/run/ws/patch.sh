#!/bin/bash
cd "$(dirname "$0")"

source ../../conf/scripts/exportGlobalEnvVar.sh;
jar -uf ${wsPackageFolder}/taxonomy-ws-0.0.1-SNAPSHOT.war WEB-INF
