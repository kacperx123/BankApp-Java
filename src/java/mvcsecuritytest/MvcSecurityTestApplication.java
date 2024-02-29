package mvcsecuritytest;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MvcSecurityTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(MvcSecurityTestApplication.class, args);

    }
}