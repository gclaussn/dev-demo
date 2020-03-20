package example;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import example.property.Property;
import example.spellcheck.SpellChecker;

@Path("/todos")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TodoResource {

  @Inject
  @Property("spellChecker.enabled")
  protected Boolean spellCheck;

  @Inject
  protected TodoRepository repository;

  @Inject
  protected SpellChecker spellChecker;

  @POST
  public TodoDTO create(@Valid TodoDTO todo) {
    if (spellCheck != null && spellCheck) {
      String description = spellChecker.check(todo.getDescription());
      todo.setDescription(description);
    }

    return repository.save(todo);
  }

  @GET
  @Path("/{id}")
  public TodoDTO getById(@PathParam("id") Long id) {
    return repository.getById(id);
  }
}
