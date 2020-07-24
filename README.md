# Query Diagnostics
This is a simple example of reading and writing to an [Apache Cassandra](https://cassandra.apache.org) database
cluster and giving a window into the internal routing and execution tracing within
the cluster. It utilizes basic logging, Cassandra query tracing, and events from
the driver's connection to the cluster. The driver events can include
notifications that members of the clusters go down or come back up along with connections
to local and remote data centers.

There are often questions about why the server throws certain exceptions to the
client application. For example, why do I get [`NoNodeAvailableException`](https://docs.datastax.com/en/drivers/java/4.7/com/datastax/oss/driver/api/core/NoNodeAvailableException.html)
errors when I know nodes in my cluster are available? Diagnosis of a fault in a 
distributed system is tricky. The application and driver have limited visibility 
of the status of the network and members of the database cluster. Each exception 
derives from a limited knowledge of what's going on. See the
[Byzantine Generals problem](https://en.wikipedia.org/wiki/Byzantine_fault) 
for why that is generally.  However with more information about what is seen at each stage
through tracing and logging, you will hopefully arrive at a diagnosis more efficiently.

Not considered directly for this example are multi-data center connections and data center failover.  However,
the logging in this example shows what connections are made from the driver to nodes in the cluster.  Best practices
for failover, especially in a multi-data center environment, are discussed at length in this
[white paper](https://www.datastax.com/resources/whitepaper/designing-fault-tolerant-applications-datastax-and-apache-cassandratm)
and in this [webinar](https://www.datastax.com/resources/webinar/designing-fault-tolerant-applications-datastax-enterprise-and-apache-cassandra)
about best practices for designing fault tolerant applications. There is also an
accompanying [demo](https://github.com/datastax/dc-failover-demo) to show best practices around dc-failover with the latest 4.x Java driver.

## Assumptions and requirements

- [Apache Maven](https://maven.apache.org) should be installed and in the path
- A recent version of Java (Java 8+) should be installed and in the path
- The [application.conf](src/main/resources/application.conf) should be configured with an address (or addresses) of a server or servers
running Cassandra or DataStax Enterprise.  You'll see that the default database address is `localhost` port `9042`.
It also defaults the data center to the Apache Cassandra default of `dc1` when using the `GossipingPropertyFileSnitch`.
The program assumes that a node of the cluster is running on the localhost.

## To Build
To build the project, execute:

`mvn clean package`

## To Execute
To execute the program, run:

`mvn exec:java -D"exec.mainClass"="com.diagnostics.QueryDiagnostics"`

## Additional resources
- Note in this repo that we've enabled debug logging of `com.datastax.oss.driver.internal.core.channel` [here](src/main/resources/logback.xml#L12) to log connection updates with different nodes in the cluster
- [Java driver documentation on query tracing](https://docs.datastax.com/en/developer/java-driver/4.7/manual/core/tracing/)
- [`cqlsh` query tracing](https://docs.datastax.com/en/cql-oss/3.3/cql/cql_reference/cqlshTracing.html)
- DataStax Studio developer notebooks have [execution configurations that can perform query traces](https://docs.datastax.com/en/studio/6.8/studio/gs/manageRunConfigurations.html)
- [Interactive request tracing](https://www.datastax.com/blog/2012/11/request-tracing-cassandra-12)
- [Probabilistic tracing](https://www.datastax.com/blog/2012/11/advanced-request-tracing-cassandra-12)
- [Packet capture for dynamic tracing](https://cassandra.apache.org/doc/latest/troubleshooting/use_tools.html#packet-capture)
- [Using Wireshark for dynamic cql tracing](http://www.redshots.com/finding-rogue-cassandra-queries/)
- [Replacing Cassandra tracing with Zipkin](https://thelastpickle.com/blog/2015/12/07/using-zipkin-for-full-stack-tracing-including-cassandra.html) (this method is not yet possible with DataStax Enterprise)
