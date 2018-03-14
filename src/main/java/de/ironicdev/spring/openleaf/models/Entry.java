package de.ironicdev.spring.openleaf.models;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(collection = "Entries")
public class Entry {


    @Id
    private String entryId;
    private String name;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date createdAt;
    private EntryCategory category;
    private String description;
    private List<Comment> comments;
    private List<Location> locations;
    private List<Rating> ratings;
    private List<Attribute> attributes;
    private List<EntryImage> images;

    // getter setter

    public Entry() {
        comments = new ArrayList<>();
        locations = new ArrayList<>();
        ratings = new ArrayList<>();
        images = new ArrayList<>();
        attributes = new ArrayList<>();
    }

    public Entry(String id, String name) {
        this();
        this.entryId = id;
        this.name = name;
    }

    public String getEntryId() {
        return entryId;
    }

    public void setEntryId(String entryId) {
        this.entryId = entryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public EntryCategory getCategory() {
        return category;
    }

    public void setCategory(EntryCategory category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    public List<EntryImage> getImages() {
        return images;
    }

    public void setImages(List<EntryImage> images) {
        this.images = images;
    }
}
