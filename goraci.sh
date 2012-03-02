#!/bin/sh
#
# The Goraci command script
#
# Environment Variables
#
#   GORACI_JAVA_HOME The java implementation to use.  Overrides JAVA_HOME.
#
#   GORACI_HEAPSIZE  The maximum amount of heap to use, in MB. 
#                   Default is 1000.
#
#   GORACI_OPTS      Extra Java runtime options.
#

# resolve links - $0 may be a softlink
THIS="$0"
while [ -h "$THIS" ]; do
  ls=`ls -ld "$THIS"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '.*/.*' > /dev/null; then
    THIS="$link"
  else
    THIS=`dirname "$THIS"`/"$link"
  fi
done

# if no args specified, show usage
if [ $# = 0 ]; then
  echo "Usage: run COMMAND [COMMAND options]"
  echo "where COMMAND is one of:"
  echo "  generator                  A map only job that generates data."
  echo "  verify                     A map reduce job that looks for holes.  
                             Look at the counts after running.  
  		             REFERENCED and UNREFERENCED are ok, 
  		             any UNDEFINED counts are bad. Do not 
  			     run at the same time as the Generator."
  echo "  walker                     A standalong program that starts 
  			     following a linked list and emits 
  			     timing info."
  echo "  print                      A standalone program that prints nodes 
  			     in the linked list."
  echo "  delete         	     A standalone program that deletes a 
  			     single node."
  echo " or"
  echo "  CLASSNAME                  run the class named CLASSNAME"
  echo "Most commands print help when invoked w/o parameters."
  exit 1
fi

# get arguments
COMMAND=$1
shift
  
# some directories
THIS_DIR=`dirname "$THIS"`
GORACI_HOME=`cd "$THIS_DIR/.." ; pwd`

# cath when JAVA_HOME is not set
if [ "$JAVA_HOME" = "" ]; then
  echo "Error: JAVA_HOME is not set."
  exit 1
fi

JAVA=$JAVA_HOME/bin/java
JAVA_HEAP_MAX=-Xmx1024m 

# check envvars which might override default args
if [ "$GORACI_HEAPSIZE" != "" ]; then
  #echo "run with heapsize $GORACI_HEAPSIZE"
  JAVA_HEAP_MAX="-Xmx""$GORACI_HEAPSIZE""m"
  #echo $JAVA_HEAP_MAX
fi

# initial CLASSPATH 
CLASSPATH=$JAVA_HOME/lib/tools.jar

# so that filenames w/ spaces are handled correctly in loops below
IFS=

# restore ordinary behaviour
unset IFS

# default log directory & file
if [ "$GORACI_LOG_DIR" = "" ]; then
  GORACI_LOG_DIR="$GORACI_HOME/logs"
fi
if [ "$GORACI_LOGFILE" = "" ]; then
  GORACI_LOGFILE='goraci.log'
fi

if [ "x$JAVA_LIBRARY_PATH" != "x" ]; then
  JAVA_OPTS="$JAVA_OPTS -Djava.library.path=$JAVA_LIBRARY_PATH"
fi

#GORACI_OPTS="$GORACI_OPTS -Dhadoop.log.dir=$GORACI_LOG_DIR"
#GORACI_OPTS="$GORACI_OPTS -Dhadoop.log.file=$GORACI_LOGFILE"

# figure out which class to run
if [ "$COMMAND" = "generator" ] ; then
  CLASS=goraci.Generator
elif [ "$COMMAND" = "verify" ] ; then
  CLASS=goraci.Verify
elif [ "$COMMAND" = "walker" ] ; then
  CLASS=goraci.Walker
elif [ "$COMMAND" = "print" ] ; then
  CLASS=goraci.Print
elif [ "$COMMAND" = "delete" ] ; then
  CLASS=goraci.Print
else
  MODULE="$COMMAND"
  CLASS=$1
  shift
fi

# add libs to CLASSPATH
for f in $GORA_HOME/$MODULE/lib/*.jar; do
  CLASSPATH=${CLASSPATH}:$f;
done

#run it
hadoop jar "$GORACI_HOME/lib/goraci-0.0.1-SNAPSHOT.jar" -classpath "$CLASSPATH" $CLASS "$@"


