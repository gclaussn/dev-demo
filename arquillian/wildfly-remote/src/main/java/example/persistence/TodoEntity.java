package example.persistence;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "todo")
public class TodoEntity {

  @Id
  @GeneratedValue
  private Long id;

  @Column(nullable = false, length = 100)
  private String summary;
  @Column(length = 255)
  private String description;

  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdAt;

  public Date getCreatedAt() {
    return createdAt;
  }

  public String getDescription() {
    return description;
  }

  public Long getId() {
    return id;
  }

  public String getSummary() {
    return summary;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }
}
