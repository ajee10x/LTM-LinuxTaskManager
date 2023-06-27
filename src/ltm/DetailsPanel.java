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


public class DetailsPanel extends JPanel {
    private JTable detailsTable;

    public DetailsPanel() {
        // Set the layout for the panel
        setLayout(new BorderLayout());

        // Create the components for the Details tab
        JLabel titleLabel = new JLabel("Details");
        detailsTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(detailsTable);

        // Add the components to the panel
        add(titleLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Refresh the details initially
        refreshDetails();

        // Schedule periodic refresh of details
        int refreshInterval = 5000; // Refresh every 5 seconds (adjust as needed)
        Timer timer = new Timer(refreshInterval, e -> refreshDetails());
        timer.start();
    }

    private void refreshDetails() {
        try {
            // Read the script file as a resource
            InputStream inputStream = getClass().getResourceAsStream("scripts/details.sh");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            // Execute the script and capture the output
            ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash");
            Process process = processBuilder.start();
            process.getOutputStream().write(reader.lines().collect(Collectors.joining("\n")).getBytes());
            process.getOutputStream().close();

            // Read the output from the script
            List<String> detailsData = new ArrayList<>();
            String line;
            BufferedReader processOutputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = processOutputReader.readLine()) != null) {
                detailsData.add(line);
            }

            // Wait for the process to complete
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                // Update the table model with the details data
                DefaultTableModel tableModel = new DefaultTableModel();
                tableModel.setColumnIdentifiers(new String[]{"Username", "Details"});
                detailsTable.setModel(tableModel);
                for (String detailsInfo : detailsData) {
                    String[] rowData = detailsInfo.split(":");
                    tableModel.addRow(rowData);
                }
            } else {
                // Handle script execution error
                System.err.println("Error executing script. Exit code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}

