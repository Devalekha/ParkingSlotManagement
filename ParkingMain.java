package parking;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JPanel;
import java.awt.FlowLayout;

public class ParkingMain extends JFrame implements ActionListener {
    JButton addBtn, removeBtn, viewBtn;
    JTextField vehicleNo, ownerName;
    Connection con;

    ParkingMain() {
        setTitle("Parking Slot Management");
        setSize(400, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        vehicleNo = new JTextField(10);
        ownerName = new JTextField(10);
        addBtn = new JButton("Check-In");
        removeBtn = new JButton("Check-Out");
        viewBtn = new JButton("View Slots");

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(2, 2, 10, 15));

        inputPanel.add(new JLabel("Vehicle No:"));
        inputPanel.add(vehicleNo);
        inputPanel.add(new JLabel("Owner Name:"));
        inputPanel.add(ownerName);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.add(addBtn);
        buttonPanel.add(removeBtn);
        buttonPanel.add(viewBtn);

        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);
        
        addBtn.addActionListener(this);
        removeBtn.addActionListener(this);
        viewBtn.addActionListener(this);

        connectDB();
        setVisible(true);
    }

    void connectDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/parkingdb",
                    "root",
                    "Lekha@2007"  // <-- replace with your actual MySQL password
            );
            JOptionPane.showMessageDialog(this, "Database Connection Successful!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "DB Connection Error: " + e);
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addBtn) { // Check-In
            try {
                String vno = vehicleNo.getText();
                String owner = ownerName.getText();

                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO vehicles (vehicle_no, owner_name, entry_time, status) VALUES (?, ?, NOW(), 'PARKED')");
                ps.setString(1, vno);
                ps.setString(2, owner);

                int rows = ps.executeUpdate();
                if (rows > 0)
                    JOptionPane.showMessageDialog(this, "Vehicle " + vno + " checked in successfully!");
                else
                    JOptionPane.showMessageDialog(this, "Check-in failed!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }

        if (e.getSource() == removeBtn) { // Check-Out
            try {
                String vno = vehicleNo.getText();

                PreparedStatement ps = con.prepareStatement(
                        "UPDATE vehicles SET exit_time = NOW(), status = 'EXITED' WHERE vehicle_no = ? AND status = 'PARKED'");
                ps.setString(1, vno);

                int rows = ps.executeUpdate();
                if (rows > 0)
                    JOptionPane.showMessageDialog(this, "Vehicle " + vno + " checked out successfully!");
                else
                    JOptionPane.showMessageDialog(this, "Vehicle not found or already exited!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }

        if (e.getSource() == viewBtn) { // View All
            try {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM vehicles");

                StringBuilder data = new StringBuilder("Vehicle_No\tOwner\tStatus\n");
                while (rs.next()) {
                    data.append(rs.getString("vehicle_no")).append("\t")
                        .append(rs.getString("owner_name")).append("\t")
                        .append(rs.getString("status")).append("\n");
                }
                JTextArea area = new JTextArea(data.toString());
                JOptionPane.showMessageDialog(this, new JScrollPane(area));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        new ParkingMain();
    }
}


