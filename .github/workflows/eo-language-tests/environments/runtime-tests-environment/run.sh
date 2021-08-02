#!/bin/bash
SCRIPT=`realpath $0`
SCRIPTPATH=`dirname $SCRIPT`
java -cp ${SCRIPTPATH}/target/classes:${SCRIPTPATH}/target/eo-runtime.jar -Xss40m  org.eolang.core.Main test.app "$@"