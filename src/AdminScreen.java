import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AdminScreen extends JFrame {
    private JButton viewSlotButton;
    private JButton viewVehicleButton;
    private JButton addSlotButton;
    private JButton viewOccupiedSlotsButton;
    private JButton viewHistoryButton;
    private JButton logoutButton;

    public AdminScreen() {
        setTitle("Admin Dashboard");
        setSize(1080, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        JLabel titleLabel = new JLabel("Welcome To The Parking Lot");
        titleLabel.setBounds(350, 20, 800, 30);
        titleLabel.setFont(titleLabel.getFont().deriveFont(24.0f));
        add(titleLabel);

        JLabel welcomeLabel = new JLabel("Welcome, Admin!");
        welcomeLabel.setBounds(450, 70, 200, 30);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(welcomeLabel);

        Font buttonFont = new Font("Arial", Font.BOLD, 12); // Define a common font for buttons

        viewSlotButton = new JButton("View All Slot");
        viewSlotButton.setBounds(70, 120, 200, 40);
        viewSlotButton.setFont(buttonFont); // Set font size
        add(viewSlotButton);

        viewSlotButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewSlotTable();
            }
        });

        viewVehicleButton = new JButton("View All Vehicle");
        viewVehicleButton.setBounds(70, 200, 200, 40); // Start at 120, then gap of 80
        viewVehicleButton.setFont(buttonFont);
        add(viewVehicleButton);

        viewVehicleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewVehicleTable();
            }
        });

        addSlotButton = new JButton("Add New Slot");
        addSlotButton.setBounds(70, 280, 200, 40);
        addSlotButton.setFont(buttonFont);
        add(addSlotButton);

        addSlotButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNewSlot();
            }
        });

        viewOccupiedSlotsButton = new JButton("View Occupied Slots");
        viewOccupiedSlotsButton.setBounds(70, 360, 200, 40);
        viewOccupiedSlotsButton.setFont(buttonFont);
        add(viewOccupiedSlotsButton);

        viewOccupiedSlotsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewOccupiedSlots();
            }
        });

        viewHistoryButton = new JButton("View Transaction History");
        viewHistoryButton.setBounds(70, 440, 200, 40);
        viewHistoryButton.setFont(buttonFont);
        add(viewHistoryButton);

        viewHistoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewTransactionHistory();
            }
        });

        logoutButton = new JButton("Log Out");
        logoutButton.setBounds(70, 520, 200, 40);
        logoutButton.setFont(buttonFont);
        add(logoutButton);

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });
    }

    private void viewSlotTable() {
        // Define the query to fetch all slot details
        String query = "SELECT id, type, status FROM slots";

        // Define column names for table header
        String[] columnNames = {"Slot ID", "Type", "Status"};

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            // Create a DefaultTableModel with column names
            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

            // Populate the table with data from the ResultSet
            while (rs.next()) {
                int slotId = rs.getInt("id");
                String type = rs.getString("type");
                String status = rs.getString("status");
                tableModel.addRow(new Object[]{slotId, type, status});
            }

            // If no data found
            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No slots found.", "Information", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Create a JTable with the populated table model
            JTable table = new JTable(tableModel);
            table.setEnabled(false); // Make the table non-editable

            // Customize table appearance
            table.setRowHeight(30);  // Set row height to add space between rows
            table.setFont(new Font("Arial", Font.PLAIN, 12));  // Set font size for table content

            // Set font size for table header
            table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

            // Create a JScrollPane to hold the table
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBounds(300, 120, 700, 450); // Adjust scrollPane bounds as needed

            // Remove any existing table (JScrollPane) from the content pane
            Component[] components = this.getContentPane().getComponents();
            for (Component component : components) {
                if (component instanceof JScrollPane) {
                    this.getContentPane().remove(component);
                }
            }

            // Add the scrollPane to the content pane
            this.getContentPane().add(scrollPane);
            this.getContentPane().revalidate();
            this.getContentPane().repaint();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching data from database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewVehicleTable() {
        // Define the query to fetch vehicle details
        String query = "SELECT id, license_plate, type, entry_time, exit_time, slot_id FROM vehicles";

        // Define column names for table header
        String[] columnNames = {"Vehicle ID", "License Plate", "Type", "Entry Time", "Exit Time", "Slot ID"};

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            // Create a DefaultTableModel with column names
            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

            // Populate the table with data from the ResultSet
            while (rs.next()) {
                int vehicleId = rs.getInt("id");
                String licensePlate = rs.getString("license_plate");
                String type = rs.getString("type");
                Timestamp entryTime = rs.getTimestamp("entry_time");
                Timestamp exitTime = rs.getTimestamp("exit_time");
                int slotId = rs.getInt("slot_id");

                // Add the row to the table model
                tableModel.addRow(new Object[]{vehicleId, licensePlate, type, entryTime, exitTime, slotId});
            }

            // If no data found
            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No vehicles found.", "Information", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Create a JTable with the populated table model
            JTable table = new JTable(tableModel);
            table.setEnabled(false); // Make the table non-editable

            // Customize table appearance
            table.setRowHeight(30);  // Set row height to add space between rows
            table.setFont(new Font("Arial", Font.PLAIN, 12));  // Set font size for table content

            // Set font size for table header
            table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

            // Create a JScrollPane to hold the table
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBounds(300, 120, 700, 450); // Adjust scrollPane bounds as needed

            // Remove any existing table (JScrollPane) from the content pane
            Component[] components = this.getContentPane().getComponents();
            for (Component component : components) {
                if (component instanceof JScrollPane) {
                    this.getContentPane().remove(component);
                }
            }

            // Add the scrollPane to the content pane
            this.getContentPane().add(scrollPane);
            this.getContentPane().revalidate();
            this.getContentPane().repaint();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching data from database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }



    private void addNewSlot() {
        String slotType = JOptionPane.showInputDialog(this, "Enter slot type (Car, Bike):");
        if (slotType != null && !slotType.trim().isEmpty()) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("INSERT INTO slots (type) VALUES (?)")) {
                stmt.setString(1, slotType);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "New slot added successfully.");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error adding new slot.");
            }
        }
    }

    private void viewOccupiedSlots() {
        // Define the query to fetch occupied slots with vehicle details
        String query = "SELECT s.id AS slot_id, s.type AS slot_type, v.id AS vehicle_id, v.license_plate " +
                "FROM slots s " +
                "JOIN vehicles v ON s.id = v.slot_id " +
                "WHERE s.status = 'occupied'";

        // Define column names for table header
        String[] columnNames = {"Slot ID", "Slot Type", "Vehicle ID", "License Plate"};

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            // Create a DefaultTableModel with column names
            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

            // Populate the table with data from the ResultSet
            while (rs.next()) {
                int slotId = rs.getInt("slot_id");
                String slotType = rs.getString("slot_type");
                int vehicleId = rs.getInt("vehicle_id");
                String licensePlate = rs.getString("license_plate");
                tableModel.addRow(new Object[]{slotId, slotType, vehicleId, licensePlate});
            }

            // If no data found
            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No occupied slots found.", "Information", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Create a JTable with the populated table model
            JTable table = new JTable(tableModel);
            table.setEnabled(false); // Make the table non-editable

            // Customize table appearance
            table.setRowHeight(30);  // Set row height to add space between rows
            table.setFont(new Font("Arial", Font.PLAIN, 12));  // Set font size for table content

            // Set font size for table header
            table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

            // Create a JScrollPane to hold the table
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBounds(300, 120, 700, 450); // Adjust scrollPane bounds as needed

            // Remove any existing table (JScrollPane) from the content pane
            Component[] components = this.getContentPane().getComponents();
            for (Component component : components) {
                if (component instanceof JScrollPane) {
                    this.getContentPane().remove(component);
                }
            }

            // Add the scrollPane to the content pane
            this.getContentPane().add(scrollPane);
            this.getContentPane().revalidate();
            this.getContentPane().repaint();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching data from database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }



    private void viewTransactionHistory() {
        // Define the SQL query to fetch transaction data
        String query = "SELECT t.id, t.vehicle_id, t.slot_id, t.amount, t.payment_time, " +
                "v.license_plate, s.type AS slot_type " +
                "FROM transactions t " +
                "JOIN vehicles v ON t.vehicle_id = v.id " +
                "JOIN slots s ON t.slot_id = s.id";

        // Define column names for table header
        String[] columnNames = {"Trans.ID", "Vehicle ID", "License Plate", "Slot ID", "Slot Type", "Amount", "Payment Time"};

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            // Create a DefaultTableModel with column names
            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

            // Populate the table with data from the ResultSet
            while (rs.next()) {
                int transactionId = rs.getInt("id");
                int vehicleId = rs.getInt("vehicle_id");
                String licensePlate = rs.getString("license_plate");
                int slotId = rs.getInt("slot_id");
                String slotType = rs.getString("slot_type");
                double amount = rs.getDouble("amount");
                Timestamp paymentTime = rs.getTimestamp("payment_time");

                tableModel.addRow(new Object[]{transactionId, vehicleId, licensePlate, slotId, slotType, amount, paymentTime});
            }

            // If no data found
            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No transaction history found.", "Information", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Create a JTable with the populated table model
            JTable table = new JTable(tableModel);
            table.setEnabled(false); // Make the table non-editable

            // Customize table appearance
            table.setRowHeight(30);  // Set row height to add space between rows
            table.setFont(new Font("Arial", Font.PLAIN, 12));  // Set font size for table content

            // Set font size for table header
            table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

            // Create a JScrollPane to hold the table
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBounds(300, 120, 700, 450); // Adjust scrollPane bounds as needed

            // Remove any existing table (JScrollPane) from the content pane
            Component[] components = this.getContentPane().getComponents();
            for (Component component : components) {
                if (component instanceof JScrollPane) {
                    this.getContentPane().remove(component);
                }
            }

            // Add the scrollPane to the content pane
            this.getContentPane().add(scrollPane);
            this.getContentPane().revalidate();
            this.getContentPane().repaint();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching transaction data from database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewTable(String query, String[] columnNames, String[] columnNamesWithData) {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            // Create a table model with the provided column names
            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

            // Populate rows from the ResultSet
            while (rs.next()) {
                Object[] rowData = new Object[columnNames.length];
                for (int i = 0; i < columnNames.length; i++) {
                    rowData[i] = rs.getObject(columnNamesWithData[i]);
                }
                tableModel.addRow(rowData);
            }

            // If no data found
            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No data found.", "Information", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Create a JTable and set the model
            JTable table = new JTable(tableModel);
            table.setEnabled(false); // Optional: Make table non-editable

            // Set row height to add gap between rows
            table.setRowHeight(30);  // Adjust row height for gap
            table.setFont(new Font("Arial", Font.PLAIN, 16));  // Set font size for table content

            // Set font size for table header
            table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 18));

            // Remove any existing table in the container before adding a new one
            Component[] components = this.getContentPane().getComponents();
            for (Component component : components) {
                if (component instanceof JScrollPane) {
                    this.getContentPane().remove(component);
                }
            }

            // Show in a JScrollPane
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBounds(100, 100, 600, 300);  // Adjust bounds as needed

            // Add the scrollPane to the container
            this.getContentPane().add(scrollPane);
            this.getContentPane().revalidate();
            this.getContentPane().repaint();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching data from database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void logout() {
        new WelcomeScreen().setVisible(true);
        dispose();
    }


}
