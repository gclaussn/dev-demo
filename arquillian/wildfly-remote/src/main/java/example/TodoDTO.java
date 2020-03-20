package example;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class TodoDTO {

  private Long id;

  @NotNull
  @Size(min = 1, max = 100)
  private String summary;

  @Size(min = 1, max = 255)
  private String description;

  public String getDescription() {
    return description;
  }

  public Long getId() {
    return id;
  }

  public String getSummary() {
    return summary;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }
}
