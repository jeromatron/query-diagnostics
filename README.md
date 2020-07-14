# Query Diagnostics
This is a simple example of reading and writing to an Apache Cassandra database
cluster and giving a window into the internal routing and execution tracing within
the cluster. It utilizes basic logging, Cassandra query tracing, and events from
the driver's connection to the cluster. The driver events can include
notifications that members of the clusters go down or come back up.

It leaves aside data center failover which is discussed at length in this
[white paper](https://www.datastax.com/resources/whitepaper/designing-fault-tolerant-applications-datastax-and-apache-cassandratm)
and in this [webinar](https://www.google.com/url?q=https%3A%2F%2Fwww.datastax.com%2Fresources%2Fwebinar%2Fdesigning-fault-tolerant-applications-datastax-enterprise-and-apache-cassandra&sa=D&sntz=1&usg=AFQjCNGConcv3pI9R9IaAUys17ae7qeuxQ)
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
