## Java EE:
## Acceptance testing with
## Arquillian

---

![But when I do I do it in production](arquillian/assets/img/but-when-i-do.jpg)

---

### How important is testing in software engineering?

### Do you have to write tests?

+++

"It depends!"

(the universal answer to all questions in IT)

+++

### Purpose of writing tests

- reduce bugs
- reduce effort of manual testing
- avoid regression
  - when adding new or changing existing features
  - when new developers join
- ensure correctness of critical applications
  - payment / finance / security / health care
- test coverage as acceptance criteria

+++?image=arquillian/assets/img/test-automation.png&size=80% auto

---

### Java EE testing challenges

- Complicated runtime environment
  - Shared resources and configuration
  - Class loading
- Various Java EE APIs
  - CDI
  - JPA / JTA
  - Servlet / JAX-RS / JAX-WS, Bean Validation
  - etc.

---

@snap[north span-100]
@quote[No more mocks. No more container lifecycle and deployment hassles. Just real tests!]
@snapend

### Arquillian - Write Real Tests

- Manages Java EE container lifecycle
  - Embedded vs. Managed vs. Remote
- Handles bundling and deployment of archives
- Tests are executable from both IDE and build tool
  - Skip build for a faster turnaround
  - Enables debugging

+++?image=arquillian/assets/img/arquillian.png&size=80% auto

+++?image=arquillian/assets/img/arquillian-execution.png&size=80% auto

+++

### Container interaction styles

- **Remote**: Resides in separate JVM, deployment via remote protocol (HTTP or JMX)
- **Managed**: Like *Remote*, but startup and shutdown is managed by Arquillian
- **Embedded**: Resides in same JVM like test runner, container comes with test depedencies

+++

### Container configuration

arquillian.xml under src/test/resources

```xml
<?xml version="1.0" encoding="UTF-8"?>
<arquillian>
  <defaultProtocol type="Servlet 3.0" />

  <engine>
    <property name="deploymentExportPath">target/arquillian</property>
  </engine>

  <container qualifier="wildfly" default="true">
    <configuration>
      <property name="host">192.168.99.100</property>
      <property name="port">8080</property>
      <property name="managementAddress">192.168.99.100</property>
      <property name="managementPort">9990</property>
      <property name="username">admin</property>
      <property name="password">admin</property>
    </configuration>
  </container>
</arquillian>
```

@[11-16]

---

Example: Todo Application

![todo-app](arquillian/assets/img/todo-app.png)

---

### Writing an Arquillian test

- Utilize test framework integration (for JUnit or TestNG)
- Create test archive, deployed to the Java EE container by Arquillian
- Write test methods in appropriate run mode
  - In container for testing components
  - As client for testing web services (REST/SOAP)

+++

```java
@RunWith(Arquillian.class)
public class TodoResourceIT {

  @Deployment(testable = false)
  public static WebArchive createDeployment() {
    // Assemble todo.war
  }

  @ArquillianResource
  private URL url; // http://<host>:<port>/todo

  @Test
  @RunAsClient
  public void testResource() {
    // Assertions via REST client
  }
}
```

@snap[south span-100 text-12]
@[1](Arquillian JUnit integration via ```org.junit.runner.Runner``` implementation)
@[4-7](Provide deployable web archive through static method annotated with ```@Deployment```)
@[9-10](Enrich URL of deployed web archive)
@[12-16](Mark test method to be executed on client side)
@snapend

---

### ShrinkWrap API

- Fluent API to assemble deployment archives
- Full classpath control
- Supports WAR, JAR, and EJB *micro-deployments*

@snap[north span-100]
@quote[allows you to skip the build and instead define a deployment archive declaratively in Java code]
@snapend

+++

```java
@Deployment(testable = false)
public static WebArchive createDeployment() {
  File[] libs = Maven.resolver()
    .loadPomFromFile("pom.xml")
    .importCompileAndRuntimeDependencies()
    .resolve()
    .withTransitivity()
    .asFile();

  return ShrinkWrap.create(WebArchive.class, "todo.war")
    .addAsLibraries(libs)
    .addPackages(true, TodoApplication.class.getPackage())
    .addAsResource("META-INF/h2.xml", "META-INF/persistence.xml");
}
```

@snap[south span-100 text-12]
@[3-8](Resolve compile and runtime dependencies from POM)
@[10](Create web archive called *todo.war*)
@[11-13](Use fluent API to assemble web archive with libraries, classes and resources)
@snapend

+++

Example for a client side test

```java
@ArquillianResource
private URL url;

private WebTarget webTarget;

@Before
public void setUp() {
  webTarget = new ResteasyClientBuilder()
    .register(JacksonJsonProvider.class)
    .build()
    .target(url.toString())
    .path("/api/todos");
}

@Test
@RunAsClient
public void testValidation() {
  Response res = webTarget.request().post(Entity.json(new TodoDTO()));
  assertThat(res.getStatus(), is(Status.BAD_REQUEST.getStatusCode()));
}
```

@[1-13]
@[15-20]

---

Example: Classpath control

![classpath-control](arquillian/assets/img/classpath-control.png)

+++

```java
@Alternative
@Stereotype
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface Mock {
}

@Mock
@ApplicationScoped
public class SpellCheckerMock implements SpellChecker {

  @Override
  public String check(String text) {
    // Mark text as spell checked
    return String.format("%s (spell checked by mock)", text);
  }
}
```

@snap[south span-100 text-12]
@[1-2,6](Define annotation for alternative CDI beans)
@[9,11](Annotate ```SpellChecker``` mock implementation as alternative)
@snapend

+++

