package com.bibliserver.dao;

import com.bibliserver.model.User;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.UUID;

public class UserDAOTest {
    private UserDAO userDAO;

    @BeforeEach
    public void setUp() throws Exception {
        CleanDatabaseUtil.clean();
        userDAO = new UserDAO();
        // Préparer la base de test ou utiliser une base en mémoire (H2) si possible
    }

    @Test
    public void testCreateAndFindUser() throws Exception {
        User user = new User();
        String uniqueUsername = "testuser_" + UUID.randomUUID().toString().substring(0,8);
        user.setUsername(uniqueUsername);
        user.setPasswordHash("hashedpassword");
        user.setGroupId(1);

        userDAO.createUser(user.getUsername(), user.getPasswordHash(), user.getGroupId());
        User found = userDAO.findByUsername(uniqueUsername);

        assertNotNull(found);
        assertEquals(uniqueUsername, found.getUsername());
    }

    @Test
    public void testCreateUserWithDuplicateUsernameThrowsException() throws Exception {
        String uniqueUsername = "testuser_" + UUID.randomUUID().toString().substring(0,8);
        userDAO.createUser(uniqueUsername, "password1", 1);
        assertThrows(DuplicateUserException.class, () -> {
            userDAO.createUser(uniqueUsername, "password2", 1);
        });
    }

    @Test
    public void testValidateUser() throws Exception {
        // Créer un utilisateur, puis valider avec le bon et le mauvais mot de passe
        // À compléter selon la logique de hashage
    }
} 