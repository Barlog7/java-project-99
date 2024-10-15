package hexlet.code.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "task_status")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
//@EqualsAndHashCode(of = {"email"})
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
    private Instant createdAt;

    @JsonIgnore
    @OneToMany(mappedBy = "taskStatus", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Task> tasks = new ArrayList<>();

    public void addTask(Task task) {
        tasks.add(task);
        task.setTaskStatus(this);
    }

    public void removeTask(Task task) {
        tasks.remove(task);
        //task.setTaskStatus(null);
    }
}
