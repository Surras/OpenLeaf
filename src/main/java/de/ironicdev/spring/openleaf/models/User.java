package de.ironicdev.spring.openleaf.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class User {
    @Id
    private String userId;
    private String username;
    @JsonIgnore
    private String password;
    private String mailAddress;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date registerDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date lastLogin;
    private UserRole memberRole;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }

    public String getMailAddress() {
        return mailAddress;
    }

    public void setMailAddress(String mailAddress) {
        this.mailAddress = mailAddress;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public UserRole getMemberRole() {
        return memberRole;
    }

    public void setMemberRole(UserRole memberRole) {
        this.memberRole = memberRole;
    }
}
