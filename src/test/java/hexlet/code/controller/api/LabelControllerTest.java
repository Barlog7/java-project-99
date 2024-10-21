package hexlet.code.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.LabelCreateDTO;
import hexlet.code.dto.LabelDTO;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
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
//import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LabelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    LabelRepository labelRepository;

    @Autowired
    LabelMapper labelMapper;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    @BeforeEach
    void setUp() {
        token = jwt().jwt(builder -> builder.subject("hexlet@example.com"));
    }

    @Test
    void index() throws Exception {
        var count = labelRepository.count();
        var result = mockMvc.perform(get("/api/labels").with(jwt()))
                .andExpect(status().isOk()).andReturn().getResponse();
        var body = result.getContentAsString();
        assertThatJson(body).isArray();
        List<LabelDTO> labelDTOs = om.readValue(body, new TypeReference<List<LabelDTO>>() { });
        assertThat(labelDTOs.size()).isEqualTo((count));
    }

    @Test
    void show() throws Exception {
        var savedFind = labelRepository.findByName("feature").get();
        ///var id = taskTest.getId();
        var result = mockMvc.perform(get("/api/labels/" + savedFind.getId()).with(jwt()))
                .andExpect(status().isOk()).andReturn().getResponse();
        var body = result.getContentAsString();
        assertThatJson(body).and(
                v -> v.node("name").isEqualTo(savedFind.getName()));
    }

    @Test
    void delete() throws Exception {
        Label labelNew = new Label();
        labelNew.setName("label_delete");
        labelRepository.save(labelNew);
        var labelFind = labelRepository.findByName("label_delete").get();
        var request = mockMvc.perform(MockMvcRequestBuilders.delete("/api/labels/{id}", labelFind.getId()).with(jwt()))
                .andExpect(status()
                        .isNoContent());
        /*var userFind  = userRepository.findByEmail("someDelete@mail.com").get();
        var tasksFindFromUser = userFind.getTasks();
        var taskStatusFind = taskStatusRepository.findBySlug("task_status_test_delete").get();
        var tasksFindFromTaskStatus = taskStatusFind.getTasks();*/
    }

    @Test
    void create() throws Exception {
        LabelCreateDTO dto = new LabelCreateDTO();
        dto.setName("create_label");
        var request = MockMvcRequestBuilders.post("/api/labels")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));
        mockMvc.perform(request)
                .andExpect(status().isCreated());
        var labelTest = labelRepository.findByName("create_label").get();

        assertNotNull(labelTest);
    }

    @Test
    void update() throws Exception {
        //LabelUpdateDTO dto = new LabelUpdateDTO();
        //dto.setName("bug_update");
        var data = new HashMap<>();
        data.put("name", "bug_update");
        var labelFind = labelRepository.findByName("bug").get();
        var request = MockMvcRequestBuilders.put("/api/labels/" + labelFind.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk());
        var labelTest = labelRepository.findById(labelFind.getId()).get();
        assertThat(labelTest.getName()).isEqualTo(("bug_update"));
    }
}