package uz.pdp.backend.olxapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableScheduling
@EnableAsync
@SpringBootApplication
@EnableTransactionManagement
public class OlxAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(OlxAppApplication.class, args);
    }

}
