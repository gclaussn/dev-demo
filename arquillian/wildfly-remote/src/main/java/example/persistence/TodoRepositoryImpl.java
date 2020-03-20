package example.persistence;

import java.util.Date;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import example.TodoDTO;
import example.TodoRepository;

@Stateless
@Transactional
public class TodoRepositoryImpl implements TodoRepository {

  @PersistenceContext
  private EntityManager em;

  @Override
  public TodoDTO getById(Long id) {
    TodoEntity entity = find(id);

    TodoDTO todo = new TodoDTO();
    todo.setId(id);
    todo.setSummary(entity.getSummary());
    todo.setDescription(entity.getDescription());

    return todo;
  }

  @Override
  public TodoDTO save(TodoDTO todo) {
    TodoEntity entity = todo.getId() != null ? find(todo.getId()) : new TodoEntity();

    entity.setSummary(todo.getSummary());
    entity.setDescription(todo.getDescription());

    if (entity.getId() == null) {
      entity.setCreatedAt(new Date());
    }

    em.persist(entity);

    todo.setId(entity.getId());

    return todo;
  }

  protected TodoEntity find(Long id) {
    TodoEntity entity = em.find(TodoEntity.class, id);
    if (entity == null) {
      throw new EntityNotFoundException(String.format("Todo with ID %d could not be found", id));
    }
    return entity;
  }
}
