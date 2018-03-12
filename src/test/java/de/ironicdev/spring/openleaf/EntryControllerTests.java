package de.ironicdev.spring.openleaf;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.jna.platform.win32.Guid;
import de.ironicdev.spring.openleaf.controller.EntryController;
import de.ironicdev.spring.openleaf.models.Entry;
import de.ironicdev.spring.openleaf.repositories.EntryRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.Assert;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {
        OpenleafApplication.class
})
@WebMvcTest(EntryController.class)
public class EntryControllerTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private EntryRepository repository;

    @Before
    public void setUp() {
        Entry entry1 = new Entry();
        entry1.setName("TEST1");

        Entry entry2 = new Entry();
        entry2.setName("TEST2");
        List<Entry> entries = Arrays.asList(entry1, entry2);

        when(repository.findAll()).thenReturn(entries);
    }

    @Test
    public void getAllEntries() throws Exception {

        mvc.perform(get("/entries")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].name").value("TEST1"))
                .andExpect(jsonPath("$.[1].name").value("TEST2"));
    }

    // entry tests
    @Test
    public void getEntry_ByName() throws Exception {
        String testName = "TEST1";

        mvc.perform(get("/entries?name=" + testName)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect((jsonPath("name").value(testName)));
    }

    @Test
    public void getEntry_ById() throws Exception {

        Entry testEntry = new Entry();
        testEntry.setName("myName");
        String randomId = Guid.GUID.newGuid().toGuidString();
        testEntry.setEntryId(randomId);
        repository.save(testEntry);

        List<Entry> entries = Arrays.asList(testEntry);
        when(repository.findById(randomId)).thenReturn(Optional.of(testEntry));

        mvc.perform(get("/entries/" + randomId)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect((jsonPath("name").value("myName")));
                //.andExpect((jsonPath("entryId").value(randomId)));
    }

//    @Test
//    public void getEntry_withoutFilter() {
//        //Assert.isTrue(false, "blubb");
//    }
//
//    @Test
//    public void getEntry_withSinglePropertyFilter() {
//        //Assert.isTrue(false, "template");
//    }
//
//    @Test
//    public void getEntry_WithMultiPropertyFilter() {
//        //Assert.isTrue(false, "template");
//    }
//
//    @Test
//    public void getEntry_WithPagination() {
//        //Assert.isTrue(false, "template");
//    }
//
//    @Test
//    public void getEntry_WithAttributeQuery() {
//        //Assert.isTrue(false, "template");
//    }
//
//    @Test
//    public void getEntry_WithSinglePropertyFilter_and_Pagination() {
//        //Assert.isTrue(false, "template");
//    }
//
//    @Test
//    public void getEntry_WithMultiPropertyFilter_and_Pagination() {
//        //Assert.isTrue(false, "template");
//    }
//
//    @Test
//    public void getEntry_WithSinglePropertyFilter_and_attributeQuery() {
//        //Assert.isTrue(false, "template");
//    }
//
//    @Test
//    public void getEntry_WithMultiPropertyFilter_and_attributeQuery() {
//        //Assert.isTrue(false, "template");
//    }
//
//    @Test
//    public void getEntry_WithSinglePropertyFilter_and_attributeQuery_and_pagination() {
//        //Assert.isTrue(false, "template");
//    }
//
//    @Test
//    public void getEntry_WithMultiPropertyFilter_and_attributeQuery_and_pagination() {
//        //Assert.isTrue(false, "template");
//    }
}
