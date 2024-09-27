package hexlet.code.controller.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.RequestEntity.post;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;


@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    //@Autowired
    //private Faker faker;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;
    private User generateUser() {
        return Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .ignore(Select.field(User::getFirstName))
                .ignore(Select.field(User::getLastName))
                .supply(Select.field(User::getEmail), () -> "john@google.com")
                .supply(Select.field(User::getPassword), () -> "1234")
                .ignore(Select.field(User::getCreatedAt))
                .ignore(Select.field(User::getUpdatedAt))
                .create();
    }

    @Test
    void setUp() {
/*        var user = generateUser();
        userRepository.save(user);*/

    }

    @Test
    void index() throws Exception {
        var user = generateUser();
        var savedUser = userRepository.save(user);
        var result = mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk()).andReturn().getResponse();

        var body = result.getContentAsString();
        assertThatJson(body).isArray();
        //assertThatJson("{\"a\":1}").isObject().containsEntry("a", BigDecimal.valueOf(1));

    }

    @Test
    void show() {
    }

    @Test
    void delete() {
    }

/*    @Test
    void create() throws Exception {
        var data = generateUser();

        var request = post("/api/users/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isCreated());
    }*/

    @Test
    void update() {
    }
}