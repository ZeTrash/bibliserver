package com.bibliserver.model;

public class MemoireDetails {
    private int id;
    private int bookId;
    private String university;
    private String supervisor;
    private int year;
    private String subject;

    public MemoireDetails() {}

    public MemoireDetails(int bookId, String university, String supervisor, int year, String subject) {
        this.bookId = bookId;
        this.university = university;
        this.supervisor = supervisor;
        this.year = year;
        this.subject = subject;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    public String getUniversity() { return university; }
    public void setUniversity(String university) { this.university = university; }
    public String getSupervisor() { return supervisor; }
    public void setSupervisor(String supervisor) { this.supervisor = supervisor; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
} 