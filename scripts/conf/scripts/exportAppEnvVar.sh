#!/bin/bash
echo "export app env var"
currentDirectory=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
cd $currentDirectory;

source exportGlobalEnvVar.sh;

application=$1

set -a
cd $currentDirectory;
source ${confFolder}/taxonomy/${application}/$(hostname).sh
set +a
