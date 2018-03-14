package de.ironicdev.spring.openleaf.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import de.ironicdev.spring.openleaf.exceptions.EntryNotFoundException;
import de.ironicdev.spring.openleaf.models.*;
import de.ironicdev.spring.openleaf.repositories.EntryRepository;
import de.ironicdev.spring.openleaf.services.StorageService;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;

@RestController
@Controller
public class EntryController {

    @Autowired
    private EntryRepository repository;

    private final StorageService storageService;
    private static final ImageObserver DUMMY_OBSERVER = (img, infoflags, x, y, width, height) -> true;

    @Autowired
    public EntryController(StorageService service) {
        this.storageService = service;
    }

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
                if (entryList.size() == 0) throw new EntryNotFoundException();

                // return all found entries
                return new Gson().toJson(entryList);
            }
        } catch (Exception ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return new Gson().toJson(new ErrorResponse(500,
                    "something critical went wrong. Error reported to support."));
        }
    }

    @PostMapping("/entries/{entryId}/images")
    public String entryImageUpload(@RequestParam("image") MultipartFile image,
                                   @PathVariable("entryId") String entryId,
                                   RedirectAttributes redirectAttributes) throws EntryNotFoundException {

        // check if entry is available
        Optional<Entry> dbEntry = repository.findById(entryId);

        if (!dbEntry.isPresent()) throw new EntryNotFoundException();

        String imgId = new ObjectId().toHexString();

        // store in filesystem
        storageService.storeJPEG(image, imgId);

        // if successfully stored, create entryImage with new ID and assing to given entry
        EntryImage entryImage = new EntryImage();
        entryImage.setImageId(imgId);
        entryImage.setCreatedAt(new Date());

        dbEntry.get().getImages().add(entryImage);
        repository.save(dbEntry.get());

        return new Gson().toJson(entryImage);
    }

    @RequestMapping(value = "/entries/images/{imageId}", method = RequestMethod.GET)
    public void getEntryImage(HttpServletResponse response, @PathVariable("imageId") String imageId) throws IOException {
        imageId += ".jpg";
        Path path = storageService.load(imageId);
        InputStream in = Files.newInputStream(path);
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        IOUtils.copy(in, response.getOutputStream());
    }

    @RequestMapping(value = "/entries/images/{imageId}/thumbnail", method = RequestMethod.GET)
    public void getEntryImageThumbnail(HttpServletResponse response,
                                       @PathVariable("imageId") String imageId,
                                       @RequestParam(value = "maxSide", required = false, defaultValue = "150") int maxSide)
            throws IOException {

        imageId += ".jpg";
        Path path = storageService.load(imageId);
        InputStream in = Files.newInputStream(path);

        BufferedImage imgIn = ImageIO.read(path.toFile());

        double scale;
        if (imgIn.getWidth() >= imgIn.getHeight()) {
            // horizontal or square image
            scale = Math.min(maxSide, imgIn.getWidth()) / (double) imgIn.getWidth();
        } else {
            // vertical image
            scale = Math.min(maxSide, imgIn.getHeight()) / (double) imgIn.getHeight();
        }

        BufferedImage thumbnailOut = new BufferedImage((int) (scale * imgIn.getWidth()),
                (int) (scale * imgIn.getHeight()),
                imgIn.getType());
        Graphics2D g = thumbnailOut.createGraphics();

        AffineTransform transform = AffineTransform.getScaleInstance(scale, scale);
        g.drawImage(imgIn, transform, DUMMY_OBSERVER);
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        ImageIO.write(thumbnailOut, "jpg", response.getOutputStream());
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
            throw new EntryNotFoundException();
        }

        return e.get();
    }

    /**
     * Classic reflection helper method for setting dynamically the properties.
     */
    private static boolean set(Object object, String fieldName, Object fieldValue) {
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

    @ExceptionHandler(EntryNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(EntryNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }
}
