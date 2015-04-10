#!/bin/bash
currentDirectory=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
cd $currentDirectory;

module=$1

echo "export env var for module(s) " $@
for module in "${@:1}"
do
	set -a
	#source environmentVariables/${module}/$(hostname).sh
	source ../../../sysdev-config/conf/environmentVariables/${module}/$(hostname).sh
	set +a
done