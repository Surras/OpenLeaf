package de.ironicdev.spring.openleaf;

import com.google.gson.Gson;
import de.ironicdev.spring.openleaf.models.Attribute;
import de.ironicdev.spring.openleaf.models.Comment;
import de.ironicdev.spring.openleaf.models.Entry;
import de.ironicdev.spring.openleaf.models.Location;
import de.ironicdev.spring.openleaf.repositories.EntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootApplication
public class OpenleafApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .sources(OpenleafApplication.class)
                .run(args);
    }
}
