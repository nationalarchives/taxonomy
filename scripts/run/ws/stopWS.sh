#!/bin/bash
cd $( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd );

ps aux | grep taxonomy | grep ws | awk "{print \$2}"  | xargs kill
