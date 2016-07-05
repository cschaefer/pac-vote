# PAC Voting Application
PAC Assignment - Voting Service

## Quick Start

To get started you will need:
1. to install [Maven 3.3+](https://maven.apache.org/download.cgi)
2. to install [Wildfly 10.0.0](http://wildfly.org/downloads/)
3. to install [MySQL 5.5](https://dev.mysql.com/downloads/mysql/5.5.html)
4. to use [MySQL Connector/J](https://dev.mysql.com/downloads/connector/j/)
5. to install [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
6. check out this repository
7. optionally check out the [polymer-ui](https://github.com/cschaefer/pac-vote-ui) repository for a working web application

## Introduction

This maven repository forms the service deploy unit for a voting application. It offers a secured JAX-RS service API that can be used by web applications or mobile applications.  A working mobile web application can already be found under [polymer-ui](https://github.com/cschaefer/pac-vote-ui).

The JAX-RS service is built using a JEE7 stack which is assumed to run in Wildfly.

The service allows users to become registered if authenticated via OAuth2.  Currently only the github OAuth2 provider is supported.

## Project Structure

**pac-vote-service** A top level module defining the API and its implementation.

**pac-vote-service-api** Defines the service API.
The API contains the following components:
1. Service interfaces for JAX-RS services.
2. Local interfaces for internal stateless session beans.
3. Domain objects for JPA 2.1 beans.
4. Transfer objects for some aspects of the JAX-RS service interfaces.
5. Business logic  exceptions.
6. Interfaces for JMX beans.
7. Annotation for security, monitoring and application property configuration.

**pac-vote-service-impl** Contains the implementation of the service API.
The implementation module contains two primary sets of stateless session beans.
1. In the impl package are the stateless sessions beans which conduct the business logic.
2. In the repo package are the stateless session beans which conduct JPA persistence logic.

In addition to the two sets of stateless sesssion beans are:
1. decorators primarily for extra logging
2. interceptors, service and beans for monitoring
3. producers for various purposes, mainly security, property configuration, logging.
4. filters for CORS and authorization

This module provides in container tests using Arquillian.

**pac-vote-web** A top level module defining the application and WAR packaging for the JAX-RS service.

**pac-vote-test** A top level module defining end to end tests.

This module contains primarily client mode tests using Arquilian. The tests exercise the JAX-RS service along with the JPA persistence.

**pac-vote-assembly** A top level module defining an release unit that can be given to an Ops team for deployment.

## Service Architecture

The service is divided into two primary layers, being a) business logic and b) persistence logic.

### Voting Logic - JAX-RS REST Service

The JAX-RS API for voting is offered by the BallotService, VoteService and UserService.  All services are based on JSON format data transfer.

The services allow for limited access for unauthenticated users. Such services are not annotated with security annotations or carry optional security annotations.

To gain full access to the services a user must register via the UserService using a valid OAuth2 access token.  This token will be validated and currently only the GitHub OAuth2 provider is supported.  This requires that the service and application both use the same client API ID and secret.

Once validated a user is issued a [JWT - JSON Web Token](https://jwt.io/).  It is important that the application submit the JWT for any secured operations.  The JWT is managed by a secret configured in and only known to the voting service.  The JWT must be submitted in the HTTP request header as shown:
```
"Authorization: Bearer <jwt>
```

Business operations carry at most one of the following secured annotations.
```
@OptionalSecured
```
The operation optionally requires a valid JWT for a user known to the voting service.  Such operations will returned  enhanced information when triggered by an authoirzed user.

```
@Secured
```
The operation requires a valid JWT for a user known to the voting service.

```
@AdministratorSecured
```
The operation requires a valid JWT for an administrator level user known to the voting service.

## Voting Persistence - JPA

The business services have corresponding persistence services for CRUD operations.  The domain model objects also defined named queries used in the persistence beans.

## Voting Authentication & Authorization

The authentication is performed by an OAuth provider.  In this case only GitHub is currently supported. By sharing a common API key and secret for the voting application registered at GitHub, the front end is able to register users.  At the moment it is assumed the frontend is able to handle the callback cycle trigged by OAuth2 authentication.  The front end must then pass the authToken as part of the user object used by UserService#register(OAuthUnauthorizedUser).

To enable GitHub as an OAuth2 provider, the application must be [registered](https://github.com/settings/developers).  The application owner must specify a callback URL to handle the OAuth2 cycle and receives an API key and secret.  This service layer does not handle the callback cycle, instead it uses the API key and secret to validate the generated authToken.

A helpful service for handling OAuth2 authentication for many different providers is from [Firebase](http://www.firebase.com). Firebase handles many [different OAuth2 providers](https://www.firebase.com/docs/web/guide/user-auth.html)  An application owner can register the application with Firebase and enable the account to handle [GitHub as OAuth2 provider](https://www.firebase.com/docs/web/guide/login/github.html).  Therefore, Firebase negotiates the OAuth2 cycle and returns the authToken.  

Once the third party OAuth2 provider authToken is validated the service takes over and issues the user an encrypted JWT.  The JWT is required for any further secured operations which depend on the identity of the user.

## Voting Monitoring

When configured with the additional required JARs from Wildfly JConsole can be used to obtain performance statistics from the services.  This is particular aimed at quality and production environments in order to ascertain the root cause of performance issues.  

Possible MX Bean operatoins are:
1. enable - start collecting
2. disable - stop collecting and discard all statistics
3. clear - keep collecting and discard all statistics
4. print - dump statistics in a csv file detailing per monitored service method the min, max, total runtime in milliseconds, as well as number of calls.

For a service call to be monitored the
```
@Monitored
```
annotation must be used.

## Configuration

### HTTPS
To provide a secure transfer of confidential voting the Wildfly application server must be configured to enabled HTTPS. An example configuration guide [for Wildfly 8 describes this process](http://blog.eisele.net/2015/01/ssl-with-wildfly-8-and-undertow.html).

### Relational Database
The create.sql script from the assembly must be applied to the database.  In this case a mySQL 5.5 database is supported.

The MySQL driver can be deployed to Wildfly and configured in the [management console](http://<wildfly hos>:9990/).  The application assumes a JNDI resource called
```
java:jboss/datasources/VoteDS
```

## Development guide


## Naming conventions

## Build Process

## Release Management
