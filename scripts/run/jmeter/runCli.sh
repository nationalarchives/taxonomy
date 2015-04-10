#!/bin/bash
currentDirectory=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd );
cd $currentDirectory;
source ../cli/runCli.sh -help