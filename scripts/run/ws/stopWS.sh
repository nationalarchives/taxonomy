#!/bin/bash
cd "$(dirname "$0")"

ps aux | grep taxonomy | grep ws | awk "{print \$2}"  | xargs kill
