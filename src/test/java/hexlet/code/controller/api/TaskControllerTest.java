package hexlet.code.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.dto.TaskDTO;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import org.instancio.Instancio;
import org.instancio.Select;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private  TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskMapper taskMapper;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    //private Task taskTest;
/*    private User user;
    private TaskStatus taskS;*/

    private User generateUser(String email, String password) {
        User user = Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .ignore(Select.field(User::getFirstName))
                .ignore(Select.field(User::getLastName))
                .supply(Select.field(User::getEmail), () -> email)
                .supply(Select.field(User::getPassword), () -> password)
                .ignore(Select.field(User::getCreatedAt))
                .ignore(Select.field(User::getUpdatedAt))
                .ignore(Select.field(User::getTasks))
                .create();
        userRepository.save(user);
        return user;
    }
    private TaskStatus generateTaskStatus(String name, String slug) {
        TaskStatus task = Instancio.of(TaskStatus.class)
                .ignore(Select.field(TaskStatus::getId))
                .supply(Select.field(TaskStatus::getName), () -> name)
                .supply(Select.field(TaskStatus::getSlug), () -> slug)
                .ignore(Select.field(TaskStatus::getTasks))
                .ignore(Select.field(TaskStatus::getCreatedAt))
                .create();
        taskStatusRepository.save(task);
        return task;
    }
    private Task generateTask(String name, String desc, User user, TaskStatus tasks) {
        return Instancio.of(Task.class)
        .ignore(Select.field(Task::getId))
        .supply(Select.field(Task::getName), () -> name)
        .ignore(Select.field(Task::getIndex))
        .supply(Select.field(Task::getDescription), () -> desc)
        .supply(Select.field(Task::getAssignee), () -> user)
        .supply(Select.field(Task::getTaskStatus), () -> tasks)
        .ignore(Select.field(TaskStatus::getCreatedAt))
        .create();
    }

    @BeforeEach
    public void setUp() {
        token = jwt().jwt(builder -> builder.subject("hexlet@example.com"));
        if (taskRepository.findByName("test task").isEmpty()) {
            User user = generateUser("some@mail.com", "1234");
            TaskStatus taskS = generateTaskStatus("task status test", "task_status_test");
            Task taskTest = generateTask("test task", "test task description", user, taskS);
            taskRepository.save(taskTest);
        }
        //taskRepository.save(taskTest);

    }

    @Test
    void index() throws Exception {
        //var taskTestIndex = generateTask("test task", "test task description");
        /*taskRepository.deleteAll();
        taskRepository.save(taskTest);*/
        //Task taskTestIndex = generateTask("test status index", "test_status_index", user, taskS);
        //taskRepository.save(taskTestIndex);
        var count = taskRepository.count();
        var result = mockMvc.perform(get("/api/tasks").with(jwt()))
                .andExpect(status().isOk()).andReturn().getResponse();
        var body = result.getContentAsString();
        assertThatJson(body).isArray();
        List<TaskDTO> taskDTOs = om.readValue(body, new TypeReference<List<TaskDTO>>() { });
        assertThat(taskDTOs.size()).isEqualTo((count));
    }

    @Test
    void show() throws Exception {
        //Task taskTestShow = generateTask("test status show", "test_status_show", user, taskS);
        //taskRepository.save(taskTestShow);
        var savedFind = taskRepository.findById(1L).get();
        ///var id = taskTest.getId();
        var result = mockMvc.perform(get("/api/tasks/" + savedFind.getId()).with(jwt()))
                .andExpect(status().isOk()).andReturn().getResponse();
        var body = result.getContentAsString();
        assertThatJson(body).and(
                v -> v.node("title").isEqualTo(savedFind.getName()));
    }

    @Test
    void delete() {
    }

    @Test
    void create() throws Exception {
        User user = generateUser("somenew@mail.com", "12345");
        TaskStatus taskS = generateTaskStatus("task status test create", "task_status_test_create");
        Task taskTest = generateTask("test task create", "test task description create", user, taskS);
        //taskRepository.save(taskTest);
        var taskDTOCreate = new TaskCreateDTO();
        taskDTOCreate.setAssigneeId(Math.toIntExact(user.getId()));
        taskDTOCreate.setStatus(taskS.getSlug());
        taskDTOCreate.setTitle("test task create");
        taskDTOCreate.setContent(("test task description create"));
        taskDTOCreate.setIndex(1);

        var request = MockMvcRequestBuilders.post("/api/tasks")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(taskDTOCreate));
        mockMvc.perform(request)
                .andExpect(status().isCreated());
        var userTest = taskRepository.findByName("test task create").get();

        assertNotNull(userTest);
    }

    @Test
    void update() {
    }
}
