package com.bibliserver.model;

import java.time.LocalDateTime;

public class Media {
    private int id;
    private String type;
    private String title;
    private String description;
    private Integer bookId; // null si indépendant
    private boolean isIndependent;
    private LocalDateTime createdAt;
    private String bookTitle;

    public Media() {}

    public Media(String type, String title, String description, Integer bookId, boolean isIndependent) {
        this.type = type;
        this.title = title;
        this.description = description;
        this.bookId = bookId;
        this.isIndependent = isIndependent;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getBookId() { return bookId; }
    public void setBookId(Integer bookId) { this.bookId = bookId; }
    public boolean isIndependent() { return isIndependent; }
    public void setIndependent(boolean independent) { isIndependent = independent; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
} 