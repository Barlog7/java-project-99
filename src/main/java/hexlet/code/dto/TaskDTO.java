package hexlet.code.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;


@Getter
@Setter
public class TaskDTO {
    private Long id;
    private Integer index;
    private Instant createdAt;
    private Integer assigneeId;
    private String title;
    private String content;
    private String status;
}
