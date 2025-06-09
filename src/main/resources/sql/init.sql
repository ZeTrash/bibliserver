-- Création de la base de données
CREATE DATABASE IF NOT EXISTS bibliserver;
USE bibliserver;

-- Table des utilisateurs
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table des livres
CREATE TABLE IF NOT EXISTS books (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(100) NOT NULL,
    isbn VARCHAR(13) UNIQUE,
    publication_year INT,
    publisher VARCHAR(100),
    quantity INT DEFAULT 1,
    available_quantity INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table des emprunts
CREATE TABLE IF NOT EXISTS loans (
    id INT PRIMARY KEY AUTO_INCREMENT,
    book_id INT,
    user_id INT,
    loan_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    FOREIGN KEY (book_id) REFERENCES books(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Insertion d'un administrateur par défaut (mot de passe: admin123)
INSERT INTO users (username, password_hash, role) 
VALUES ('admin', '$2a$10$8K1p/a0dL1LXMIgoEDFrwOQDyUD7ZWQkQwP4SlqWnX.QUKTjzlUie', 'ADMIN'); 