package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;


//import java.time.Instant;
import java.util.Date;

@Getter
@Setter
public class UserDTO {

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")

    private Date createdAt;

}
