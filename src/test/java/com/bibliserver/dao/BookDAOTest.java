package com.bibliserver.dao;

import com.bibliserver.model.Book;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.UUID;

public class BookDAOTest {
    private BookDAO bookDAO;

    @BeforeEach
    public void setUp() throws Exception {
        CleanDatabaseUtil.clean();
        bookDAO = new BookDAO();
        // Préparer la base de test ou utiliser une base en mémoire (H2) si possible
    }

    @Test
    public void testCreateAndFindBook() throws Exception {
        Book book = new Book();
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setIsbn(UUID.randomUUID().toString().substring(0,13)); // ISBN unique
        book.setPublicationYear(2023);
        book.setPublisher("Test Publisher");
        book.setQuantity(5);
        book.setAvailableQuantity(5);

        bookDAO.create(book);
        Book found = bookDAO.findById(book.getId());

        assertNotNull(found);
        assertEquals("Test Book", found.getTitle());
    }
} 