package ltm;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StartupPanel extends JPanel {
    private JTable startupTable;

    public StartupPanel() {
        // Set the layout for the panel
        setLayout(new BorderLayout());

        // Create the components for the Startup tab
        JLabel titleLabel = new JLabel("Startup");
        startupTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(startupTable);

        // Add the components to the panel
        add(titleLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Add a popup menu for options
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem startItem = new JMenuItem("Start");
        JMenuItem stopItem = new JMenuItem("Stop");
        popupMenu.add(startItem);
        popupMenu.add(stopItem);

        // Add mouse listener to show popup menu
        startupTable.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopupMenu(e.getX(), e.getY());
                }
            }

            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopupMenu(e.getX(), e.getY());
                }
            }
        });

        // Add action listeners for popup menu items
        startItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row = startupTable.getSelectedRow();
                if (row != -1) {
                    String startupItem = (String) startupTable.getValueAt(row, 0);
                    startStartupItem(startupItem);
                }
            }
        });

        stopItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row = startupTable.getSelectedRow();
                if (row != -1) {
                    String startupItem = (String) startupTable.getValueAt(row, 0);
                    stopStartupItem(startupItem);
                }
            }
        });

        // Refresh the startup items initially
        refreshStartupItems();

        // Schedule periodic refresh of startup items
        int refreshInterval = 5000; // Refresh every 5 seconds (adjust as needed)
        Timer timer = new Timer(refreshInterval, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                refreshStartupItems();
            }
        });
        timer.start();
    }

    private void refreshStartupItems() {
        try {
            // Read the script file as a resource
            InputStream inputStream = getClass().getResourceAsStream("scripts/startup_data.sh");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            // Execute the script and capture the output
            ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash");
            Process process = processBuilder.start();
            process.getOutputStream().write(reader.lines().collect(Collectors.joining("\n")).getBytes());
            process.getOutputStream().close();

            // Read the output from the script
            List<String> startupData = new ArrayList<>();
            String line;
            BufferedReader processOutputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = processOutputReader.readLine()) != null) {
                startupData.add(line);
            }

            // Wait for the process to complete
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                // Update the table model with the startup data
                DefaultTableModel tableModel = (DefaultTableModel) startupTable.getModel();
                tableModel.setColumnCount(1); // Set the number of columns
                tableModel.setRowCount(0); // Clear previous data
                for (String startupInfo : startupData) {
                    tableModel.addRow(new Object[]{startupInfo});
                }
            } else {
                // Handle script execution error
                System.err.println("Error executing script. Exit code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void showPopupMenu(int x, int y) {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem startItem = new JMenuItem("Start");
        JMenuItem stopItem = new JMenuItem("Stop");

        startItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row = startupTable.rowAtPoint(new Point(x, y));
                if (row != -1) {
                    String startupItem = (String) startupTable.getValueAt(row, 0);
                    startStartupItem(startupItem);
                }
            }
        });

        stopItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row = startupTable.rowAtPoint(new Point(x, y));
                if (row != -1) {
                    String startupItem = (String) startupTable.getValueAt(row, 0);
                    stopStartupItem(startupItem);
                }
            }
        });

        popupMenu.add(startItem);
        popupMenu.add(stopItem);
        popupMenu.show(startupTable, x, y);
    }

    private void startStartupItem(String startupItem) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", "sudo service " + startupItem + " start");
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Started: " + startupItem);
            } else {
                System.err.println("Error starting: " + startupItem);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void stopStartupItem(String startupItem) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", "sudo service " + startupItem + " stop");
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Stopped: " + startupItem);
            } else {
                System.err.println("Error stopping: " + startupItem);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

