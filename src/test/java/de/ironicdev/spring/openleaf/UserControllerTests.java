package de.ironicdev.spring.openleaf;

import de.ironicdev.spring.openleaf.controller.UserController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.Assert;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = OpenleafApplication.class)
@WebMvcTest(UserController.class)
public class UserControllerTests {

    @Autowired
    private MockMvc mvc;

    @Before
    public void setUp() {
    }

    @Test
    public void getUser_byId() {
        Assert.isTrue(false, "Default false");
    }
}
