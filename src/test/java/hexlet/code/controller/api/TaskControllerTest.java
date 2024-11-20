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
import org.openapitools.jackson.nullable.JsonNullable;
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
        .ignore(Select.field(Task::getCreatedAt))
                .ignore(Select.field(Task::getLabelsUsed))
        .create();
    }

    @BeforeEach
    public void setUp() {
        token = jwt().jwt(builder -> builder.subject("hexlet@example.com"));
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
    void indexWithSerchName() throws Exception {


        if (taskStatusRepository.findBySlug("task_status_test_search").isPresent()) {
            var testStatus  = taskStatusRepository.findBySlug("task_status_test_search").get();
            testStatus.removeTask(taskRepository.findByName("search").get());
            taskStatusRepository.save(testStatus);
            taskStatusRepository.delete(taskStatusRepository.findBySlug("task_status_test_search").get());
        }
        if (taskRepository.findByName("search").isPresent()) {
            taskRepository.delete(taskRepository.findByName("search").get());
        }
        if (userRepository.findByEmail("someSerch@mail.com").isPresent()) {
            userRepository.delete(userRepository.findByEmail("someSerch@mail.com").get());
        }


        User userSearch = generateUser("someSerch@mail.com", "1234");
        TaskStatus taskSSearch = generateTaskStatus("task status test search", "task_status_test_search");
        Task taskTestSearch = generateTask("search", "test task description search", userSearch, taskSSearch);

        taskRepository.save(taskTestSearch);
        if (taskRepository.findByName("test task").isEmpty()) {
            User user = generateUser("some@mail.com", "1234");
            TaskStatus taskS = generateTaskStatus("task status test", "task_status_test");
            Task taskTest = generateTask("test task", "test task description", user, taskS);
            taskRepository.save(taskTest);
        }

        var count = taskRepository.count();
        var result = mockMvc.perform(get("/api/tasks?titleCont=search").with(jwt()))
                .andExpect(status().isOk()).andReturn().getResponse();
        var body = result.getContentAsString();
        assertThatJson(body).isArray();
        List<TaskDTO> taskDTOs = om.readValue(body, new TypeReference<List<TaskDTO>>() { });
        assertThat(taskDTOs.size()).isEqualTo((1));

        if (taskStatusRepository.findBySlug("task_status_test_search").isPresent()) {
            var testStatus  = taskStatusRepository.findBySlug("task_status_test_search").get();
            testStatus.removeTask(taskRepository.findByName("search").get());
            taskStatusRepository.save(testStatus);
            taskStatusRepository.delete(taskStatusRepository.findBySlug("task_status_test_search").get());
        }
        if (taskRepository.findByName("search").isPresent()) {
            taskRepository.delete(taskRepository.findByName("search").get());
        }
        if (userRepository.findByEmail("someSerch@mail.com").isPresent()) {
            userRepository.delete(userRepository.findByEmail("someSerch@mail.com").get());
        }

    }


    @Test
    void show() throws Exception {
        if (taskStatusRepository.findBySlug("task_status_test_show").isPresent()) {
            var testStatus  = taskStatusRepository.findBySlug("task_status_test_show").get();
            testStatus.removeTask(taskRepository.findByName("test task show").get());
            taskStatusRepository.save(testStatus);
            taskStatusRepository.delete(taskStatusRepository.findBySlug("task_status_test_show").get());
        }
        if (taskRepository.findByName("test task show").isPresent()) {
            taskRepository.delete(taskRepository.findByName("test task show").get());
        }
        if (userRepository.findByEmail("someShow@mail.com").isPresent()) {
            userRepository.delete(userRepository.findByEmail("someShow@mail.com").get());
        }

        if (taskRepository.findByName("test task show").isEmpty()) {
            User user = generateUser("someShow@mail.com", "1234");
            TaskStatus taskS = generateTaskStatus("task status test show", "task_status_test_show");
            Task taskTest = generateTask("test task show", "test task description show", user, taskS);
            taskRepository.save(taskTest);
        }
        var savedFind = taskRepository.findByName("test task show").get();
        ///var id = taskTest.getId();
        var result = mockMvc.perform(get("/api/tasks/" + savedFind.getId()).with(jwt()))
                .andExpect(status().isOk()).andReturn().getResponse();
        var body = result.getContentAsString();
        assertThatJson(body).and(
                v -> v.node("title").isEqualTo(savedFind.getName()));
    }

    @Test
    void delete() throws Exception {

        if (taskStatusRepository.findBySlug("task_status_test_delete").isPresent()) {
            var testStatus  = taskStatusRepository.findBySlug("task_status_test_delete").get();
            if (taskRepository.findByName("test task delete").isPresent()
                && testStatus.getTasks().contains(taskRepository.findByName("test task delete").get())) {
                testStatus.removeTask(taskRepository.findByName("test task delete").get());
                taskStatusRepository.save(testStatus);
            }
            taskStatusRepository.delete(taskStatusRepository.findBySlug("task_status_test_delete").get());
        }
        if (taskRepository.findByName("test task delete").isPresent()) {
            taskRepository.delete(taskRepository.findByName("test task delete").get());
        }
        if (userRepository.findByEmail("someDelete@mail.com").isPresent()) {
            userRepository.delete(userRepository.findByEmail("someDelete@mail.com").get());
        }
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

        if (taskStatusRepository.findBySlug("task_status_test_delete").isPresent()) {
            var testStatus  = taskStatusRepository.findBySlug("task_status_test_delete").get();
            if (taskRepository.findByName("test task delete").isPresent()
                    && testStatus.getTasks().contains(taskRepository.findByName("test task delete").get())) {
                testStatus.removeTask(taskRepository.findByName("test task delete").get());
                taskStatusRepository.save(testStatus);
            }
            taskStatusRepository.delete(taskStatusRepository.findBySlug("task_status_test_delete").get());
        }
        if (taskRepository.findByName("test task delete").isPresent()) {
            taskRepository.delete(taskRepository.findByName("test task delete").get());
        }
        if (userRepository.findByEmail("someDelete@mail.com").isPresent()) {
            userRepository.delete(userRepository.findByEmail("someDelete@mail.com").get());
        }
    }

    @Test
    void create() throws Exception {
        if (taskStatusRepository.findBySlug("task_status_test_create").isPresent()) {
            var testStatus  = taskStatusRepository.findBySlug("task_status_test_create").get();
            if (taskRepository.findByName("test task create").isPresent()
                    && testStatus.getTasks().contains(taskRepository.findByName("test task create").get())) {
                testStatus.removeTask(taskRepository.findByName("test task create").get());
                taskStatusRepository.save(testStatus);
            }
            taskStatusRepository.delete(taskStatusRepository.findBySlug("task_status_test_create").get());
        }
        if (taskRepository.findByName("test task create").isPresent()) {
            taskRepository.delete(taskRepository.findByName("test task create").get());
        }
        if (userRepository.findByEmail("somenew@mail.com").isPresent()) {
            userRepository.delete(userRepository.findByEmail("somenew@mail.com").get());
        }

        User user = generateUser("somenew@mail.com", "12345");
        TaskStatus taskS = generateTaskStatus("task status test create", "task_status_test_create");
        Task taskTest = generateTask("test task create", "test task description create", user, taskS);
        //taskRepository.save(taskTest);
        var taskDTOCreate = new TaskCreateDTO();

        JsonNullable<Integer> assigneeNullable = JsonNullable.of(Math.toIntExact(user.getId()));
        taskDTOCreate.setAssigneeid(assigneeNullable);
        //taskDTOCreate.setAssigneeid(Math.toIntExact(user.getId()));
        //JsonNullable<Integer> assigneeNullable = JsonNullable.of(Math.toIntExact(user.getId()));
        JsonNullable<String> jsonSlug = JsonNullable.of(taskS.getSlug());
        taskDTOCreate.setStatus(jsonSlug);
        //taskDTOCreate.setStatus(taskS.getSlug());
        taskDTOCreate.setTitle("test task create");
        JsonNullable<String> jsonContent = JsonNullable.of("test task description create");
        taskDTOCreate.setContent(jsonContent);
        //taskDTOCreate.setContent(("test task description create"));
        JsonNullable<Integer> jsonIndex = JsonNullable.of(1);
        taskDTOCreate.setIndex(jsonIndex);
        //taskDTOCreate.setIndex(1);

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

        if (taskStatusRepository.findBySlug("task_status_test_create").isPresent()) {
            var testStatus  = taskStatusRepository.findBySlug("task_status_test_create").get();
            if (taskRepository.findByName("test task create").isPresent()
                    && testStatus.getTasks().contains(taskRepository.findByName("test task create").get())) {
                testStatus.removeTask(taskRepository.findByName("test task create").get());
                taskStatusRepository.save(testStatus);
            }
            taskStatusRepository.delete(taskStatusRepository.findBySlug("task_status_test_create").get());
        }
        if (taskRepository.findByName("test task create").isPresent()) {
            taskRepository.delete(taskRepository.findByName("test task create").get());
        }
        if (userRepository.findByEmail("somenew@mail.com").isPresent()) {
            userRepository.delete(userRepository.findByEmail("somenew@mail.com").get());
        }
    }

    @Test
    void update() throws Exception {
        if (taskStatusRepository.findBySlug("task_status_test_update").isPresent()) {
            var testStatus  = taskStatusRepository.findBySlug("task_status_test_update").get();
            if (taskRepository.findByName("test task update change").isPresent()
                    && testStatus.getTasks().contains(taskRepository.findByName("test task update change").get())) {
                testStatus.removeTask(taskRepository.findByName("test task update change").get());
                taskStatusRepository.save(testStatus);
            }
            if (taskRepository.findByName("test task update").isPresent()
                    && testStatus.getTasks().contains(taskRepository.findByName("test task update").get())) {
                testStatus.removeTask(taskRepository.findByName("test task update").get());
                taskStatusRepository.save(testStatus);
            }
            taskStatusRepository.delete(taskStatusRepository.findBySlug("task_status_test_update").get());
        }
        if (taskRepository.findByName("test task update change").isPresent()) {
            taskRepository.delete(taskRepository.findByName("test task update change").get());
        }
        if (taskRepository.findByName("test task update").isPresent()) {
            taskRepository.delete(taskRepository.findByName("test task update").get());
        }
        if (userRepository.findByEmail("someupdate@mail.com").isPresent()) {
            userRepository.delete(userRepository.findByEmail("someupdate@mail.com").get());
        }
        if (userRepository.findByEmail("someupdateNew@mail.com").isPresent()) {
            userRepository.delete(userRepository.findByEmail("someupdateNew@mail.com").get());
        }


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




        User userNew = generateUser("asomeupdateNew@mail.com", "12347");
        var dataUser = new HashMap<>();
        dataUser.put("assignee_id", Long.valueOf(userNew.getId()));
        var taskFindUpdateUser = taskRepository.findByName("test task update change").get();
        var request2 = MockMvcRequestBuilders.put("/api/tasks/" + taskFindUpdateUser.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dataUser));

        mockMvc.perform(request2)
                .andExpect(status().isOk());
        taskTest = taskRepository.findById(taskFindUpdateUser.getId()).get();

        var userFind  = userRepository.findByEmail("asomeupdateNew@mail.com").get();
        var userFindOld  = userRepository.findByEmail("someupdate@mail.com").get();
        System.out.println("Test");
        assertThat(taskTest.getAssignee().getEmail()).isEqualTo("asomeupdateNew@mail.com");
        var task = userFind.getTasks().get(0);
        assertThat(task).isEqualTo(taskTest);
        assertThat(userFindOld.getTasks().contains(taskTest)).isFalse();

        if (taskStatusRepository.findBySlug("task_status_test_update").isPresent()) {
            var testStatus  = taskStatusRepository.findBySlug("task_status_test_update").get();
            if (taskRepository.findByName("test task update change").isPresent()
                    && testStatus.getTasks().contains(taskRepository.findByName("test task update change").get())) {
                testStatus.removeTask(taskRepository.findByName("test task update change").get());
                taskStatusRepository.save(testStatus);
            }
            if (taskRepository.findByName("test task update").isPresent()
                    && testStatus.getTasks().contains(taskRepository.findByName("test task update").get())) {
                testStatus.removeTask(taskRepository.findByName("test task update").get());
                taskStatusRepository.save(testStatus);
            }
            taskStatusRepository.delete(taskStatusRepository.findBySlug("task_status_test_update").get());
        }
        if (taskRepository.findByName("test task update change").isPresent()) {
            taskRepository.delete(taskRepository.findByName("test task update change").get());
        }
        if (taskRepository.findByName("test task update").isPresent()) {
            taskRepository.delete(taskRepository.findByName("test task update").get());
        }
        if (userRepository.findByEmail("someupdate@mail.com").isPresent()) {
            userRepository.delete(userRepository.findByEmail("someupdate@mail.com").get());
        }
        if (userRepository.findByEmail("someupdateNew@mail.com").isPresent()) {
            userRepository.delete(userRepository.findByEmail("asomeupdateNew@mail.com").get());
        }

    }

}
