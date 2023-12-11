/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package library;

/**
 *
 * @author swarn
 */
import java.sql.*;
import java.util.Scanner;

public class LibraryManagement{

    private Connection connection;
    private final Scanner scanner;

     public LibraryManagement() {
        try {
            // Initialize the database connection
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "swarnim");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        scanner = new Scanner(System.in);
    }

    // Method to add a new book to the database
    // Method to add a new book to the database
public void addNewBook() {
    System.out.println("Enter Title of the Book:");
    String title = scanner.nextLine();

    System.out.println("Enter Author of the Book:");
    String author = scanner.nextLine();

    System.out.println("Enter Publisher of the Book:");
    String publisher = scanner.nextLine();

    System.out.println("Enter Date of Issue (YYYY-MM-DD):");
    String dateOfIssueStr = scanner.nextLine();
    Date dateOfIssue = Date.valueOf(dateOfIssueStr);

    System.out.println("Enter Issue Status (Issued/InShelf):");
    String issueStatus = scanner.nextLine();

    try {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO Book (Title, Author, Publisher, DateOfIssue, IssueStatus) VALUES (?, ?, ?, ?, ?)");
        statement.setString(1, title);
        statement.setString(2, author);
        statement.setString(3, publisher);
        statement.setDate(4, dateOfIssue);
        statement.setString(5, issueStatus);
        
        int rowsAffected = statement.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println("Book added successfully!");
        } else {
            System.out.println("Failed to add the book.");
        }
    } catch (SQLException e) {
        e.printStackTrace();
        System.out.println("Error occurred while adding the book.");
    }
}

    

    // Method to update book information
    // Method to update book information
public void updateBookInfo() {
    System.out.println("Enter Book ID to update:");
    int bookID = scanner.nextInt();
    scanner.nextLine(); // Clear the newline from the buffer

    System.out.println("Enter new Title:");
    String newTitle = scanner.nextLine();

    System.out.println("Enter new Author:");
    String newAuthor = scanner.nextLine();

    System.out.println("Enter new Publisher:");
    String newPublisher = scanner.nextLine();

    System.out.println("Enter new Date of Issue (YYYY-MM-DD):");
    String newDateOfIssueStr = scanner.nextLine();
    Date newDateOfIssue = Date.valueOf(newDateOfIssueStr);

    System.out.println("Enter new Issue Status (Issued/InShelf):");
    String newIssueStatus = scanner.nextLine();

    try {
        PreparedStatement statement = connection.prepareStatement("UPDATE Book SET Title=?, Author=?, Publisher=?, DateOfIssue=?, IssueStatus=? WHERE BookID=?");
        statement.setString(1, newTitle);
        statement.setString(2, newAuthor);
        statement.setString(3, newPublisher);
        statement.setDate(4, newDateOfIssue);
        statement.setString(5, newIssueStatus);
        statement.setInt(6, bookID);

        int rowsAffected = statement.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println("Book information updated successfully!");
        } else {
            System.out.println("Failed to update book information. BookID might not exist.");
        }
    } catch (SQLException e) {
        e.printStackTrace();
        System.out.println("Error occurred while updating book information.");
    }
}


    // Method to check book availability
    // Method to check book availability
public void checkBookAvailability() {
    System.out.println("Enter Book ID to check availability:");
    int bookID = scanner.nextInt();
    scanner.nextLine(); // Clear the newline from the buffer

    try {
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM Book WHERE BookID=?");
        statement.setInt(1, bookID);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            String status = resultSet.getString("IssueStatus");
            System.out.println("Book is currently: " + status);
        } else {
            System.out.println("Book not found!");
        }
    } catch (SQLException e) {
        e.printStackTrace();
        System.out.println("Error occurred while checking book availability.");
    }
}


    // Method to delete a book
    // Method to delete a book
public void deleteBook() {
    System.out.println("Enter Book ID to delete:");
    int bookID = scanner.nextInt();
    scanner.nextLine(); // Clear the newline from the buffer

    try {
        PreparedStatement statement = connection.prepareStatement("DELETE FROM Book WHERE BookID=?");
        statement.setInt(1, bookID);

        int rowsAffected = statement.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println("Book deleted successfully!");
        } else {
            System.out.println("Failed to delete book. BookID might not exist.");
        }
    } catch (SQLException e) {
        e.printStackTrace();
        System.out.println("Error occurred while deleting book.");
    }
}


    // Method to display all books
    // Method to display all books
public void displayAllBooks() {
    try {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM Book");

        while (resultSet.next()) {
            int bookID = resultSet.getInt("BookID");
            String title = resultSet.getString("Title");
            String author = resultSet.getString("Author");
            String publisher = resultSet.getString("Publisher");
            Date dateOfIssue = resultSet.getDate("DateOfIssue");
            String issueStatus = resultSet.getString("IssueStatus");

            System.out.println("BookID: " + bookID + ", Title: " + title + ", Author: " + author + ", Publisher: " + publisher + ", DateOfIssue: " + dateOfIssue + ", IssueStatus: " + issueStatus);
        }
    } catch (SQLException e) {
        e.printStackTrace();
        System.out.println("Error occurred while displaying books.");
    }
}


    // Method to handle borrowing a book
    // Method to handle borrowing a book
