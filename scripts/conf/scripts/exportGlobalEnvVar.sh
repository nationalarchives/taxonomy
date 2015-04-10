#!/bin/bash

#This script should export the variables provided by the template file 
# after you 
#			filled it with your own env values 
#			renamed as "$(hostname).sh"
cd $( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )

set -a
#source environmentVariables/global/$(hostname).sh
source ../../../../sysdev-config/environmentVariables/taxonomy/global/$(hostname).sh
set +a

