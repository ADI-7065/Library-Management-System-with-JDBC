/*	Description: Develop a Library Management System using Java and JDBC for database connectivity. 
			   The system should allow adding, borrowing, and returning books.	*/

package library; //our package name..!!

import java.sql.*; //import sql files
import java.util.Scanner; //scanner files

public class LibraryManagementSystem { // Class
	// Database connection details
	private static final String url = "jdbc:mysql://localhost:3306/librarysystem"; // url of your database
	private static final String user = "root"; // your DB UserName
	private static final String pass = "Aditya.kr"; // your DB password
	static {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("MySQL Driver not found. Add the connector JAR to classpath.");
		}
	} // to check the driver

	// Create table if not exists
	private static void createTable() { // this function create a table if it doesn't exist...!!!

		String query = "CREATE TABLE IF NOT EXISTS Books (" + "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "title VARCHAR(100), " + "author VARCHAR(100), " + "available BOOLEAN DEFAULT TRUE)";

		try (Connection c = DriverManager.getConnection(url, user, pass); Statement st = c.createStatement()) {
			st.execute(query);
		} catch (SQLException e) {
			System.out.println("Error creating table: " + e.getMessage());
		}
	}

	// Add Book
	private static void addBook(Scanner sc) {
		System.out.print("Enter Title: ");
		String title = sc.nextLine();
		System.out.print("Enter Author: ");
		String author = sc.nextLine();

		String query = "INSERT INTO Books (title, author, available) VALUES (?, ?, 1)";

		try (Connection c = DriverManager.getConnection(url, user, pass);
				PreparedStatement ps = c.prepareStatement(query)) { // PreparedStatement is used for parameterize
																	// queries
			ps.setString(1, title);
			ps.setString(2, author);
			ps.executeUpdate();
			System.out.println("Book added successfully.");
		} catch (SQLException e) {
			System.out.println("Error adding book: " + e.getMessage());
		}
	}

	// View All Books
	private static void viewBooks() {

		String query = "SELECT * FROM Books";

		try (Connection c = DriverManager.getConnection(url, user, pass);
				Statement st = c.createStatement();
				ResultSet rs = st.executeQuery(query)) {
			System.out.printf("| %-5s | %-25s | %-25s | %-12s%n", "ID", "Title", "Author", "Status");
			System.out.println("----------------------------------------------------------------------------");
			while (rs.next()) {
				int id = rs.getInt("id");
				String title = rs.getString("title");
				String author = rs.getString("author");
				boolean available = rs.getBoolean("available");
				String status = available ? "Available" : "Borrowed";
				System.out.printf("| %-5s | %-25s | %-25s | %-12s%n", id, title, author, status);
			}
		} catch (SQLException e) {
			System.out.println("Error viewing books: " + e.getMessage());
		}
	}

	// Borrow Book
	private static void borrowBook(Scanner sc) {
		System.out.print("Enter Book ID to borrow: ");
		int bookId = sc.nextInt();

		String query = "UPDATE Books SET available = 0 WHERE id = ? AND available = 1";

		try (Connection c = DriverManager.getConnection(url, user, pass);
				PreparedStatement ps = c.prepareStatement(query)) {
			ps.setInt(1, bookId);
			int updated = ps.executeUpdate();
			System.out.println(updated > 0 ? "Book borrowed successfully." : "Book not available or invalid ID.");
		} catch (SQLException e) {
			System.out.println("Error borrowing book: " + e.getMessage());
		}
	}

	// Return Book
	private static void returnBook(Scanner sc) {
		System.out.print("Enter Book ID to return: ");
		int bookId = sc.nextInt();

		String query = "UPDATE Books SET available = 1 WHERE id = ?";

		try (Connection c = DriverManager.getConnection(url, user, pass);
				PreparedStatement ps = c.prepareStatement(query)) {
			ps.setInt(1, bookId);
			int updated = ps.executeUpdate();
			System.out.println(updated > 0 ? "Book returned successfully." : "Invalid Book ID.");
		} catch (SQLException e) {
			System.out.println("Error returning book: " + e.getMessage());
		}
	}

	// Remove Book
	private static void removeBook(Scanner sc) {
		System.out.print("Enter Book ID to remove: ");
		int bookId = sc.nextInt();

		String query = "DELETE FROM books WHERE id = ?";

		try (Connection c = DriverManager.getConnection(url, user, pass);
				PreparedStatement ps = c.prepareStatement(query)) {

			ps.setInt(1, bookId);

			int rowsDeleted = ps.executeUpdate();
			if (rowsDeleted > 0) {
				System.out.println("Book removed successfully!");
			} else {
				System.out.println("No book found with the given ID.");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Main Function
	public static void main(String[] args) {
		Scanner sn = new Scanner(System.in);
		createTable();

		int choice;
		do {
			System.out.println("\n===== Library Management System =====");
			System.out.println("1. Add Book");
			System.out.println("2. View Books");
			System.out.println("3. Borrow Book");
			System.out.println("4. Return Book");
			System.out.println("5. Remove Book");
			System.out.println("7. Exit");
			System.out.print("Enter choice: ");

			choice = sn.nextInt();
			sn.nextLine();

			switch (choice) {
			case 1 -> addBook(sn);
			case 2 -> viewBooks();
			case 3 -> borrowBook(sn);
			case 4 -> returnBook(sn);
			case 5 -> removeBook(sn);
			case 6 -> System.out.println("Exiting... Goodbye!");
			default -> System.out.println("Invalid choice. Try again.");
			}
		} while (choice != 6);

		sn.close();
	}

}
