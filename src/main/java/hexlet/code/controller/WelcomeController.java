package hexlet.code.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
//import io.sentry.Sentry;

@RestController
public class WelcomeController {
    @GetMapping("/welcome")
    String welcome() {
        /*try {
            throw new Exception("This is a test.");
        } catch (Exception e) {
            Sentry.captureException(e);
        }*/

        return "Welcome to Spring";
    }
}
