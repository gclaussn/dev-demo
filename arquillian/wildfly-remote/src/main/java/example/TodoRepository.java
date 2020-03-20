package example;

public interface TodoRepository {

  TodoDTO getById(Long id);

  TodoDTO save(TodoDTO todo);
}
