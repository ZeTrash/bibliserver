package com.bibliserver.util;

import com.bibliserver.model.User;
import com.bibliserver.model.Group;
import com.bibliserver.model.Permission;
import com.bibliserver.dao.GroupDAO;
import java.sql.SQLException;

public class SecurityUtil {
    private static GroupDAO groupDAO;

    public static void initialize(GroupDAO dao) {
        groupDAO = dao;
    }

    public static boolean hasPermission(User user, String resource, String action) {
        try {
            if (user == null || user.getGroupId() == 0) {
                return false;
            }

            Group group = groupDAO.findById(user.getGroupId());
            if (group == null) {
                return false;
            }

            for (Permission permission : group.getPermissions()) {
                if (permission.getResource().equals(resource) && 
                    permission.getAction().equals(action)) {
                    return true;
                }
            }

            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void checkPermission(User user, String resource, String action) 
            throws SecurityException {
        if (!hasPermission(user, resource, action)) {
            throw new SecurityException("Accès refusé : " + resource + ":" + action);
        }
    }

    public static boolean isAdmin(User user) {
        try {
            if (user == null || user.getGroupId() == 0) {
                return false;
            }

            Group group = groupDAO.findById(user.getGroupId());
            return group != null && "Administrateurs".equals(group.getName());
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isLibrarian(User user) {
        try {
            if (user == null || user.getGroupId() == 0) {
                return false;
            }

            Group group = groupDAO.findById(user.getGroupId());
            return group != null && "Bibliothécaires".equals(group.getName());
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isReader(User user) {
        try {
            if (user == null || user.getGroupId() == 0) {
                return false;
            }

            Group group = groupDAO.findById(user.getGroupId());
            return group != null && "Lecteurs".equals(group.getName());
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
} 