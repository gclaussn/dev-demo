FROM jboss/wildfly:18.0.1.Final

# Management user
RUN /opt/jboss/wildfly/bin/add-user.sh admin admin --silent

# Enable remote debugging
ENV JAVA_TOOL_OPTIONS "-agentlib:jdwp=transport=dt_socket,address=*:8787,server=y,suspend=n"

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]