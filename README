=================================
=				=
= GORACI README			=
= @author Keith Turner		=
=================================

BACKGROUND
------------

Apache Accumulo [0] has a simple test suite that verifies that data is not lost
at scale.  This test suite is called continuous ingest.  This test runs many
ingest clients that continually create linked lists containing 25 million
nodes. At some point the clients are stopped and a map reduce job is run to
ensure no linked list has a hole. A hole indicates data was lost.  

The nodes in the linked list are random.  This causes each linked list to
spread across the table.  Therefore if one part of a table loses data, then it
will be detected by references in another part of the table.

This project is a version of the test suite written using Apache Gora [1].
Theoretically it could run against other column stores, however currently it
has only been tested at scale using Apache Accumulo.

THE ANATOMY OF GORACI TESTS
----------------------------

Below is rough sketch of how data is written.  For specific details look at the
Generator code (src/main/java/goraci/Generator.java)

  1 Write out 1 million nodes 
  2 Flush 
  3 Write out 1 million that reference previous million 
  4 If this is the 25th set of 1 million nodes, then update 1st set of million
    to point to last 
  5 goto 1

The key is that nodes only reference flushed nodes.  Therefore a node should
never reference a missing node, even if the ingest client is killed at any
point in time.

When running this test suite w/ Accumulo there is a script running in parallel
called the Aggitator that randomly and continuously kills server processes.  
The outcome was that many data loss bugs were found in Accumulo by doing this. 
This test suite can also help find bugs that impact uptime and stability when 
run for days or weeks.  

This test suite consists the following 
- a few Java programs 
- a little helper script to run the java programs
- a maven script to build it.  

BUILDING GORACI
---------------

To build the code, you may need to edit the maven script to point to the gora
datastore that you want to use. This will require you to edit commented out
dependencies, which will reflect which datastore you wish to test against.
Alternatively just use the maven script to build the java code, and copy
whatever dependencies you need into lib.  

To compile, do 

$mvn compile package  

The current maven build script depends on an unreleased version of Accumulo and
an un released version of gora-accumulo.  Both of these can be downloaded and
installed in your local maven repo using mvn install.

JAVA CLASS DESCRIPTION
-----------------

Below is a description of the Java programs

  * goraci.Generator - A map only job that generates data.
  * goraci.Verify    - A map reduce job that looks for holes.  Look at the
                       counts after running.  REFERENCED and UNREFERENCED are 
                       ok, any UNDEFINED counts are bad. Do not run at the 
                       same time as the Generator.
  * goraci.Walker    - A standalong program that start following a linked  list 
                       and emits timing info.  
  * goraci.Print     - A standalone program that prints nodes in the linked list
  * goraci.Delete    - A standalone program that deletes a single node

goraci.sh is a helper script that you can use to run the above programs.  It
assumes all needed jars are in the lib dir.  It does not need the package name.
You can just run "./goraci.sh Generator", below is an example.

  $ ./goraci.sh Generator
  Usage : Generator <num mappers> <num nodes>

For Gora to work, it needs a gora.properties file on the classpath and a
mapping file on the classpath, the contents of both are datastore specific,
more details can be found here [2]. You can edit the ones in src/main/resources
and build the goraci-${version}-SNAPSHOT.jar with those. Alternatively remove
those and put them on the classpath through some other means.

CONCLUSIONS
------------

This test suite does not do everything that the Accumulo test suite does,
mainly it does not collect statistics and generate reports.  The reports
are useful for assesing performance.

Below shows running a test of the test.  Ingest one linked list, deleted a node
in it, ensure the verifaction map reduce job notices that the node is missing.
Not all output is shown, just the important parts.

  $ ./goraci.sh Generator  1 25000000
  $ ./goraci.sh Print -s 2000000000000000 -l 1
  2000001f65dbd238:30350f9ae6f6e8f7:000004265852:ef09f9dd-75b1-4c16-9f14-0fa84f3029b6
  $ ./goraci.sh Print -s 30350f9ae6f6e8f7 -l 1
  30350f9ae6f6e8f7:4867fe03de6ea6c8:000003265852:ef09f9dd-75b1-4c16-9f14-0fa84f3029b6
  $ ./goraci.sh Delete 30350f9ae6f6e8f7
  Delete returned true
  $ ./goraci.sh Verify gci_verify_1 2 
  11/12/20 17:12:31 INFO mapred.JobClient:   goraci.Verify$Counts
  11/12/20 17:12:31 INFO mapred.JobClient:     UNDEFINED=1
  11/12/20 17:12:31 INFO mapred.JobClient:     REFERENCED=24999998
  11/12/20 17:12:31 INFO mapred.JobClient:     UNREFERENCED=1
  $ hadoop fs -cat gci_verify_1/part\*
  30350f9ae6f6e8f7	2000001f65dbd238

The map reduce job found the one undefined node and gave the node that
referenced it.

[0] http://accumulo.apache.org
[1] http://gora.apache.org
[2] http://gora.apache.org/docs/current/gora-conf.html
