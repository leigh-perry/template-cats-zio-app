#!/usr/bin/env bash

# This runs inside the application docker container, all at /app

#JVM_SWITCHES="--add-modules java.xml.bind"
#JVM_SWITCHES="-XshowSettings:vm -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -XX:MaxRAMFraction=1"
JVM_SWITCHES="-XshowSettings:vm -XX:+UnlockExperimentalVMOptions -XX:+UseContainerSupport"
SCALA_SWITCHES="-Dscala.concurrent.context.minThreads=4 -Dscala.concurrent.context.numThreads=x4 -Dscala.concurrent.context.maxThreads=64"
JAVA_BASE_COMMAND="java -cp $(printf ":%s" lib/*.jar | cut -c2-) ${JVM_SWITCHES} ${SCALA_SWITCHES}"

echo JAR_FILE=${JAR_FILE}
echo ================================================================================
ls -lR /app
echo ================================================================================
env | grep LPTEMPLATEENVPREFIX_ | sort
echo ================================================================================

if [[ "$LPTEMPLATEENVPREFIX_OPERATION" == "lptemplateservicename" ]]; then
    set -x
    exec ${JAVA_BASE_COMMAND} com.lptemplatecompany.lptemplatedivision.lptemplateservicename.AppMain

else
  echo "Unknown option '$LPTEMPLATEENVPREFIX_OPERATION'"

fi