```java
@Deployment(testable = false)
public static WebArchive createDeployment() {
  File[] libs = // ...

  return ShrinkWrap.create(WebArchive.class, "todo.war")
    .addAsLibraries(libs)
    .addPackages(true, TodoApplication.class.getPackage())
    .addClass(Mock.class)
    .addClass(SpellCheckerMock.class)
    .addAsWebInfResource("mock-beans.xml", "beans.xml")
    // spellChecker.enabled=true
    .addAsResource("test.properties", "application.properties")
    .addAsResource("META-INF/h2.xml", "META-INF/persistence.xml");
}
```

@snap[south span-100 text-12]
@[8-9](Add spell checker mock implementation)
@[10](Prefer alternative implementations annotated with ```@Mock```)
@[11-12](Enable spell checking feature)
@snapend

---

Example: Deployment control

![deployment-control](arquillian/assets/img/deployment-control.png)

+++

```java
@WebServlet("/check")
public class SpellCheckerStub extends HttpServlet {

  private static final long serialVersionUID = 1L;

  @Override
  protected void doPost(
    HttpServletRequest req,
    HttpServletResponse res)
  throws ServletException, IOException {
    String text = req.getReader()
      .lines()
      .collect(Collectors.joining());

    res.setContentType(MediaType.TEXT_PLAIN);
    res.setStatus(Status.OK.getStatusCode());
    res.getWriter().write(text);
    res.getWriter().write(" (spell checked by stub)");
  }
}
```

+++

```java
@Deployment(order = 1, testable = false, name = "spell-checker")
public static WebArchive createSpellCheckerDeployment() {
  File[] libs = // ...
  
  return ShrinkWrap.create(WebArchive.class, "spell-checker.war")
    .addAsLibraries(libs)
    .addClass(SpellCheckerStub.class);
}

@Deployment(order = 2, testable = false)
public static WebArchive createDeployment() {
  File[] libs = // ...
  
  return ShrinkWrap.create(WebArchive.class, "todo.war")
    .addAsLibraries(libs)
    .addPackages(true, TodoApplication.class.getPackage())
    // spellChecker.url=http://localhost:8080/spell-checker/check
    .addAsResource("test.properties", "application.properties")
    .addAsResource("META-INF/h2.xml", "META-INF/persistence.xml");
}
```

@snap[south span-100 text-12]
@[1,5](Define additional ```spell-checker``` deployment, which stubs the real spell checker service)
@[7](Add the stub implementation)
@[10-11](Define default deployment)
@[17-18](Enable feature and set service URL, required by the REST client to perform a spell check)
@snapend

---

### Remote debugging
### (with Eclipse)

1. Enable remote debugging at container via Java options
2. Start debugger: Run -> Debug Configurations -> Remote Java Application -> Debug
3. Set breakpoint(s)
4. Right click on Arquillian test -> Debug As -> JUnit Test

+++?image=arquillian/assets/img/remote-debug-configuration.png&size=80% auto

+++?image=arquillian/assets/img/remote-debug.png&size=80% auto

---

### Arquillian Cube

- Manages Docker container lifecycle
  - Supports start of multiple containers
  - Supports ```docker-compose.yml``` files
- Communication with Docker via ```docker-java```
  - Support for UNIX socket and TCP (with TLS)
- Extensions for Kubernetes, Fabric8 and Openshift

+++

arquillian.xml under src/test/resources

```xml
<extension qualifier="docker">
  <property name="serverVersion">19.03.5</property>
  <property name="serverUri">tcp://192.168.99.100:2376</property>
  <property name="tlsVerify">true</property>
  <property name="certPath">...</property>
  <property name="definitionFormat">CUBE</property>
  <property name="dockerContainers">
    wildfly:
      image: wildfly-admin
      exposedPorts: [8080/tcp, 9990/tcp, 8787/tcp]
      await:
        strategy: polling
        sleepPollingTime: 5s
        iterations: 10
      portBindings: [8080->8080/tcp, 9990->9990/tcp, 8787->8787/tcp]
  </property>
</extension>

<container qualifier="wildfly" default="true">
  <!-- ... -->
</container>
```

@[1](Cube provides an Arquillian extension named ```docker```)
@[2-5](Configure communication with Docker host)
@[6-16](Define Docker containers to start - CUBE vs COMPOSE)
@[8,19](Docker container must match the qualifier of the Java EE container)

+++

Provided enrichers

```java
// Get configured docker-java client
@ArquillianResource
DockerClient dockerClient;

// Get IP of Docker host
@HostIp
String ip;

// Get binding for exposed port 8080 of container tomcat
@HostPort(containerName = "tomcat", value = 8080)
int tomcatPort;

// org.arquillian.cube:arquillian-cube-openshift:${version}
// When running against Openshift, retrieve route from service
@RouteURL("<service-name>")
URL url;
```

---

### Lessons learned

- Use remote container for local development
  - Fast, since no server start up needed

- Docker Image
  - Application server + H2 in-memory database
  - Second screen with logs (```docker logs -f```)
  - Remote debugging enabled

```
ENV JAVA_TOOL_OPTIONS \
"-agentlib:jdwp=transport=dt_socket,address=*:8787,server=y,suspend=n"
```

+++

- Don't repeat yourself
  - Abstract super class handles ```@Deployment```
  - Create customizable ```WebArchive``` factory class

```java
@Deployment(testable = false)
public static WebArchive createDeployment() {
  return WebArchiveFactory.create(webArchive -> {
    // lambda consumer function for customization

    webArchive.addAsResource("custom.properties");
    webArchive.addClass(Custom.class);
  });
}
```

---

## Thank you!

Source code on Github:

[gclaussn/dev-demo/arquillian](https://github.com/gclaussn/dev-demo/tree/master/arquillian)