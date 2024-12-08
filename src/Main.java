public class Main {
    public static void main(String[] args) {
        // Initialize the database
        DatabaseConnection.initializeDatabase();

        // Launch the login screen
        // WelcomeScreen, LoginScreen, AdminScreen, UserScreen
        new WelcomeScreen().setVisible(true);
    }
}
