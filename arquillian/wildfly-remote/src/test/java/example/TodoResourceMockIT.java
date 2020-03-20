package example;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.net.URL;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import example.spellcheck.SpellChecker;
import test.Mock;
import test.SpellCheckerMock;

/**
 * Integration test with spell checking against a mock, an alternative implementation of the
 * {@link SpellChecker} interface.
 * 
 * @see SpellCheckerMock
 * @see Mock
 */
@RunWith(Arquillian.class)
public class TodoResourceMockIT {

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
      .addClass(Mock.class)
      .addClass(SpellCheckerMock.class)
      .addAsWebInfResource("mock-beans.xml", "beans.xml")
      .addAsResource("test.properties", "application.properties")
      .addAsResource("META-INF/h2.xml", "META-INF/persistence.xml");
  }

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
  public void testCreateWithSpellCheckerMock() {
    TodoDTO todo = new TodoDTO();
    todo.setSummary("A summary");
    todo.setDescription("A description");

    Response response = webTarget.request().post(Entity.json(todo));
    assertThat(response.getStatus(), is(Status.OK.getStatusCode()));

    TodoDTO actual = response.readEntity(TodoDTO.class);
    assertThat(actual.getId(), notNullValue());
    assertThat(actual.getSummary(), equalTo(todo.getSummary()));
    assertThat(actual.getDescription(), equalTo(String.format("%s (spell checked by mock)", todo.getDescription())));
  }
}
