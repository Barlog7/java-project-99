package hexlet.code.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.dto.TaskDTO;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
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

import java.util.HashMap;
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

    @Autowired
    private LabelRepository labelRepository;

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
/*        if (taskRepository.findByName("test task").isEmpty()) {
            User user = generateUser("some@mail.com", "1234");
            TaskStatus taskS = generateTaskStatus("task status test", "task_status_test");
            Task taskTest = generateTask("test task", "test task description", user, taskS);
            taskRepository.save(taskTest);
        }*/
        //taskRepository.save(taskTest);

    }

    @Test
    void index() throws Exception {
        if (taskRepository.findByName("test task").isEmpty()) {
            User user = generateUser("some@mail.com", "1234");
            TaskStatus taskS = generateTaskStatus("task status test", "task_status_test");
            Task taskTest = generateTask("test task", "test task description", user, taskS);
            taskRepository.save(taskTest);
        }
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
        if (taskRepository.findByName("test task").isEmpty()) {
            User user = generateUser("someShow@mail.com", "1234");
            TaskStatus taskS = generateTaskStatus("task status test show", "task_status_test_show");
            Task taskTest = generateTask("test task show", "test task description show", user, taskS);
            taskRepository.save(taskTest);
        }
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
    void delete() throws Exception {
        User user = generateUser("someDelete@mail.com", "1234d");
        TaskStatus taskS = generateTaskStatus("task status test delete", "task_status_test_delete");
        Task taskTest = generateTask("test task delete", "test task description delete", user, taskS);
        taskRepository.save(taskTest);
        var taskFind = taskRepository.findByName("test task delete").get();
        var request = mockMvc.perform(MockMvcRequestBuilders.delete("/api/tasks/{id}", taskFind.getId()).with(jwt()))
                .andExpect(status()
                        .isNoContent());
        var userFind  = userRepository.findByEmail("someDelete@mail.com").get();
        var tasksFindFromUser = userFind.getTasks();
        var taskStatusFind = taskStatusRepository.findBySlug("task_status_test_delete").get();
        var tasksFindFromTaskStatus = taskStatusFind.getTasks();
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
        var userFind  = userRepository.findByEmail("somenew@mail.com").get();
        var tasksFindFromUser = userFind.getTasks();
        var taskStatusFind = taskStatusRepository.findBySlug("task_status_test_create").get();
        var tasksFindFromTaskStatus = taskStatusFind.getTasks();
        System.out.println("Test");
    }

    @Test
    void update() throws Exception {
        User user = generateUser("someupdate@mail.com", "1234");
        TaskStatus taskS = generateTaskStatus("task status test update", "task_status_test_update");
        Task taskTestSave = generateTask("test task update", "test task description update", user, taskS);
        taskRepository.save(taskTestSave);
        var data = new HashMap<>();
        data.put("title", "test task update change");
        var taskFind = taskRepository.findByName("test task update").get();
        var request = MockMvcRequestBuilders.put("/api/tasks/" + taskFind.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk());
        var taskTest = taskRepository.findById(taskFind.getId()).get();
        assertThat(taskTest.getName()).isEqualTo(("test task update change"));

        User userNew = generateUser("someupdateNew@mail.com", "12347");
        var dataUser = new HashMap<>();
        dataUser.put("assigneeId", Long.valueOf(userNew.getId()));
        //data.put("title", "test task update change");
        var taskFindUpdateUser = taskRepository.findByName("test task update change").get();
        var request2 = MockMvcRequestBuilders.put("/api/tasks/" + taskFind.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dataUser));

        mockMvc.perform(request2)
                .andExpect(status().isOk());
        taskTest = taskRepository.findById(taskFind.getId()).get();
        var userFind  = userRepository.findByEmail("someupdateNew@mail.com").get();

        var userFindOld  = userRepository.findByEmail("someupdate@mail.com").get();
        System.out.println("Test");
        assertThat(taskTest.getAssignee().getEmail()).isEqualTo("someupdateNew@mail.com");
        var task = userFind.getTasks().get(0);
        assertThat(task).isEqualTo(taskTest);
        //assertThat(tasks.contains(taskTest)).isTrue();
        assertThat(userFindOld.getTasks().contains(taskTest)).isFalse();

    }
}
