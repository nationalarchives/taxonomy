#!/bin/bash
cd $( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd );

action=
if [ -n "$1" ]
then
	action=$1
fi
ps aux | grep taxonomy | grep batch | egrep "($action)" | awk "{print \$2}"  | xargs kill
