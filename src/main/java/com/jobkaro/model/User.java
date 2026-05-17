package com.jobkaro.model;

import java.time.LocalDateTime;

public class User {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String password;
    private String role;
    private String city;
    private String avatarPath;
    private boolean active = true;
    private double rating;
    private int totalRatings;
    private int completedJobs;
    private LocalDateTime createdAt;

    public User() {}
    public User(String name, String email, String phone, String password, String role, String city) {
        this.name = name; this.email = email; this.phone = phone;
        this.password = password; this.role = role; this.city = city;
    }

    public boolean isWorker()   { return "worker".equals(role); }
    public boolean isProvider() { return "provider".equals(role); }
    public boolean isAdmin()    { return "admin".equals(role); }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getAvatarPath() { return avatarPath; }
    public void setAvatarPath(String avatarPath) { this.avatarPath = avatarPath; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    public int getTotalRatings() { return totalRatings; }
    public void setTotalRatings(int totalRatings) { this.totalRatings = totalRatings; }
    public int getCompletedJobs() { return completedJobs; }
    public void setCompletedJobs(int completedJobs) { this.completedJobs = completedJobs; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
