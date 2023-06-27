package ltm;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UsersPanel extends JPanel {
    private JTable usersTable;

    public UsersPanel() {
        // Set the layout for the panel
        setLayout(new BorderLayout());

        // Create the components for the Users tab
        JLabel titleLabel = new JLabel("Users");
        usersTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(usersTable);

        // Add the components to the panel
        add(titleLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Refresh the users initially
        refreshUsers();

        // Schedule periodic refresh of users
        int refreshInterval = 5000; // Refresh every 5 seconds (adjust as needed)
        Timer timer = new Timer(refreshInterval, e -> refreshUsers());
        timer.start();
    }

    private void refreshUsers() {
        try {
            // Read the script file as a resource
            InputStream inputStream = getClass().getResourceAsStream("scripts/user_data.sh");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            // Execute the script and capture the output
            ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash");
            Process process = processBuilder.start();
            process.getOutputStream().write(reader.lines().collect(Collectors.joining("\n")).getBytes());
            process.getOutputStream().close();

            // Read the output from the script
            List<String> userData = new ArrayList<>();
            String line;
            BufferedReader processOutputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = processOutputReader.readLine()) != null) {
                userData.add(line);
            }

            // Wait for the process to complete
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                // Create the table model with column names
                String[] columnNames = {"Username"};
                DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

                // Add the user data to the table model
                for (String username : userData) {
                    tableModel.addRow(new Object[]{username});
                }

                // Set the table model
                usersTable.setModel(tableModel);
            } else {
                // Handle script execution error
                System.err.println("Error executing script. Exit code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}


