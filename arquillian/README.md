# Java EE: Acceptance testing with Arquillian

Start the [presentation](https://gclaussn.github.io/dev-demo/arquillian)

To configure Arquillian for a remote Wildfly container, change the following properties in ```arquillian.xml``` under src/test/resources:

- *host* and *managementAddress*: IP of the remote container
- *port* of the remote container
- *managementPort*: Port of the management endpoint, normally ```9990```
- *username*: Management user
- *password*: Management user's password

Please note, that the JBoss Wildfly management interface needs some start up time.
Check it manually via: http://management-address:9990/console/

## Build and run Wildfly Docker container

```
docker build --tag=wildfly-admin .
docker run --name wildfly -d -p 8080:8080 -p 9990:9990 -p 8787:8787 wildfly-admin
```

## Examples

All examples shown within the presentation can be found in [wildfly-remote](wildfly-remote/src/test/java/example) and [wildfly-docker](wildfly-docker/src/test/java/example).

## Links

- [Arquillian](http://arquillian.org/)
- [Arquillian Core](http://arquillian.org/arquillian-core/)
- [Arquillian Cube](http://arquillian.org/arquillian-cube/)
- [Dockerhub - JBoss Wildfly](https://hub.docker.com/r/jboss/wildfly/)