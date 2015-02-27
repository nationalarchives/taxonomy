#!/bin/bash
cd "$(dirname "$0")"

source ../../conf/conf.sh;
jar -uf ${wsPackageFolder}/taxonomy-ws-0.0.1-SNAPSHOT.war WEB-INF
