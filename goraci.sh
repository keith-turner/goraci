#!/bin/sh

GORACI_HOME=`dirname "$0"`
export HADOOP_CLASSPATH=$(JARS=("$GORACI_HOME/lib"/*.jar); IFS=:; echo "${JARS[*]}")
LIBJARS=`echo $HADOOP_CLASSPATH | tr : ,`


PACKAGE="goraci"

CMD=$1
shift

hadoop jar "$GORACI_HOME/lib/goraci-0.0.1-SNAPSHOT.jar" "$PACKAGE.$CMD" -libjars $LIBJARS $@

