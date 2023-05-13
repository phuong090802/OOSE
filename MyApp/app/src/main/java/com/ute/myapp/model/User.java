package com.ute.myapp.model;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class User implements Serializable {
    private String userId;
    private String firstName;
    private String lastName;
    private String userName;
    private String password;
    private String phone;
    private String dateOfBirth;
    private String email;
    private String imageUrl;
    private String roleName;
    private Boolean status;
    private String lockedTime;

    public User() {
    }

    public User(String firstName, String lastName, String userName, String password, String phone, String dateOfBirth, String email, String imageUrl, String roleName, Boolean status, String lockedTime) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.password = password;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.imageUrl = imageUrl;
        this.roleName = roleName;
        this.status = status;
        this.lockedTime = lockedTime;
    }

    public User(String userId, String firstName, String lastName, String userName, String password, String phone, String dateOfBirth, String email, String imageUrl, String roleName, Boolean status) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.password = password;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.imageUrl = imageUrl;
        this.roleName = roleName;
        this.status = status;
    }

    public User(String userId, String firstName, String lastName, String userName, String password, String phone, String dateOfBirth, String email, String imageUrl, String roleName, Boolean status, String lockedTime) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.password = password;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.imageUrl = imageUrl;
        this.roleName = roleName;
        this.status = status;
        this.lockedTime = lockedTime;
    }

    public String getLockedTime() {
        return lockedTime;
    }

    public void setLockedTime(String lockedTime) {
        this.lockedTime = lockedTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Boolean isStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", phone='" + phone + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", email='" + email + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", roleName='" + roleName + '\'' +
                ", status=" + status +
                ", lockedTime='" + lockedTime + '\'' +
                '}';
    }
}
