package com.bibliserver.dao;

import com.bibliserver.model.Group;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.UUID;

public class GroupDAOTest {
    private GroupDAO groupDAO;

    @BeforeEach
    public void setUp() throws Exception {
        CleanDatabaseUtil.clean();
        groupDAO = new GroupDAO();
        // Préparer la base de test ou utiliser une base en mémoire (H2) si possible
    }

    @Test
    public void testCreateAndFindGroup() throws Exception {
        Group group = new Group();
        String uniqueGroupName = "Test Group " + UUID.randomUUID().toString().substring(0,8);
        group.setName(uniqueGroupName);
        group.setDescription("Description de test");

        groupDAO.create(group);
        Group found = groupDAO.findById(group.getId());

        assertNotNull(found);
        assertEquals(uniqueGroupName, found.getName());
    }
} 