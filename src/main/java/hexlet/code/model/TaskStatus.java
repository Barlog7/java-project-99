package hexlet.code.model;

import jakarta.persistence.*;
//import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

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
    private LocalDate createdAt;
}