public void borrowBook() {
    System.out.println("Enter Member ID:");
    int memberID = scanner.nextInt();
    scanner.nextLine(); // Clear the newline from the buffer

    System.out.println("Enter Book ID to borrow:");
    int bookID = scanner.nextInt();
    scanner.nextLine(); // Clear the newline from the buffer

    try {
        // Check if the book is available
        PreparedStatement checkAvailability = connection.prepareStatement("SELECT IssueStatus FROM Book WHERE BookID = ?");
        checkAvailability.setInt(1, bookID);
        ResultSet availabilityResult = checkAvailability.executeQuery();

        if (availabilityResult.next()) {
            String issueStatus = availabilityResult.getString("IssueStatus");
            if (issueStatus.equals("Issued")) {
                System.out.println("Sorry, the book is already issued.");
            } else {
                // Update book status to 'Issued' and assign it to the member
                PreparedStatement borrowStatement = connection.prepareStatement("UPDATE Book SET IssueStatus = 'Issued' WHERE BookID = ?");
                borrowStatement.setInt(1, bookID);
                int borrowUpdate = borrowStatement.executeUpdate();

                if (borrowUpdate > 0) {
                    PreparedStatement assignBook = connection.prepareStatement("UPDATE Member SET BookBorrowed = ? WHERE MemberID = ?");
                    assignBook.setInt(1, bookID);
                    assignBook.setInt(2, memberID);
                    int assignUpdate = assignBook.executeUpdate();

                    if (assignUpdate > 0) {
                        System.out.println("Book successfully borrowed.");
                    } else {
                        System.out.println("Failed to assign book to the member.");
                    }
                } else {
                    System.out.println("Failed to update book status.");
                }
            }
        } else {
            System.out.println("Book not found!");
        }
    } catch (SQLException e) {
        e.printStackTrace();
        System.out.println("Error occurred while borrowing the book.");
    }
}

public void addMember() {
    System.out.println("Enter Member ID:");
    int memberID = scanner.nextInt();
    scanner.nextLine(); // Clear the newline from the buffer

    try {
        // Check if the member already exists
        PreparedStatement checkMember = connection.prepareStatement("SELECT * FROM Member WHERE MemberID = ?");
        checkMember.setInt(1, memberID);
        ResultSet memberResult = checkMember.executeQuery();

        if (memberResult.next()) {
            System.out.println("Member already exists with this ID.");
            return;
        }

        // Member doesn't exist, proceed to add the member
        System.out.println("Enter Member Name:");
        String memberName = scanner.nextLine();

        PreparedStatement addMember = connection.prepareStatement("INSERT INTO Member (MemberID, MemberName) VALUES (?, ?)");
        addMember.setInt(1, memberID);
        addMember.setString(2, memberName);
        int rowsAffected = addMember.executeUpdate();

        if (rowsAffected > 0) {
            System.out.println("New member added successfully!");
        } else {
            System.out.println("Failed to add new member.");
        }
    } catch (SQLException e) {
        e.printStackTrace();
        System.out.println("Error occurred while adding the member.");
    }
}


public void deleteMember() {
    System.out.println("Enter Member ID to delete:");
    int memberID = scanner.nextInt();
    scanner.nextLine(); // Clear the newline from the buffer

    try {
        // Check if the member exists
        PreparedStatement checkMember = connection.prepareStatement("SELECT * FROM Member WHERE MemberID = ?");
        checkMember.setInt(1, memberID);
        ResultSet memberResult = checkMember.executeQuery();

        if (!memberResult.next()) {
            System.out.println("Member with this ID doesn't exist.");
            return;
        }

        // Member exists, proceed to delete the member
        PreparedStatement deleteMember = connection.prepareStatement("DELETE FROM Member WHERE MemberID = ?");
        deleteMember.setInt(1, memberID);
        int rowsAffected = deleteMember.executeUpdate();

        if (rowsAffected > 0) {
            System.out.println("Member deleted successfully!");
        } else {
            System.out.println("Failed to delete member.");
        }
    } catch (SQLException e) {
        e.printStackTrace();
        System.out.println("Error occurred while deleting the member.");
    }
}

