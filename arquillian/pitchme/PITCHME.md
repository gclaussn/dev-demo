
<!-- .slide: data-background="white" -->
## Java EE:
## Acceptance testing with
## Arquillian

---
<img src="https://gitpitch.com/pitchme/cdn/github/gclaussn/dev-demo/master/C83E14B6F05BADEF8B5F29923C1B5E8DFACC8EA1C8453152EE526FE6362E19C66E9F217AC2ED491789AF8E2F97095572DAC66B0197DB9E660F066CF197FCE57347E26DBFD3521CD1C0A0D622615349789F429ACB36C9A2D6/arquillian/assets/img/but-when-i-do.jpg" title="" alt="But when I do I do it in production" class="But when I do I do it in production" style="width: auto;height: auto;" data-image-key="5625223047416576092"  />

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

+++
<!-- .slide: data-background-image="https://gitpitch.com/pitchme/cdn/github/gclaussn/dev-demo/master/C83E14B6F05BADEF8B5F29923C1B5E8DFACC8EA1C8453152EE526FE6362E19C66E9F217AC2ED491789AF8E2F97095572DAC66B0197DB9E660F066CF197FCE57347E26DBFD3521CD1C0A0D622615349789F429ACB36C9A2D6/arquillian/assets/img/test-automation.png" data-background-size="80% auto" data-background-position="center" data-background=" " data-background-repeat=" " data-background-transition="none" -->


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
<div class="north span-100">

<blockquote class=quote-text><i class="fa fa-quote-left" aria-hidden="true"> </i> No more mocks. No more container lifecycle and deployment hassles. Just real tests!</blockquote>

</div>

### Arquillian - Write Real Tests

- Manages Java EE container lifecycle
  - Embedded vs. Managed vs. Remote
- Handles bundling and deployment of archives
- Tests are executable from both IDE and build tool
  - Skip build for a faster turnaround
  - Enables debugging

+++
<!-- .slide: data-background-image="https://gitpitch.com/pitchme/cdn/github/gclaussn/dev-demo/master/C83E14B6F05BADEF8B5F29923C1B5E8DFACC8EA1C8453152EE526FE6362E19C66E9F217AC2ED491789AF8E2F97095572DAC66B0197DB9E660F066CF197FCE57347E26DBFD3521CD1C0A0D622615349789F429ACB36C9A2D6/arquillian/assets/img/arquillian.png" data-background-size="80% auto" data-background-position="center" data-background=" " data-background-repeat=" " data-background-transition="none" -->


+++
<!-- .slide: data-background-image="https://gitpitch.com/pitchme/cdn/github/gclaussn/dev-demo/master/C83E14B6F05BADEF8B5F29923C1B5E8DFACC8EA1C8453152EE526FE6362E19C66E9F217AC2ED491789AF8E2F97095572DAC66B0197DB9E660F066CF197FCE57347E26DBFD3521CD1C0A0D622615349789F429ACB36C9A2D6/arquillian/assets/img/arquillian-execution.png" data-background-size="80% auto" data-background-position="center" data-background=" " data-background-repeat=" " data-background-transition="none" -->


+++
### Container interaction styles

- **Remote**: Resides in separate JVM, deployment via remote protocol (HTTP or JMX)
- **Managed**: Like *Remote*, but startup and shutdown is managed by Arquillian
- **Embedded**: Resides in same JVM like test runner, container comes with test depedencies

+++
### Container configuration

arquillian.xml under src/test/resources

<pre><code class="hljs lang-">&lt;?xml version="1.0" encoding="UTF-8"?&gt;
&lt;arquillian&gt;
  &lt;defaultProtocol type="Servlet 3.0" /&gt;

  &lt;engine&gt;
    &lt;property name="deploymentExportPath"&gt;target/arquillian&lt;/property&gt;
  &lt;/engine&gt;

  &lt;container qualifier="wildfly" default="true"&gt;
    &lt;configuration&gt;
      &lt;property name="host"&gt;192.168.99.100&lt;/property&gt;
      &lt;property name="port"&gt;8080&lt;/property&gt;
      &lt;property name="managementAddress"&gt;192.168.99.100&lt;/property&gt;
      &lt;property name="managementPort"&gt;9990&lt;/property&gt;
      &lt;property name="username"&gt;admin&lt;/property&gt;
      &lt;property name="password"&gt;admin&lt;/property&gt;
    &lt;/configuration&gt;
  &lt;/container&gt;
&lt;/arquillian&gt;
</code></pre>


<span class="code-presenting-annotation fragment current-only" data-code-focus="11-16"></span>

---
Example: Todo Application

<img src="https://gitpitch.com/pitchme/cdn/github/gclaussn/dev-demo/master/C83E14B6F05BADEF8B5F29923C1B5E8DFACC8EA1C8453152EE526FE6362E19C66E9F217AC2ED491789AF8E2F97095572DAC66B0197DB9E660F066CF197FCE57347E26DBFD3521CD1C0A0D622615349789F429ACB36C9A2D6/arquillian/assets/img/todo-app.png" title="" alt="todo-app" class="todo-app" style="width: auto;height: auto;" data-image-key="3653388480541075009"  />

