package org.hhautoresponder;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(properties = {
        "spring.config.import=optional:classpath:application-secret.yml"
})
class HhAutoResponderApplicationTests {

    @Test
    void contextLoads() {
    }

}
