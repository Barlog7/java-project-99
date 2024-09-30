package hexlet.code.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.JsonProcessingException;
import hexlet.code.dto.UserDTO;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.PasswordHashing;
import net.datafaker.Faker;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.RequestEntity.post;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashMap;
import java.util.List;


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


    private User generateUser(String email) {
        return Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .ignore(Select.field(User::getFirstName))
                .ignore(Select.field(User::getLastName))
                .supply(Select.field(User::getEmail), () -> email)
                .supply(Select.field(User::getPassword), () -> "1234")
                .ignore(Select.field(User::getCreatedAt))
                .ignore(Select.field(User::getUpdatedAt))
                .create();
    }
    User user;
    User savedUser;


  @BeforeEach
  public void setUp() {
      //System.out.printf("1");
        user = generateUser("john@google.com");
     if (userRepository.findByEmail(user.getEmail()).isEmpty()) {
         savedUser = userRepository.save(user);
         var passHash = PasswordHashing.getHashPass(user.getPassword());
     }
    }

    @Test
    void create() throws Exception {
        //var data = generateUser();
        var user = generateUser("aaaa@google.com");
        //var savedUser = userRepository.save(user);
        var passHash = PasswordHashing.getHashPass(user.getPassword());

        var request = MockMvcRequestBuilders.post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(user));

        mockMvc.perform(request)
                .andExpect(status().isCreated());
        var userTest = userRepository.findByEmail(user.getEmail()).get();

        assertNotNull(userTest);


    }



    @Test
    void show() throws Exception {
        //var user = generateUser();
        //var savedUser = userRepository.save(user);
        var savedUser = userRepository.findByEmail("john@google.com").get();
        var id = savedUser.getId();
        var result = mockMvc.perform(get("/api/users/"+ savedUser.getId()))
                .andExpect(status().isOk()).andReturn().getResponse();
        var body = result.getContentAsString();
        assertThatJson(body).and(
                v -> v.node("email").isEqualTo(savedUser.getEmail()));
    }




    @Test
    void update() throws Exception {
        //var user = generateUser();
        //var savedUser = userRepository.save(user);
        /*var userCur = userRepository.findByEmail("john@google.com").get();*/
        var data = new HashMap<>();
        data.put("firstName", "Mike");

        var request = MockMvcRequestBuilders.put("/api/users/" + savedUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var userTest = userRepository.findById(savedUser.getId()).get();
        assertThat(userTest.getFirstName()).isEqualTo(("Mike"));
    }

    @Test
    void index() throws Exception {
        //var user = generateUser();
        //var passHash = PasswordHashing.getHashPass(user.getPassword());
        //user.setPassword(passHash);

        //var savedUser = userRepository.save(user);
        var result = mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk()).andReturn().getResponse();

        var body = result.getContentAsString();
        assertThatJson(body).isArray();
        /*List<UserDTO> userDTOS = om.readValue(body,
                new TypeReference<List<UserDTO>>(){});*/
        List<UserDTO> userDTOS = om.readValue(body, new TypeReference<List<UserDTO>>(){});

        var actual = userDTOS.stream().map(dto -> userMapper.map(dto, userRepository.findById(dto.getId()).get())   ).toList();

        var expected = userRepository.findAll();
        //var userDTOExp = expected.stream().map(userMapper::mapModel).toList();

        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
        //assertThat(userDTOS).containsExactlyInAnyOrderElementsOf(userDTOExp);
    }

    @Test
    void delete() throws Exception {
        var userCur = userRepository.findByEmail("john@google.com").get();
        /*var user = generateUser();
        var savedUser = userRepository.save(user);*/

        var request = mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/{id}", userCur.getId())).andExpect(status().isNoContent());
    }
}