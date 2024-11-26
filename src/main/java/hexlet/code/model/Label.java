package hexlet.code.model;

//import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToMany;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

//import java.time.Instant;
import java.util.Date;
import java.util.Set;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "labels")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Label implements BaseEntity  {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @NotNull
    @Column(unique = true)
    @Size(min = 3, max = 1000)
    private String name;
    @CreatedDate
    private Date createdAt;
    @ManyToMany(mappedBy = "labelsUsed")
    private Set<Task> tasks;

}
