package de.ironicdev.spring.openleaf;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({
        EntryControllerTests.class,
        UserControllerTests.class
})
public class OpenleafApplicationTests {

    @Before
    public void setUp() {
    }

    @Test
    public void contextLoads() {

    }
}
