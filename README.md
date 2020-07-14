# Query Diagnostics
This is a simple example of reading and writing to an Apache Cassandra database
cluster and giving a window into the internal routing and execution tracing within
the cluster. It utilizes basic logging, Cassandra query tracing, and events from
the driver's connection to the cluster. The driver events can include
notifications that members of the clusters go down or come back up.

It leaves aside data center failover which is discussed at length in this
[white paper](https://www.datastax.com/resources/whitepaper/designing-fault-tolerant-applications-datastax-and-apache-cassandratm)
and in this [webinar](https://www.datastax.com/resources/webinar/designing-fault-tolerant-applications-datastax-enterprise-and-apache-cassandra)
about best practices for designing fault tolerant applications.

There are often questions about why the server throws certain exceptions to the
client application. For example, why do I get `NoNodeAvailableException` errors when
I know nodes in my cluster are available? Why do I get `AllNodesFailedException`, 
`OperationTimedOutException`, `TransportException`, `UnavailableException`?

Diagnosis of a fault in a distributed system is tricky. The application and driver
have limited visibility of the status of the network and members of the database
cluster. Each exception derives from a limited knowledge of what's going on. 
See the [Byzantine Generals problem](https://en.wikipedia.org/wiki/Byzantine_fault) 
for why that is generally.

## Assumptions
Apache Maven should be installed along with a recent version of Java (Java 8+).

In the [application.conf](src/main/resources/application.conf) you'll see that the
default database address is `localhost` port `9042`.  It also defaults the data center
to the Apache Cassandra default of `dc1` when using the `GossipingPropertyFileSnitch`.
The program assumes that a node of the cluster is running on the localhost.

## To Build
To build the project, execute:

`mvn clean package`

## To Execute
To execute the program, run:

`mvn exec:java -D"exec.mainClass"="com.diagnostics.QueryDiagnostics`