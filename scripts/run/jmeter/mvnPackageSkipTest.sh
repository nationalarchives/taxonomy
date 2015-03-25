#!/bin/bash
cd "$(dirname "$0")"
mvn clean compile package -DskipTests=true -f ../../../ 