---
### Writing an Arquillian test

- Utilize test framework integration (for JUnit or TestNG)
- Create test archive, deployed to the Java EE container by Arquillian
- Write test methods in appropriate run mode
  - In container for testing components
  - As client for testing web services (REST/SOAP)

+++
<pre><code class="hljs lang-">@RunWith(Arquillian.class)
public class TodoResourceIT {

  @Deployment(testable = false)
  public static WebArchive createDeployment() {
    // Assemble todo.war
  }

  @ArquillianResource
  private URL url; // http://&lt;host&gt;:&lt;port&gt;/todo

  @Test
  @RunAsClient
  public void testResource() {
    // Assertions via REST client
  }
}
</code></pre>


<div class="south span-100 text-12">

<span class="code-presenting-annotation fragment current-only" data-code-focus="1">Arquillian JUnit integration via ```org.junit.runner.Runner``` implementation</span>
<span class="code-presenting-annotation fragment current-only" data-code-focus="4-7">Provide deployable web archive through static method annotated with ```@Deployment```</span>
<span class="code-presenting-annotation fragment current-only" data-code-focus="9-10">Enrich URL of deployed web archive</span>
<span class="code-presenting-annotation fragment current-only" data-code-focus="12-16">Mark test method to be executed on client side</span>

</div>

---
### ShrinkWrap API

- Fluent API to assemble deployment archives
- Full classpath control
- Supports WAR, JAR, and EJB *micro-deployments*

<div class="north span-100">

<blockquote class=quote-text><i class="fa fa-quote-left" aria-hidden="true"> </i> allows you to skip the build and instead define a deployment archive declaratively in Java code</blockquote>

</div>

+++
<pre><code class="hljs lang-">@Deployment(testable = false)
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
</code></pre>


<div class="south span-100 text-12">

<span class="code-presenting-annotation fragment current-only" data-code-focus="3-8">Resolve compile and runtime dependencies from POM</span>
<span class="code-presenting-annotation fragment current-only" data-code-focus="10">Create web archive called *todo.war*</span>
<span class="code-presenting-annotation fragment current-only" data-code-focus="11-13">Use fluent API to assemble web archive with libraries, classes and resources</span>

</div>

+++
Example for a client side test

<pre><code class="hljs lang-">@ArquillianResource
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
  assertThat(res.getStatus(), is(Status.BAD&#95;REQUEST.getStatusCode()));
}
</code></pre>


<span class="code-presenting-annotation fragment current-only" data-code-focus="1-13"></span>
<span class="code-presenting-annotation fragment current-only" data-code-focus="15-20"></span>

---
Example: Classpath control

<img src="https://gitpitch.com/pitchme/cdn/github/gclaussn/dev-demo/master/C83E14B6F05BADEF8B5F29923C1B5E8DFACC8EA1C8453152EE526FE6362E19C66E9F217AC2ED491789AF8E2F97095572DAC66B0197DB9E660F066CF197FCE57347E26DBFD3521CD1C0A0D622615349789F429ACB36C9A2D6/arquillian/assets/img/classpath-control.png" title="" alt="classpath-control" class="classpath-control" style="width: auto;height: auto;" data-image-key="-4428704254121973196"  />

+++
<pre><code class="hljs lang-">@Alternative
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
</code></pre>


<div class="south span-100 text-12">

<span class="code-presenting-annotation fragment current-only" data-code-focus="1-2,6">Define annotation for alternative CDI beans</span>
<span class="code-presenting-annotation fragment current-only" data-code-focus="9,11">Annotate ```SpellChecker``` mock implementation as alternative</span>

</div>

+++
<pre><code class="hljs lang-">@Deployment(testable = false)
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
</code></pre>


<div class="south span-100 text-12">

<span class="code-presenting-annotation fragment current-only" data-code-focus="8-9">Add spell checker mock implementation</span>
<span class="code-presenting-annotation fragment current-only" data-code-focus="10">Prefer alternative implementations annotated with ```@Mock```</span>
<span class="code-presenting-annotation fragment current-only" data-code-focus="11-12">Enable spell checking feature</span>

</div>

---
Example: Deployment control

<img src="https://gitpitch.com/pitchme/cdn/github/gclaussn/dev-demo/master/C83E14B6F05BADEF8B5F29923C1B5E8DFACC8EA1C8453152EE526FE6362E19C66E9F217AC2ED491789AF8E2F97095572DAC66B0197DB9E660F066CF197FCE57347E26DBFD3521CD1C0A0D622615349789F429ACB36C9A2D6/arquillian/assets/img/deployment-control.png" title="" alt="deployment-control" class="deployment-control" style="width: auto;height: auto;" data-image-key="-9009735677868828247"  />

+++
<pre><code class="hljs lang-">@WebServlet("/check")
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

    res.setContentType(MediaType.TEXT&#95;PLAIN);
    res.setStatus(Status.OK.getStatusCode());
    res.getWriter().write(text);
    res.getWriter().write(" (spell checked by stub)");
  }
}
</code></pre>


