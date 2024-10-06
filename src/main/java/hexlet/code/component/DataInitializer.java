package hexlet.code.component;

import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.PasswordHashing;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        var email = "hexlet@example.com";
        var userData = new User();
        userData.setEmail(email);
        var password = "qwerty";
        var passHash = PasswordHashing.getHashPass(password);
        userData.setPassword(passHash);
        userRepository.save(userData);

        var slag = "draft";
        var name = "Draft";
        var taskData = new TaskStatus();
        taskData.setName(name);
        taskData.setSlug(slag);
        taskStatusRepository.save(taskData);

        slag = "to_review";
        name = "To review";
        taskData = new TaskStatus();
        taskData.setName(name);
        taskData.setSlug(slag);
        taskStatusRepository.save(taskData);

        slag = "to_be_fixed";
        name = "To be fixed";
        taskData = new TaskStatus();
        taskData.setName(name);
        taskData.setSlug(slag);
        taskStatusRepository.save(taskData);

        slag = "to_publish";
        name = "To publish";
        taskData = new TaskStatus();
        taskData.setName(name);
        taskData.setSlug(slag);
        taskStatusRepository.save(taskData);

        slag = "published";
        name = "Published";
        taskData = new TaskStatus();
        taskData.setName(name);
        taskData.setSlug(slag);
        taskStatusRepository.save(taskData);
    }
}
