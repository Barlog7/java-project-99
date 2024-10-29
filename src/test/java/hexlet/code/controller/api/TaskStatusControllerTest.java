package hexlet.code.controller.api;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatusDTO;

import hexlet.code.mapper.TaskStatusMaper;
import hexlet.code.model.TaskStatus;

import hexlet.code.repository.TaskStatusRepository;

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

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
class TaskStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskStatusMaper taskStatusMaper;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    private TaskStatusCreateDTO generateTaskStatusCreateDTO(String name, String slug) {
        return Instancio.of(TaskStatusCreateDTO.class)
                .supply(Select.field(TaskStatusCreateDTO::getName), () -> name)
                .supply(Select.field(TaskStatusCreateDTO::getSlug), () -> slug)
                .create();
    }

    private TaskStatus generateTaskStatus(String name, String slug) {
        return Instancio.of(TaskStatus.class)
                .ignore(Select.field(TaskStatus::getId))
                .supply(Select.field(TaskStatus::getName), () -> name)
                .supply(Select.field(TaskStatus::getSlug), () -> slug)
                .ignore(Select.field(TaskStatus::getCreatedAt))
                .ignore(Select.field(TaskStatus::getTasks))
                .create();
    }


    private TaskStatus taskTest;

    @BeforeEach
    public void setUp() {
        token = jwt().jwt(builder -> builder.subject("hexlet@example.com"));
        taskTest = generateTaskStatus("test status", "test_status");

    }

    @Test
    void index() throws Exception {
        var count = taskStatusRepository.count();
        var result = mockMvc.perform(get("/api/task_statuses").with(jwt()))
                .andExpect(status().isOk()).andReturn().getResponse();
        var body = result.getContentAsString();
        assertThatJson(body).isArray();
        List<TaskStatusDTO> taskDTOs = om.readValue(body, new TypeReference<List<TaskStatusDTO>>() { });
        assertThat(taskDTOs.size()).isEqualTo((count));
    }

    @Test
    void show() throws Exception {
        if (taskStatusRepository.findBySlug("test_status_show").isPresent()) {
            taskStatusRepository.delete(taskStatusRepository.findBySlug("test_status_show").get());
        }
        var taskTestShow = generateTaskStatus("test status show", "test_status_show");
        taskStatusRepository.save(taskTestShow);
        var savedFind = taskStatusRepository.findBySlug("test_status_show").get();

        //var savedFind = taskStatusRepository.findBySlug("draft").get();
        var id = savedFind.getId();
        var result = mockMvc.perform(get("/api/task_statuses/" + savedFind.getId()).with(jwt()))
                .andExpect(status().isOk()).andReturn().getResponse();
        var body = result.getContentAsString();
        assertThatJson(body).and(
                v -> v.node("name").isEqualTo(savedFind.getName()));

        if (taskStatusRepository.findBySlug("test_status_show").isPresent()) {
            taskStatusRepository.delete(taskStatusRepository.findBySlug("test_status_show").get());
        }

    }

    @Test
    void create() throws Exception {

        if (taskStatusRepository.findBySlug("test_status1").isPresent()) {
            taskStatusRepository.delete(taskStatusRepository.findBySlug("test_status1").get());
        }


        var taskTestStatusCreate = generateTaskStatusCreateDTO("test status1", "test_status1");
        var request = MockMvcRequestBuilders.post("/api/task_statuses")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(taskTestStatusCreate));
        mockMvc.perform(request)
                .andExpect(status().isCreated());
        var userTest = taskStatusRepository.findBySlug("test_status1").get();

        assertNotNull(userTest);
        taskStatusRepository.delete(userTest);
    }

    @Test
    void update() throws Exception {
        var data = new HashMap<>();
        data.put("name", "Draft test");


        if (taskStatusRepository.findBySlug("test_status_update").isPresent()) {
            taskStatusRepository.delete(taskStatusRepository.findBySlug("test_status_update").get());
        }
        var taskTestShow = generateTaskStatus("test status update", "test_status_update");
        taskStatusRepository.save(taskTestShow);
        var savedFind = taskStatusRepository.findBySlug("test_status_update").get();

        var request = MockMvcRequestBuilders.put("/api/task_statuses/" + savedFind.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var userTest = taskStatusRepository.findById(savedFind.getId()).get();
        assertThat(userTest.getName()).isEqualTo(("Draft test"));
        assertThat(userTest.getSlug()).isEqualTo(("test_status_update"));

        if (taskStatusRepository.findBySlug("test_status_update").isPresent()) {
            taskStatusRepository.delete(taskStatusRepository.findBySlug("test_status_update").get());
        }
    }

    @Test
    void delete() throws Exception {
        var taskTestDel = generateTaskStatus("test status delete", "test_status_delete");
        taskStatusRepository.save(taskTestDel);
        var taskFind = taskStatusRepository.findBySlug("test_status_delete").get();
        var request = mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/task_statuses/{id}", taskFind.getId()).with(jwt()))
                .andExpect(status()
                        .isNoContent());

    }




}
