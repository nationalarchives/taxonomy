#!/bin/bash
fileDir=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
cd $fileDir

application=$1

set -a
source ${appConfFolder}/global/${server}.sh
source ${appConfFolder}/${application}/${server}.sh
set +a
