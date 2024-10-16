package hexlet.code.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class LabelDTO {
    private Long id;
    private String name;
    private Instant createdAt;
}
