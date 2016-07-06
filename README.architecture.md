# PAC Voting Application System Architecture

PAC Assignment - Voting Service

## High Availability

#UI

The UI is able to run in the cloud with [Firebase](https://firebase.google.com/).

Alternatively a self hosted solution can provide a redundant apache web server.  Two servers with the same virtual host configuration can host the UI distribution.  For the reference UI web application this means unpacking the contents of the dist folder into the virtual host root folder which should load the index.html file as default.

For HA a load balancer pair services a Virtual IP address with a DNS entry for the application mapped to the IP address.  The load balancer can be configured to route requests to the least loaded apache server.  Only one LB is active at one time for the VIP and each LB is aware of the two apache servers.

As the UI is a single page application, no further interaction is required, unless the UI is able to conditionally load element resources.  All further communication for application content is handled from the browser using AJAX to the vote service.

#Vote Service

The vote service is a stateless JAX-RS JSON service. To achieve high availability and horizontal scalability multiple application servers running Wildfly 10.0.0 can be run.  The application servers must also be preceded by a redundant load balancer pair with a Virtual IP and DNS entry for the host name used for the rest endpoint.  Only one LB is active at any one time.  The incoming requests should we handled with a sticky session for optimal performance. Though the DB is clustered it is best for consecutive requests to come in via the same application server for a given client.  New sessions should be sent to the least loaded application server.

The application servers do not require any clustered connectivity as the service is stateless.  Howerver, it is important that all application servers share identical configuration as defined in the application.properties. Of most importance is the service secret and the 3rd party OAUth provider api key and secret.

The application WAR for the service guarantees a CONFIDENTIAL transport layer.  Therefore, it is necessary for the application servers to enable HTTPS.  This also requires the generation of a key pair.

#DB

The **vote-service** supports MySQL.  To achieve [high availability and horizontal scale](http://dev.mysql.com/doc/refman/5.7/en/ha-overview.html) the following options are available:
1. [Replicated](http://dev.mysql.com/doc/refman/5.7/en/replication.html)
2. Clusterd & Virtualized
3. Shared-Nothing, Geographically-Replicated Clusters. [MySQL Cluster](https://www.mysql.de/products/cluster/)

For basic HA and horizontal scale the replicated option is sufficent.

The already used [Connector/J](https://dev.mysql.com/doc/ndbapi/en/mccj-using-connectorj.html) driver for MySQL can be used. This requires specifying the JNDI connection url resource slightly differently for {
[replicated server](http://dev.mysql.com/doc/connector-j/5.1/en/connector-j-master-slave-replication-connection.html).

```
jdbc:mysql:replication://[master host][:port],[slave host 1][:port][,[slave host 2][:port]]...[/[database]][?propertyName1=propertyValue1[&propertyName2=propertyValue2]...]
```