+++
<pre><code class="hljs lang-">@Deployment(order = 1, testable = false, name = "spell-checker")
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
</code></pre>


<div class="south span-100 text-12">

<span class="code-presenting-annotation fragment current-only" data-code-focus="1,5">Define additional ```spell-checker``` deployment, which stubs the real spell checker service</span>
<span class="code-presenting-annotation fragment current-only" data-code-focus="7">Add the stub implementation</span>
<span class="code-presenting-annotation fragment current-only" data-code-focus="10-11">Define default deployment</span>
<span class="code-presenting-annotation fragment current-only" data-code-focus="17-18">Enable feature and set service URL, required by the REST client to perform a spell check</span>

</div>

---
### Remote debugging
### (with Eclipse)

1. Enable remote debugging at container via Java options
2. Start debugger: Run -> Debug Configurations -> Remote Java Application -> Debug
3. Set breakpoint(s)
4. Right click on Arquillian test -> Debug As -> JUnit Test

+++
<!-- .slide: data-background-image="https://gitpitch.com/pitchme/cdn/github/gclaussn/dev-demo/master/C83E14B6F05BADEF8B5F29923C1B5E8DFACC8EA1C8453152EE526FE6362E19C66E9F217AC2ED491789AF8E2F97095572DAC66B0197DB9E660F066CF197FCE57347E26DBFD3521CD1C0A0D622615349789F429ACB36C9A2D6/arquillian/assets/img/remote-debug-configuration.png" data-background-size="80% auto" data-background-position="center" data-background=" " data-background-repeat=" " data-background-transition="none" -->


+++
<!-- .slide: data-background-image="https://gitpitch.com/pitchme/cdn/github/gclaussn/dev-demo/master/C83E14B6F05BADEF8B5F29923C1B5E8DFACC8EA1C8453152EE526FE6362E19C66E9F217AC2ED491789AF8E2F97095572DAC66B0197DB9E660F066CF197FCE57347E26DBFD3521CD1C0A0D622615349789F429ACB36C9A2D6/arquillian/assets/img/remote-debug.png" data-background-size="80% auto" data-background-position="center" data-background=" " data-background-repeat=" " data-background-transition="none" -->


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

<pre><code class="hljs lang-">&lt;extension qualifier="docker"&gt;
  &lt;property name="serverVersion"&gt;19.03.5&lt;/property&gt;
  &lt;property name="serverUri"&gt;tcp://192.168.99.100:2376&lt;/property&gt;
  &lt;property name="tlsVerify"&gt;true&lt;/property&gt;
  &lt;property name="certPath"&gt;...&lt;/property&gt;
  &lt;property name="definitionFormat"&gt;CUBE&lt;/property&gt;
  &lt;property name="dockerContainers"&gt;
    wildfly:
      image: wildfly-admin
      exposedPorts: [8080/tcp, 9990/tcp, 8787/tcp]
      await:
        strategy: polling
        sleepPollingTime: 5s
        iterations: 10
      portBindings: [8080-&gt;8080/tcp, 9990-&gt;9990/tcp, 8787-&gt;8787/tcp]
  &lt;/property&gt;
&lt;/extension&gt;

&lt;container qualifier="wildfly" default="true"&gt;
  &lt;!-- ... --&gt;
&lt;/container&gt;
</code></pre>


<span class="code-presenting-annotation fragment current-only" data-code-focus="1">Cube provides an Arquillian extension named ```docker```</span>
<span class="code-presenting-annotation fragment current-only" data-code-focus="2-5">Configure communication with Docker host</span>
<span class="code-presenting-annotation fragment current-only" data-code-focus="6-16">Define Docker containers to start - CUBE vs COMPOSE</span>
<span class="code-presenting-annotation fragment current-only" data-code-focus="8,19">Docker container must match the qualifier of the Java EE container</span>

+++
Provided enrichers

<pre><code class="hljs lang-">// Get configured docker-java client
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
@RouteURL("&lt;service-name&gt;")
URL url;
</code></pre>


---
### Lessons learned

- Use remote container for local development
  - Fast, since no server start up needed

- Docker Image
  - Application server + H2 in-memory database
  - Second screen with logs (```docker logs -f```)
  - Remote debugging enabled

<pre><code class="hljs lang-">ENV JAVA&#95;TOOL&#95;OPTIONS \
"-agentlib:jdwp=transport=dt&#95;socket,address=&#42;:8787,server=y,suspend=n"
</code></pre>


+++
- Don't repeat yourself
  - Abstract super class handles ```@Deployment```
  - Create customizable ```WebArchive``` factory class

<pre><code class="hljs lang-">@Deployment(testable = false)
public static WebArchive createDeployment() {
  return WebArchiveFactory.create(webArchive -&gt; {
    // lambda consumer function for customization

    webArchive.addAsResource("custom.properties");
    webArchive.addClass(Custom.class);
  });
}
</code></pre>


---
## Thank you!

Source code on Github:

[gclaussn/dev-demo/arquillian](https://github.com/gclaussn/dev-demo/tree/master/arquillian)