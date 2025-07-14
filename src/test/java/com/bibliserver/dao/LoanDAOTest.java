package com.bibliserver.dao;

import com.bibliserver.model.Loan;
import com.bibliserver.model.Book;
import com.bibliserver.model.User;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.util.UUID;

public class LoanDAOTest {
    private LoanDAO loanDAO;
    private BookDAO bookDAO;
    private UserDAO userDAO;

    @BeforeEach
    public void setUp() throws Exception {
        CleanDatabaseUtil.clean();
        loanDAO = new LoanDAO();
        bookDAO = new BookDAO();
        userDAO = new UserDAO();
        // Préparer la base de test ou utiliser une base en mémoire (H2) si possible
    }

    @Test
    public void testCreateLoan() throws Exception {
        // Créer un livre et un utilisateur fictifs
        Book book = new Book();
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setIsbn(UUID.randomUUID().toString().substring(0,13)); // ISBN unique
        book.setPublicationYear(2023);
        book.setPublisher("Test Publisher");
        book.setQuantity(5);
        book.setAvailableQuantity(5);
        bookDAO.create(book);

        String uniqueUsername = "testuser_" + UUID.randomUUID().toString().substring(0,8);
        User user = new User();
        user.setUsername(uniqueUsername);
        user.setPasswordHash("hashedpassword");
        user.setGroupId(1);
        userDAO.createUser(user.getUsername(), user.getPasswordHash(), user.getGroupId());
        User userFromDb = userDAO.findByUsername(user.getUsername());

        Loan loan = new Loan(book, userFromDb, LocalDate.now(), LocalDate.now().plusDays(14));
        loanDAO.create(loan);

        Loan found = loanDAO.findById(loan.getId());
        assertNotNull(found);
        assertEquals(book.getId(), found.getBook().getId());
        assertEquals(userFromDb.getId(), found.getUser().getId());
    }
} 