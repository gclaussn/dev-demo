package example.spellcheck;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import example.property.Property;

@ApplicationScoped
public class SpellCheckerClient implements SpellChecker {

  @Inject
  @Property("spellChecker.url")
  private String url;

  private Client client;

  @PostConstruct
  public void init() {
    client = new ResteasyClientBuilder().build();
  }

  @Override
  public String check(String text) {
    Response response = client.target(url).request().post(Entity.text(text));
    if (response.getStatus() != Status.OK.getStatusCode()) {
      throw new RuntimeException(String.format("Spell check failed with status %d", response.getStatus()));
    }
    return response.readEntity(String.class);
  }
}
