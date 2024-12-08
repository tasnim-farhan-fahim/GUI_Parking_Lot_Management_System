import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WelcomeScreen extends JFrame {
    private JButton adminButton;
    private JButton userButton;
    public static String selectedRole; // This will hold the selected role

    public WelcomeScreen() {
        setTitle("Welcome To The Parking Lot");
        setSize(1080, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        // Add title label
        JLabel titleLabel = new JLabel("Welcome To The Parking Lot");
        titleLabel.setBounds(350, 100, 800, 30);
        titleLabel.setFont(titleLabel.getFont().deriveFont(24.0f));
        add(titleLabel);

        // Create buttons for selecting the role (admin or user)
        adminButton = new JButton("Login as Admin");
        adminButton.setBounds(420, 250, 200, 40);
        add(adminButton);

        userButton = new JButton("Login as User");
        userButton.setBounds(420, 350, 200, 40);
        add(userButton);

        // Button actions to set the role and open LoginScreen
        adminButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedRole = "admin"; // Set role to admin
                new LoginScreen().setVisible(true); // Open LoginScreen
                dispose(); // Close the WelcomeScreen
            }
        });

        userButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedRole = "user"; // Set role to user
                new LoginScreen().setVisible(true); // Open LoginScreen
                dispose(); // Close the WelcomeScreen
            }
        });
    }
}
