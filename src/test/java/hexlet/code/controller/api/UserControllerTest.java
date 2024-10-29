package hexlet.code.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserDTO;

import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.PasswordHashing;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.util.HashMap;
import java.util.List;


@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

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
                .ignore(Select.field(User::getTasks))
                .create();
    }

    private UserCreateDTO generateUserDTO(String email) {
        return Instancio.of(UserCreateDTO.class)
                .ignore(Select.field(UserCreateDTO::getFirstName))
                .ignore(Select.field(UserCreateDTO::getLastName))
                .supply(Select.field(UserCreateDTO::getEmail), () -> email)
                .supply(Select.field(UserCreateDTO::getPassword), () -> "1234")
                .create();
    }
    private User user;
    private User savedUser;

    private JwtRequestPostProcessor token;

    @BeforeEach
    public void setUp() {
        token = jwt().jwt(builder -> builder.subject("hexlet@example.com"));
        user = generateUser("john@google.com");
        if (userRepository.findByEmail(user.getEmail()).isEmpty()) {
            savedUser = userRepository.save(user);
            var passHash = PasswordHashing.getHashPass(user.getPassword());
        }
    }

    @Test
    void create() throws Exception {
        //var data = generateUser();
        //var userFind = generateUser("aaaa@google.com");
        //var savedUser = userRepository.save(user);
        //var passHash = PasswordHashing.getHashPass(userFind.getPassword());
        if (userRepository.findByEmail("aaaa@google.com").isPresent()) {
            userRepository.delete(userRepository.findByEmail("aaaa@google.com").get());
        }
        var userFind = generateUserDTO("aaaa@google.com");
        var passHash = PasswordHashing.getHashPass(userFind.getPassword());

        var request = MockMvcRequestBuilders.post("/api/users")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(userFind));

        mockMvc.perform(request)
                .andExpect(status().isCreated());
        var userTest = userRepository.findByEmail(userFind.getEmail()).get();

        assertNotNull(userTest);
        userRepository.delete(userTest);


    }

    @Test
    void checkValidate() throws Exception {
        //var data = generateUser();
        var userFind = generateUser("aaaa");
        //var savedUser = userRepository.save(user);
        var passHash = PasswordHashing.getHashPass(userFind.getPassword());

        var request = MockMvcRequestBuilders.post("/api/users")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(userFind));

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
        //var userTest = userRepository.findByEmail(userFind.getEmail()).get();

        //assertNotNull(userTest);


    }



    @Test
    void show() throws Exception {
        //var user = generateUser();
        //var savedUser = userRepository.save(user);
        var savedFind = userRepository.findByEmail("john@google.com").get();
        var id = savedFind.getId();
        var result = mockMvc.perform(get("/api/users/" + savedFind.getId()).with(jwt()))
                .andExpect(status().isOk()).andReturn().getResponse();
        var body = result.getContentAsString();
        assertThatJson(body).and(
                v -> v.node("email").isEqualTo(savedFind.getEmail()));
    }




    @Test
    void update() throws Exception {
        //var user = generateUser();
        //var savedUser = userRepository.save(user);
        /*var userCur = userRepository.findByEmail("john@google.com").get();*/
        var data = new HashMap<>();
        data.put("firstName", "Mike");
/*        var updateUser = new UserUpdateDTO();
        JsonNullable<String> nullableParam;
        updateUser.setFirstName("Mike");*/

        var request = MockMvcRequestBuilders.put("/api/users/" + savedUser.getId())
                .with(token)
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
        var result = mockMvc.perform(get("/api/users").with(jwt()))
                .andExpect(status().isOk()).andReturn().getResponse();

        var body = result.getContentAsString();
        assertThatJson(body).isArray();
        /*List<UserDTO> userDTOS = om.readValue(body,
                new TypeReference<List<UserDTO>>(){});*/
        List<UserDTO> userDTOS = om.readValue(body, new TypeReference<List<UserDTO>>() { });

        var actual = userDTOS.stream()
                .map(dto -> userMapper.map(dto, userRepository.findById(dto.getId()).get()))
                .toList();

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

        var request = mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/{id}", userCur.getId()).with(jwt()))
                .andExpect(status()
                .isNoContent());
    }
}
