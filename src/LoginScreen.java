import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginScreen extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginScreen() {
        setTitle("Login - Welcome To The Parking Lot");
        setSize(1080, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        // Add title label
        JLabel titleLabel = new JLabel("Welcome To The Parking Lot");
        titleLabel.setBounds(380, 60, 800, 30);
        titleLabel.setFont(titleLabel.getFont().deriveFont(24.0f));
        add(titleLabel);

        // Add welcome label with role information
        JLabel welcomeLabel = new JLabel("You are logging in as an " + WelcomeScreen.selectedRole.toUpperCase()); // Dynamically display role
        welcomeLabel.setBounds(400, 140, 800, 30);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(welcomeLabel);

        // Create login components
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(400, 200, 80, 25);
        add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setBounds(500, 200, 165, 25);
        add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(400, 250, 80, 25);
        add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(500, 250, 165, 25);
        add(passwordField);

        loginButton = new JButton("Login");
        loginButton.setBounds(500, 300, 80, 25);
        add(loginButton);

        // Button action to authenticate the user
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                authenticateUser();
            }
        });
    }

    private void authenticateUser() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String selectedRole = WelcomeScreen.selectedRole; // Get the selected role

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?")) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                if (selectedRole.equals(role)) {
                    if ("admin".equals(role)) {
                        new AdminScreen().setVisible(true);
                    } else {
                        new UserScreen().setVisible(true);
                    }
                    dispose(); // Close the LoginScreen
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid role for this login type");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
