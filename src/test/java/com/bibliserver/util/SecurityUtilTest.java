package com.bibliserver.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mindrot.jbcrypt.BCrypt;

public class SecurityUtilTest {
    @Test
    public void testPasswordHashing() {
        String password = "secret";
        String hash = BCrypt.hashpw(password, BCrypt.gensalt());
        assertTrue(BCrypt.checkpw(password, hash));
        assertFalse(BCrypt.checkpw("wrong", hash));
    }

    @Test
    public void testPasswordIsHashedAndNotPlainText() {
        String password = "monSuperMotDePasse";
        String hash = SecurityUtil.hashPassword(password);
        assertNotEquals(password, hash);
        assertTrue(SecurityUtil.verifyPassword(password, hash));
    }
} 