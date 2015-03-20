#!/bin/bash
cd "$(dirname "$0")"

action=
if [ -n "$1" ]
then
	action=$1
fi
ps aux | grep taxonomy | grep batch | grep $action | awk "{print \$2}"  | xargs kill
