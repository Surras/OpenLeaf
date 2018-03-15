package de.ironicdev.spring.openleaf.models;

import org.springframework.data.annotation.Id;

import java.util.List;

public class UserRole {
    @Id
    private String roleId;
    private String name;
    private List<Privileg> privilegs;

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Privileg> getPrivilegs() {
        return privilegs;
    }

    public void setPrivilegs(List<Privileg> privilegs) {
        this.privilegs = privilegs;
    }
}
