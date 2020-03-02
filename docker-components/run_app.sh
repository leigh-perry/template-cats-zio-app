#!/usr/bin/env bash

# This runs inside the application docker container, all at /app

jarsPath () {
  printf ":%s" /app/lib/*.jar | cut -c2-
}

main() {
  JVM_SWITCHES="-XshowSettings:vm -XX:+UnlockExperimentalVMOptions -XX:+UseContainerSupport"
  SCALA_SWITCHES="-Dscala.concurrent.context.minThreads=4 -Dscala.concurrent.context.numThreads=x4 -Dscala.concurrent.context.maxThreads=64"

  JARS="$(jarsPath)"
  JAVA_BASE_COMMAND="java -cp ${JARS} ${JVM_SWITCHES} ${SCALA_SWITCHES}"

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
}

main "$@"
