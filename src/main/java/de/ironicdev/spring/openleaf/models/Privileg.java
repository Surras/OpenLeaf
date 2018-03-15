package de.ironicdev.spring.openleaf.models;

import org.springframework.data.annotation.Id;

public class Privileg {
    @Id
    private String privilegId;
    private String name;

    public String getPrivilegId() {
        return privilegId;
    }

    public void setPrivilegId(String privilegId) {
        this.privilegId = privilegId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
