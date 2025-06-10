package com.bibliserver.model;

import java.time.LocalDateTime;

public class GroupPermission {
    private int id;
    private int groupId;
    private int permissionId;
    private LocalDateTime createdAt;

    public GroupPermission() {}

    public GroupPermission(int groupId, int permissionId) {
        this.groupId = groupId;
        this.permissionId = permissionId;
        this.createdAt = LocalDateTime.now();
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(int permissionId) {
        this.permissionId = permissionId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
} 