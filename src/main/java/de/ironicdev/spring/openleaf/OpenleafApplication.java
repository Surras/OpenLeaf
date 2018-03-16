package de.ironicdev.spring.openleaf;

import com.google.gson.Gson;
import de.ironicdev.spring.openleaf.models.Attribute;
import de.ironicdev.spring.openleaf.models.Comment;
import de.ironicdev.spring.openleaf.models.Entry;
import de.ironicdev.spring.openleaf.models.Location;
import de.ironicdev.spring.openleaf.repositories.EntryRepository;
import de.ironicdev.spring.openleaf.services.StorageProperties;
import de.ironicdev.spring.openleaf.services.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class OpenleafApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .sources(OpenleafApplication.class)
                .run(args);
    }

    @Bean
    CommandLineRunner init(StorageService storageService) {
        return (args) -> {
            storageService.init();
        };
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
