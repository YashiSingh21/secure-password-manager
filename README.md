# Secure Password Manager

A Java console application for securely storing and managing website login credentials, backed by MySQL.

## Features
- User registration & login (master password hashed with SHA-256 + per-user salt — never stored in plaintext)
- Add, view, search, update, and delete saved credentials
- Site passwords encrypted with AES before being stored in the database
- Password strength check on save
- Layered architecture: `model` → `dao` → `service` → `ui`, with custom exception handling throughout

## Tech Stack
Java (JDK 17+) · MySQL 8 · JDBC · OOP

## Project Structure
```
SecurePasswordManager/
├── src/com/yashi/passwordmanager/
│   ├── model/        # User, Credential
│   ├── dao/           # UserDAO, CredentialDAO — all raw SQL lives here
│   ├── service/        # AuthService, CredentialService — business rules
│   ├── util/           # DBConnection, SecurityUtil (hashing/AES), AppException
│   ├── ui/              # ConsoleUI — menu-driven interface
│   └── Main.java
├── sql/schema.sql       # run this first to create the database & tables
└── README.md
```

## Setup

1. **Create the database**
   ```bash
   mysql -u root -p < sql/schema.sql
   ```

2. **Create an app user** (or edit `DBConnection.java` to use your own credentials)
   ```sql
   CREATE USER 'appuser'@'localhost' IDENTIFIED BY 'AppPass123!';
   GRANT ALL PRIVILEGES ON password_manager_db.* TO 'appuser'@'localhost';
   FLUSH PRIVILEGES;
   ```

3. **Download the MySQL Connector/J jar** and place it in a `lib/` folder:
   https://dev.mysql.com/downloads/connector/j/

4. **Compile**
   ```bash
   javac -cp "lib/mysql-connector-j-x.x.x.jar" -d bin $(find src -name "*.java")
   ```

5. **Run**
   ```bash
   java -cp "bin:lib/mysql-connector-j-x.x.x.jar" com.yashi.passwordmanager.Main
   ```
   (On Windows, use `;` instead of `:` in the classpath.)

## Security Notes
This is an academic/portfolio project. The AES key in `SecurityUtil.java` is hardcoded for simplicity —
in production it would come from a proper key-management service (KMS/environment variable), and the
master password hashing would use bcrypt/Argon2 instead of SHA-256.
