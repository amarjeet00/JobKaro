package com.jobkaro.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Job {
    private int id;
    private int providerId;
    private String providerName;
    private double providerRating;
    private String title;
    private String description;
    private String category;
    private double payment;
    private String paymentType;
    private int workersNeeded;
    private String address;
    private String city;
    private LocalDate jobDate;
    private String duration;
    private boolean urgent;
    private String status;
    private int acceptedWorkerId;
    private int views;
    private LocalDateTime createdAt;

    public static final String STATUS_OPEN      = "open";
    public static final String STATUS_APPLIED   = "applied";
    public static final String STATUS_ACCEPTED  = "accepted";
    public static final String STATUS_ONGOING   = "ongoing";
    public static final String STATUS_COMPLETED = "completed";
    public static final String STATUS_CANCELLED = "cancelled";

    public String getPaymentDisplay() {
        if ("daily".equals(paymentType))  return "₹" + (int)payment + "/day";
        if ("hourly".equals(paymentType)) return "₹" + (int)payment + "/hr";
        return "₹" + (int)payment;
    }

    public String getCategoryIcon() {
        return switch (category) {
            case "delivery"      -> "📦";
            case "shop_helper"   -> "🛒";
            case "construction"  -> "🏗️";
            case "packing"       -> "📫";
            case "cleaning"      -> "🧹";
            case "event_helper"  -> "🎉";
            case "electrician"   -> "⚡";
            case "plumber"       -> "🔧";
            case "house_shifting"-> "🏠";
            case "office_helper" -> "🏢";
            default              -> "💼";
        };
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getProviderId() { return providerId; }
    public void setProviderId(int providerId) { this.providerId = providerId; }
    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }
    public double getProviderRating() { return providerRating; }
    public void setProviderRating(double providerRating) { this.providerRating = providerRating; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public double getPayment() { return payment; }
    public void setPayment(double payment) { this.payment = payment; }
    public String getPaymentType() { return paymentType; }
    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }
    public int getWorkersNeeded() { return workersNeeded; }
    public void setWorkersNeeded(int workersNeeded) { this.workersNeeded = workersNeeded; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public LocalDate getJobDate() { return jobDate; }
    public void setJobDate(LocalDate jobDate) { this.jobDate = jobDate; }
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
    public boolean isUrgent() { return urgent; }
    public void setUrgent(boolean urgent) { this.urgent = urgent; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getAcceptedWorkerId() { return acceptedWorkerId; }
    public void setAcceptedWorkerId(int acceptedWorkerId) { this.acceptedWorkerId = acceptedWorkerId; }
    public int getViews() { return views; }
    public void setViews(int views) { this.views = views; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
