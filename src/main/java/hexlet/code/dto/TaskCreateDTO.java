package hexlet.code.dto;

import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
public class TaskCreateDTO {

    private Integer index;
    private Integer assigneeId;
    private String title;
    private String content;
    private String status;

}
