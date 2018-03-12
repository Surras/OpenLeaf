package de.ironicdev.spring.openleaf.controller;

import com.google.gson.Gson;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import de.ironicdev.spring.openleaf.models.*;
import de.ironicdev.spring.openleaf.repositories.EntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.util.*;

@RestController
public class EntryController {

    @Autowired
    private EntryRepository repository;


    private boolean firstInit = true;

    /**
     * Main Call function to query the database.
     *
     * @param response        HTTP response
     * @param attributeFilter optional filter for attributes
     * @param propertyFilter  optional filter to ignore properties in response (lower response size)
     * @param orderBy         optional order by column
     * @param orderByDesc     optional order by column descending (if desc is filled, orderBy is ignored)
     * @param page            Page-Count for pagination
     * @param pageSize        optional size per page (Page-Limit 500, default 30)
     * @return
     */
    @RequestMapping(value = "/entries", method = RequestMethod.GET, produces = "application/json")
    public String getEnries(HttpServletResponse response,
                            @RequestParam Map<String, String> attributeFilter,
                            @RequestParam(value = "ignore", required = false) List<String> propertyFilter,
                            @RequestParam(value = "orderBy", required = false) List<String> orderBy,
                            @RequestParam(value = "orderByDesc", required = false) List<String> orderByDesc,
                            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                            @RequestParam(value = "pagesize", required = false, defaultValue = "30") int pageSize) throws Exception {
        try {
            if (firstInit) {
                firstInit = false;
                initTestData(100);
            }
            List<Entry> entryList = new ArrayList<>();

            /* remove attributeFilter that already RequestParams */
            attributeFilter.remove("ignore");
            attributeFilter.remove("page");
            attributeFilter.remove("pagesize");
            attributeFilter.remove("orderBy");
            attributeFilter.remove("orderByDesc");

            // if id is present, only get single entry and add to response list
            if (attributeFilter.containsKey("id")) {
                // go to database and fetch single entry
                Optional<Entry> dbEntry = repository.findById(attributeFilter.get("id"));
                if (dbEntry.isPresent()) // if found, filter ignored proerties
                    entryList.add(filterProperties(dbEntry.get(), propertyFilter));
                else
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);

                // send list back to client
                return new Gson().toJson(entryList);
            } else {
                // check order filter
                boolean orderDesc = false;
                List<String> orderList;
                if (orderByDesc != null && orderByDesc.size() > 0) {
                    orderDesc = true;
                    orderList = orderByDesc;
                } else
                    orderList = orderBy;


                // query data on database with given filter
                List<Entry> foundEntries = repository
                        .findByFilter(attributeFilter, page, pageSize, orderList, orderDesc);
                if (foundEntries != null) {
                    // use filter on returned data
                    for (Entry DB_ENTRY : foundEntries)
                        entryList.add(filterProperties(DB_ENTRY, propertyFilter));
                }

                // if nothing found at all, set status to not found 404
                if (entryList.size() == 0) response.setStatus(HttpServletResponse.SC_NOT_FOUND);

                // return all found entries
                return new Gson().toJson(entryList);
            }
        } catch (Exception ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return new Gson().toJson(new ErrorResponse(500,
                    "something critical went wrong. Error reported to support."));
        }
    }

    @RequestMapping(value = "/entries/initTestData/{quantity}", method = RequestMethod.GET, produces = "application/json")
    public String initTestData(@PathVariable("quantity") int quantity) {
        Entry e = null;

        for (int i = 1; i <= quantity; i++) {
            e = new Entry();
            //e.setEntryId(String.valueOf(i));
            e.setName("HelloData" + String.valueOf(i));
            e.setCreatedAt(new Date());
            e.setDescription("my description contains " + String.valueOf(i) + " characters");
            List<Attribute> attributeList = new ArrayList<>();
            for (int a = 1; a <= 10; a++)
                attributeList.add(new Attribute("key" + String.valueOf(a + i * 2), "val" + String.valueOf(a + i * 2)));
            e.setAttributes(attributeList);

            List<Location> locationList = new ArrayList<>();
            for (int l = 1; l <= 20; l++) {
                Location loc = new Location();
                loc.setFoundAt(new Date());
                loc.setLatitude(l + i * 3);
                loc.setLongitude(l + i * 3);
                loc.setNote("Location-Note " + String.valueOf(l));
                locationList.add(loc);
            }

            e.setLocations(locationList);

            List<Comment> commentList = new ArrayList<>();
            for (int c = 1; c <= 5; c++) {
                commentList.add(new Comment(String.valueOf(c + i * 4), "here comes comment no. " + String.valueOf(c + i * 4)));
            }

            e.setComments(commentList);

            repository.save(e);
        }

        return "";
        //return new Gson().toJson(repository.findAll());
    }

    /**
     * Save new Entry in DB.
     *
     * @param http
     * @param newEntry
     * @return
     */
    @RequestMapping(value = "/entries", method = RequestMethod.POST, produces = "application/json")
    public Entry saveEntry(HttpServletResponse http, @RequestBody Entry newEntry) {
        repository.save(newEntry);

        http.setStatus(HttpServletResponse.SC_OK);

        return newEntry;
    }

    /**
     * Update Entry in DB.
     *
     * @param http
     * @param changedEntry
     * @return
     */
    @RequestMapping(value = "/entries", method = RequestMethod.PUT, produces = "application/json")
    public Entry updateEntry(HttpServletResponse http,
                             @RequestBody Entry changedEntry) throws Exception {
        if (changedEntry.getEntryId() == null || changedEntry.getEntryId().equals(""))
            throw new Exception("please specify entry id to update entry");

        repository.save(changedEntry);

        http.setStatus(HttpServletResponse.SC_OK);
        return changedEntry;
    }

    /**
     * delete Entry in DB
     *
     * @param http
     * @return
     */
    @RequestMapping(value = "/entries/{entryId}", method = RequestMethod.DELETE, produces = "application/json")
    public Entry deleteEntry(HttpServletResponse http,
                             @PathVariable("entryId") String entryId) throws Exception {

        if (entryId == null || entryId.equals(""))
            throw new Exception("please specify entry id to update entry");

        Optional<Entry> e = repository.findById(entryId);
        if (e.isPresent()) {
            repository.delete(e.get());
            http.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } else {
            http.setStatus(HttpServletResponse.SC_NOT_FOUND);
            throw new Exception("Entry not found");
        }

        return e.get();
    }

    /**
     * Classic reflection helper method for setting dynamically the properties.
     */
    public static boolean set(Object object, String fieldName, Object fieldValue) {
        Class<?> clazz = object.getClass();
        if (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(object, fieldValue);
                return true;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
        return false;
    }

    private Entry filterProperties(Entry entry, List<String> propertyFilter) {
        if (propertyFilter == null) return entry; // nothing to do here...

        for (String PROP : propertyFilter) {
            // do some property reflection to set fields to "null"
            set(entry, PROP, null);
        }

        return entry;
    }
}