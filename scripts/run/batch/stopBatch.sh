#!/bin/bash
cd "$(dirname "$0")"

ps aux | grep taxonomy | grep batch | awk "{print \$2}"  | xargs kill