public void updateMemberInfo() {
    System.out.println("Enter Member ID to update:");
    int memberID = scanner.nextInt();
    scanner.nextLine(); // Clear the newline from the buffer

    try {
        // Check if the member exists
        PreparedStatement checkMember = connection.prepareStatement("SELECT * FROM Member WHERE MemberID = ?");
        checkMember.setInt(1, memberID);
        ResultSet memberResult = checkMember.executeQuery();

        if (!memberResult.next()) {
            System.out.println("Member with this ID doesn't exist.");
            return;
        }

        System.out.println("Enter new Member Name:");
        String newMemberName = scanner.nextLine();

        // Update member information
        PreparedStatement updateMember = connection.prepareStatement("UPDATE Member SET MemberName = ? WHERE MemberID = ?");
        updateMember.setString(1, newMemberName);
        updateMember.setInt(2, memberID);

        int rowsAffected = updateMember.executeUpdate();

        if (rowsAffected > 0) {
            System.out.println("Member information updated successfully!");
        } else {
            System.out.println("Failed to update member information.");
        }
    } catch (SQLException e) {
        e.printStackTrace();
        System.out.println("Error occurred while updating member information.");
    }
}

    // Method to handle returning a book
    // Method to handle returning a book
public void returnBook() {
    System.out.println("Enter Member ID:");
    int memberID = scanner.nextInt();
    scanner.nextLine();

    try {
        // Get the book ID borrowed by the member
        PreparedStatement getBorrowedBook = connection.prepareStatement("SELECT BookBorrowed FROM Member WHERE MemberID = ?");
        getBorrowedBook.setInt(1, memberID);
        ResultSet borrowedBookResult = getBorrowedBook.executeQuery();

        if (borrowedBookResult.next()) {
            int borrowedBookID = borrowedBookResult.getInt("BookBorrowed");

            // Update book status and remove book from member
            if (borrowedBookID == 0) {
                // No book borrowed, set BookBorrowed to NULL
                PreparedStatement removeBook = connection.prepareStatement("UPDATE Member SET BookBorrowed = NULL WHERE MemberID = ?");
                removeBook.setInt(1, memberID);
                int removeUpdate = removeBook.executeUpdate();

                if (removeUpdate > 0) {
                    System.out.println("Book successfully returned.");
                } else {
                    System.out.println("Failed to remove book from the member.");
                }
            } else {
                // Book borrowed, update BookBorrowed and Book tables
                PreparedStatement updateBook = connection.prepareStatement("UPDATE Book SET IssueStatus = 'InShelf' WHERE BookID = ?");
                updateBook.setInt(1, borrowedBookID);
                updateBook.executeUpdate();

                PreparedStatement removeBook = connection.prepareStatement("UPDATE Member SET BookBorrowed = NULL WHERE MemberID = ?");
                removeBook.setInt(1, memberID);
                int removeUpdate = removeBook.executeUpdate();

                if (removeUpdate > 0) {
                    System.out.println("Book successfully returned.");
                } else {
                    System.out.println("Failed to remove book from the member.");
                }
            }
        } else {
            System.out.println("No book is currently borrowed by this member.");
        }
    } catch (SQLException e) {
        e.printStackTrace();
        System.out.println("Error occurred while returning the book.");
    }
}

    
    public void generateReports() {
    try {
        
        Statement statement = connection.createStatement();

        
        ResultSet resultSet = statement.executeQuery("SELECT Author, COUNT(*) AS BookCount FROM Book GROUP BY Author");

        // Display the report
        System.out.println("Report: Count of books by each author");
        while (resultSet.next()) {
            String author = resultSet.getString("Author");
            int bookCount = resultSet.getInt("BookCount");

            System.out.println("Author: " + author + ", Book Count: " + bookCount);
        }
    } catch (SQLException e) {
        e.printStackTrace();
        System.out.println("Error occurred while generating reports.");
    }
}
    // Method to close resources and connection
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
            if (scanner != null) {
                scanner.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        LibraryManagement bookManager = new LibraryManagement();

        // Menu based interface
        boolean exit = false;
        while (!exit) {
            System.out.println("1. Add New Book");
            System.out.println("2. Update Book Information");
            System.out.println("3. Check Book Availability");
            System.out.println("4. Delete Book");
            System.out.println("5. Display All Books");
            System.out.println("6. Borrow Book");
            System.out.println("7. Return Book");
            System.out.println("8. Generate Reports");
            System.out.println("9. Enter member information");
            System.out.println("10. Delete member information");
            System.out.println("11. Update member information");
            System.out.println("12. Exit");
            System.out.println("Enter your choice:");

            int choice = bookManager.scanner.nextInt();
            

            
            
            switch (choice) {
                case 1:
                    bookManager.addNewBook();
                    break;
                case 2:
                    bookManager.updateBookInfo();
                    break;
                case 3:
                    bookManager.checkBookAvailability();
                    break;
                case 4:
                    bookManager.deleteBook();
                    break;
                case 5:
                    bookManager.displayAllBooks();
                    break;
                case 6:
                    bookManager.borrowBook();
                    break;
                case 7:
                    bookManager.returnBook();
                    break;
                case 8:
                    bookManager.generateReports();
                    break;
                case 9:
                    bookManager.addMember();
                    break;
                case 10:
                    bookManager.deleteMember();
                    break;
                case 11:
                    bookManager.updateMemberInfo();
                    break;
                case 12:
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
                    break;
            }
        }

        bookManager.close(); // Close resources when done
    }
}
