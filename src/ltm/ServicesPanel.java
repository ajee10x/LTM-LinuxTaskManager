package ltm;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.List;

public class ServicesPanel extends JPanel {
    private JTable servicesTable;
    private JPopupMenu popupMenu;
    private JPasswordField passwordField;

    public ServicesPanel() {
        // Set the layout for the panel
        setLayout(new BorderLayout());

        // Create the components for the Services tab
        JLabel titleLabel = new JLabel("Services");
        servicesTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(servicesTable);

        // Add the components to the panel
        add(titleLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Create the popup menu and its options
        popupMenu = new JPopupMenu();
        JMenuItem startMenuItem = new JMenuItem("Start");
        JMenuItem stopMenuItem = new JMenuItem("Stop");
        JMenuItem restartMenuItem = new JMenuItem("Restart");

        // Add action listeners to the menu items
        startMenuItem.addActionListener(e -> {
            String selectedService = getSelectedService();
            if (selectedService != null) {
                String password = getPassword();
                if (password != null) {
                    executeSudoCommand("service " + selectedService + " start", password);
                }
            }
        });

        stopMenuItem.addActionListener(e -> {
            String selectedService = getSelectedService();
            if (selectedService != null) {
                String password = getPassword();
                if (password != null) {
                    executeSudoCommand("service " + selectedService + " stop", password);
                }
            }
        });

        restartMenuItem.addActionListener(e -> {
            String selectedService = getSelectedService();
            if (selectedService != null) {
                String password = getPassword();
                if (password != null) {
                    executeSudoCommand("service " + selectedService + " restart", password);
                }
            }
        });

        // Add the menu items to the popup menu
        popupMenu.add(startMenuItem);
        popupMenu.add(stopMenuItem);
        popupMenu.add(restartMenuItem);

        // Attach the popup menu to the table
        servicesTable.setComponentPopupMenu(popupMenu);

        // Refresh the services initially
        refreshServices();

        // Schedule periodic refresh of services
        int refreshInterval = 5000; // Refresh every 5 seconds (adjust as needed)
        Timer timer = new Timer(refreshInterval, e -> refreshServices());
        timer.start();
    }

    private String getSelectedService() {
        int selectedRow = servicesTable.getSelectedRow();
        if (selectedRow != -1) {
            String serviceInfo = servicesTable.getValueAt(selectedRow, 0).toString();
            // Split the service information by whitespace and return the first element
            return serviceInfo.split("\\s+")[0];
        }
        return null;
    }


    private void refreshServices() {
        try {
            // Read the script file as a resource
            InputStream inputStream = getClass().getResourceAsStream("scripts/services_data.sh");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            // Execute the script and capture the output
            ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash");
            Process process = processBuilder.start();
            process.getOutputStream().write(reader.lines().collect(Collectors.joining("\n")).getBytes());
            process.getOutputStream().close();

            // Read the output from the script
            StringBuilder serviceDataBuilder = new StringBuilder();
            String line;
            BufferedReader processOutputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = processOutputReader.readLine()) != null) {
                serviceDataBuilder.append(line.trim()).append("\n");
            }

            // Remove leading and trailing newlines
            String serviceData = serviceDataBuilder.toString().trim();

            // Split the service data by newline
            String[] serviceInfo = serviceData.split("\\n");

            // Update the table model with the service data
            DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"Service Information"}, serviceInfo.length);
            for (int i = 0; i < serviceInfo.length; i++) {
                tableModel.setValueAt(serviceInfo[i], i, 0);
            }
            servicesTable.setModel(tableModel);

            // Wait for the process to complete
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                // Handle any errors if necessary
                System.err.println("Error executing script. Exit code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String getPassword() {
        passwordField = new JPasswordField();
        Object[] message = {"Enter sudo password:", passwordField};
        int option = JOptionPane.showConfirmDialog(null, message, "Sudo Password", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            return new String(passwordField.getPassword());
        }
        return null;
    }

    private void executeSudoCommand(String command, String password) {
        try {
            // Execute the sudo command
            List<String> sudoCommand = new ArrayList<>(Arrays.asList("sudo", "-S"));
            sudoCommand.addAll(Arrays.asList(command.split("\\s+")));

            ProcessBuilder processBuilder = new ProcessBuilder(sudoCommand);
            Process process = processBuilder.start();

            // Write the password to the process input stream
            OutputStream outputStream = process.getOutputStream();
            outputStream.write((password + "\n").getBytes());
            outputStream.flush();
            outputStream.close();

            // Read the output from the process
            StringBuilder outputBuilder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                outputBuilder.append(line).append("\n");
            }
            String output = outputBuilder.toString();

            // Print the output to the terminal
            System.out.println(output);

            // Wait for the process to complete
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                JOptionPane.showMessageDialog(null, "Command executed successfully:\n\n" + output, "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Error executing command:\n\n" + output, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}


