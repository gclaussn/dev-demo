package example;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.arquillian.cube.impl.util.IOUtil;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class GreetingServletIT {

  @Deployment(testable = false)
  public static WebArchive createDeployment() {
    return ShrinkWrap.create(WebArchive.class, "greeting.war").addClass(GreetingServlet.class);
  }

  @ArquillianResource
  private URL url;
  
  @Test
  @RunAsClient
  public void testGreetings() throws IOException {
    URL target = new URL(url, "greetings");

    HttpURLConnection connection = (HttpURLConnection) target.openConnection();

    String greetings = IOUtil.asString(connection.getInputStream());
    assertThat(greetings, equalTo("Greetings from Docker container"));
  }
}
