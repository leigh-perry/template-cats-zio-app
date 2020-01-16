#!/usr/bin/env bash

# This runs inside the application docker container, all at /app

JAR_FILE=$1
#GLOBAL_SWITCHES="--add-modules java.xml.bind"
GLOBAL_SWITCHES=
SCALA_SWITCHES="-Dscala.concurrent.context.minThreads=4 -Dscala.concurrent.context.numThreads=x4 -Dscala.concurrent.context.maxThreads=64"

echo JAR_FILE=${JAR_FILE}
ls -l ${JAR_FILE}
echo ============
env
echo ============
ls -lR /app
echo ============
env | grep LPTEMPLATEENVPREFIX_ | sort

if [[ "$LPTEMPLATEENVPREFIX_OPERATION" == "lptemplateservicename" ]]; then
    echo exec java -cp "${JAR_FILE}" ${GLOBAL_SWITCHES} ${SCALA_SWITCHES} com.lptemplatecompany.lptemplatedivision.lptemplateservicename.AppMain
    exec java -cp "${JAR_FILE}" ${GLOBAL_SWITCHES} ${SCALA_SWITCHES} com.lptemplatecompany.lptemplatedivision.lptemplateservicename.AppMain

else
  echo "Unknown option '$LPTEMPLATEENVPREFIX_OPERATION'"

fi
