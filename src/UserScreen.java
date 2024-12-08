import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class UserScreen extends JFrame {
    private JButton viewSlotsButton;
    private JButton entryVehicleButton;
    private JButton exitVehicleButton;
    private JButton viewHistoryButton;
    private JButton logoutButton;

    public UserScreen() {
        setTitle("User Dashboard");
        setSize(1080, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        JLabel titleLabel = new JLabel("Welcome To The Parking Lot");
        titleLabel.setBounds(350, 20, 800, 30);
        titleLabel.setFont(titleLabel.getFont().deriveFont(24.0f));
        add(titleLabel);

        JLabel welcomeLabel = new JLabel("Welcome, User!");
        welcomeLabel.setBounds(450, 70, 200, 30);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(welcomeLabel);

        Font buttonFont = new Font("Arial", Font.BOLD, 12); // Define a common font for buttons

        viewSlotsButton = new JButton("View Available Slots");
        viewSlotsButton.setBounds(70, 120, 200, 40); // Start at y=120
        viewSlotsButton.setFont(buttonFont);
        add(viewSlotsButton);

        viewSlotsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewAvailableSlots();
            }
        });

        entryVehicleButton = new JButton("Entry Vehicle");
        entryVehicleButton.setBounds(70, 200, 200, 40); // Increment y by 80
        entryVehicleButton.setFont(buttonFont);
        add(entryVehicleButton);

        entryVehicleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                entryVehicle();
            }
        });

        exitVehicleButton = new JButton("Exit Vehicle");
        exitVehicleButton.setBounds(70, 280, 200, 40); // Increment y by 80
        exitVehicleButton.setFont(buttonFont);
        add(exitVehicleButton);

        exitVehicleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exitVehicle();
            }
        });


        logoutButton = new JButton("Log Out");
        logoutButton.setBounds(70, 360, 200, 40); // Increment y by 80
        logoutButton.setFont(buttonFont);
        add(logoutButton);

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });
    }

    private void viewAvailableSlots() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, type FROM slots WHERE status = 'available'")) {

            // Define column names
            String[] columnNames = {"Slot ID", "Type"};
            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

            // Populate rows from ResultSet
            while (rs.next()) {
                int slotId = rs.getInt("id");
                String type = rs.getString("type");
                tableModel.addRow(new Object[]{slotId, type});
            }

            // If no data found
            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No available slots found.", "Information", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Create a JTable and set the model
            JTable table = new JTable(tableModel);
            table.setEnabled(false); // Optional: Make table non-editable

            table.setRowHeight(30);  // Set row height to add space between rows
            table.setFont(new Font("Arial", Font.PLAIN, 12));  // Set font size for table content

            // Set font size for table header
            table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

            // Show in a JScrollPane
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBounds(300, 120, 700, 450); // Adjust bounds as needed

            // Add the scrollPane to the container
            this.getContentPane().add(scrollPane);
            this.getContentPane().revalidate();
            this.getContentPane().repaint();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching data from database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void entryVehicle() {
        String vehicleType = JOptionPane.showInputDialog(this, "Enter vehicle type (car/bike):");
        if (vehicleType != null && !vehicleType.trim().isEmpty()) {
            String licensePlate = JOptionPane.showInputDialog(this, "Enter vehicle license plate:");
            if (licensePlate != null && !licensePlate.trim().isEmpty()) {
                try {
                    // Prompt for Slot ID
                    String slotIdInput = JOptionPane.showInputDialog(this, "Enter slot ID to assign:");
                    if (slotIdInput != null && !slotIdInput.trim().isEmpty()) {
                        int slotId = Integer.parseInt(slotIdInput);

                        // Check if the slot is available
                        try (Connection conn = DatabaseConnection.getConnection()) {
                            String checkSlotQuery = "SELECT status FROM slots WHERE id = ?";
                            try (PreparedStatement pstmt = conn.prepareStatement(checkSlotQuery)) {
                                pstmt.setInt(1, slotId);
                                try (ResultSet rs = pstmt.executeQuery()) {
                                    if (rs.next() && rs.getString("status").equals("available")) {

                                        // Insert vehicle entry with $20 charge
                                        String insertVehicleQuery = """
                                    INSERT INTO vehicles (license_plate, type, entry_time, slot_id)
                                    VALUES (?, ?, NOW(), ?)
                                    """;
                                        try (PreparedStatement pstmt2 = conn.prepareStatement(insertVehicleQuery)) {
                                            pstmt2.setString(1, licensePlate);
                                            pstmt2.setString(2, vehicleType);
                                            pstmt2.setInt(3, slotId);
                                            pstmt2.executeUpdate();
                                        }

                                        // Update slot status to occupied
                                        String updateSlotQuery = "UPDATE slots SET status = 'occupied' WHERE id = ?";
                                        try (PreparedStatement pstmt3 = conn.prepareStatement(updateSlotQuery)) {
                                            pstmt3.setInt(1, slotId);
                                            pstmt3.executeUpdate();
                                        }

                                        // Immediately create a transaction with $20 entry fee
                                        String insertTransactionQuery = """
                                    INSERT INTO transactions (vehicle_id, slot_id, amount, payment_time)
                                    VALUES (LAST_INSERT_ID(), ?, 20, NOW())
                                    """;
                                        try (PreparedStatement pstmt4 = conn.prepareStatement(insertTransactionQuery)) {
                                            pstmt4.setInt(1, slotId);
                                            pstmt4.executeUpdate();
                                        }

                                        JOptionPane.showMessageDialog(this, "Vehicle entered successfully! $20 charged on entry.");
                                    } else {
                                        JOptionPane.showMessageDialog(this, "Slot not available.");
                                    }
                                }
                            }
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid slot ID. Please enter a valid number.");
                }
            }
        }
    }

    private void exitVehicle() {
        // Prompt the user for the vehicle ID
        String vehicleIdInput = JOptionPane.showInputDialog(this, "Enter vehicle ID:");
        if (vehicleIdInput != null && !vehicleIdInput.trim().isEmpty()) {
            try {
                int vehicleId = Integer.parseInt(vehicleIdInput);

                try (Connection conn = DatabaseConnection.getConnection()) {
                    // Get the vehicle details including entry time and slot id
                    String getVehicleDetailsQuery = """
                    SELECT entry_time, slot_id
                    FROM vehicles
                    WHERE id = ? AND exit_time IS NULL
                    """;

                    try (PreparedStatement pstmt = conn.prepareStatement(getVehicleDetailsQuery)) {
                        pstmt.setInt(1, vehicleId);
                        try (ResultSet rs = pstmt.executeQuery()) {
                            if (rs.next()) {
                                Timestamp entryTime = rs.getTimestamp("entry_time");
                                int slotId = rs.getInt("slot_id");

                                // Default entry fee and hourly fee calculation
                                double entryFee = 20; // Default entry fee is $20
                                double hourlyFee = 20.0 / 3600.0; // $20 per hour, converted to per second

                                // Calculate the time duration in seconds
                                long durationInMillis = System.currentTimeMillis() - entryTime.getTime();
                                long durationInSeconds = durationInMillis / 1000; // Duration in seconds

                                // Calculate the additional fee based on time parked
                                double additionalFee = durationInSeconds * hourlyFee;
                                double totalFee = entryFee + additionalFee; // Total fee is entry fee + additional fee

                                // Ensure a minimum fee of $20
                                if (totalFee < 20) totalFee = 20;

                                // Format the fee to 3 decimal places
                                String formattedFee = String.format("%.3f", totalFee);

                                // Ask for payment confirmation
                                int confirmPayment = JOptionPane.showConfirmDialog(this,
                                        "The parking fee is $" + formattedFee + ". Do you confirm payment?",
                                        "Confirm Payment", JOptionPane.YES_NO_OPTION);

                                if (confirmPayment == JOptionPane.YES_OPTION) {
                                    // Set the exit time for the vehicle
                                    String updateVehicleExitQuery = """
                                    UPDATE vehicles
                                    SET exit_time = NOW()
                                    WHERE id = ? AND exit_time IS NULL
                                    """;
                                    try (PreparedStatement pstmt2 = conn.prepareStatement(updateVehicleExitQuery)) {
                                        pstmt2.setInt(1, vehicleId);
                                        pstmt2.executeUpdate();
                                    }

                                    // Update slot status to available
                                    String updateSlotStatusQuery = """
                                    UPDATE slots
                                    SET status = 'available'
                                    WHERE id = ?
                                    """;
                                    try (PreparedStatement pstmt3 = conn.prepareStatement(updateSlotStatusQuery)) {
                                        pstmt3.setInt(1, slotId);
                                        pstmt3.executeUpdate();
                                    }

                                    // Insert the transaction record
                                    String insertTransactionQuery = """
                                    INSERT INTO transactions (vehicle_id, slot_id, amount, payment_time)
                                    VALUES (?, ?, ?, NOW())
                                    """;
                                    try (PreparedStatement pstmt4 = conn.prepareStatement(insertTransactionQuery)) {
                                        pstmt4.setInt(1, vehicleId);
                                        pstmt4.setInt(2, slotId);
                                        pstmt4.setDouble(3, totalFee);
                                        pstmt4.executeUpdate();
                                    }

                                    JOptionPane.showMessageDialog(this, "Vehicle exited successfully! Fee: $" + formattedFee);
                                } else {
                                    JOptionPane.showMessageDialog(this, "Payment not confirmed. Vehicle exit cancelled.");
                                }
                            } else {
                                JOptionPane.showMessageDialog(this, "Vehicle not found or already exited.");
                            }
                        }
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid vehicle ID. Please enter a valid number.");
            }
        }
    }

    private void logout() {
        new WelcomeScreen().setVisible(true);
        dispose();
    }

}
