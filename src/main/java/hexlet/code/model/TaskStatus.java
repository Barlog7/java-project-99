package hexlet.code.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Column;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "task_status")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class TaskStatus implements BaseEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @NotNull
    @Column(unique = true)
    @Size(min = 1)
    private String name;
    @NotNull
    @Column(unique = true)
    @Size(min = 1)
    private String slug;
    @CreatedDate
    private Date createdAt;

    @JsonIgnore
    @OneToMany(mappedBy = "taskStatus", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Task> tasks = new ArrayList<>();

    public void addTask(Task task) {
        tasks.add(task);
        task.setTaskStatus(this);
    }

    public void removeTask(Task task) {
        tasks.remove(task);
    }
}
