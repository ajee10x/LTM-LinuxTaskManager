package ltm;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProcessesPanel extends JPanel {
    private JTable processTable;
    private JProgressBar cpuProgressBar;
    private JProgressBar memoryProgressBar;
    private Timer refreshTimer;

    public ProcessesPanel() {
        // Set the layout for the panel
        setLayout(new BorderLayout());

        // Create the components for the Processes tab
        JLabel titleLabel = new JLabel("Processes");
        processTable = new JTable();

        // Set up the table model
        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Disable cell editing
            }
        };
        tableModel.addColumn("Name");
        tableModel.addColumn("PID");
        tableModel.addColumn("CPU (%)");
        tableModel.addColumn("Memory (%)");
        processTable.setModel(tableModel);

        // Set custom cell renderers for centering and styling
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        processTable.setDefaultRenderer(Object.class, centerRenderer);

        // Set column widths
        TableColumnModel columnModel = processTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(200);
        columnModel.getColumn(1).setPreferredWidth(80);
        columnModel.getColumn(2).setPreferredWidth(100);
        columnModel.getColumn(3).setPreferredWidth(100);

        // Set table styles
        processTable.setRowHeight(25);
        processTable.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        processTable.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));

        // Create the CPU and Memory progress bars
        cpuProgressBar = new JProgressBar(0, 100);
        memoryProgressBar = new JProgressBar(0, 100);
        cpuProgressBar.setStringPainted(true);
        memoryProgressBar.setStringPainted(true);
        //cpuProgressBar.setForeground(Color.green);
        //cpuProgressBar.setBackground(Color.black);
        //memoryProgressBar.setForeground(Color.green);
        //memoryProgressBar.setBackground(Color.black);


        // Create a panel for the CPU and Memory charts
        JPanel chartPanel = new JPanel(new GridLayout(2, 1));
        chartPanel.add(cpuProgressBar);
        chartPanel.add(memoryProgressBar);

        // Create a panel to hold the Processes table and chart panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(new JScrollPane(processTable), BorderLayout.CENTER);
        contentPanel.add(chartPanel, BorderLayout.WEST);

        // Add the components to the panel
        add(titleLabel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        // Create a timer to refresh the processes at a specified interval
        int refreshInterval = 5000; // Refresh interval in milliseconds (e.g., 5000 = 5 seconds)
        refreshTimer = new Timer(refreshInterval, e -> refreshProcesses());
        refreshTimer.start();

        // Add a right-click menu to the process table
        processTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = processTable.rowAtPoint(e.getPoint());
                    if (row >= 0 && row < processTable.getRowCount()) {
                        processTable.setRowSelectionInterval(row, row);
                        JPopupMenu popupMenu = createPopupMenu();
                        popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }
        });

        // Refresh the processes initially
        refreshProcesses();
    }

    private JPopupMenu createPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem endTaskMenuItem = new JMenuItem("End Task");
        endTaskMenuItem.addActionListener((ActionEvent e) -> {
            int selectedRow = processTable.getSelectedRow();
            if (selectedRow >= 0 && selectedRow < processTable.getRowCount()) {
                String pid = (String) processTable.getValueAt(selectedRow, 1);
                endTask(pid);
            }
        });
        popupMenu.add(endTaskMenuItem);

        JMenuItem propertiesMenuItem = new JMenuItem("Properties");
        propertiesMenuItem.addActionListener((ActionEvent e) -> {
            int selectedRow = processTable.getSelectedRow();
            if (selectedRow >= 0 && selectedRow < processTable.getRowCount()) {
                String name = (String) processTable.getValueAt(selectedRow, 0);
                String pid = (String) processTable.getValueAt(selectedRow, 1);
                showProperties( pid);
            }
        });
        popupMenu.add(propertiesMenuItem);

        return popupMenu;
    }

    private void endTask(String pid) {
        try {
            // Construct the kill command
            String command = "kill -9 " + pid;

            // Execute the kill command
            Process process = Runtime.getRuntime().exec(command);

            // Wait for the process to complete
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Process with PID " + pid + " terminated successfully.");
            } else {
                System.err.println("Error terminating process with PID " + pid + ". Exit code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }



    private void showProperties(String pid) {
        try {
            // Construct the ps command
            String command = "ps -p " + pid + " -o %cpu,%mem,etime";

            // Execute the ps command
            Process process = Runtime.getRuntime().exec(command);

            // Read the output from the process
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // Wait for the process to complete
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("Error showing properties for process with PID " + pid + ". Exit code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void refreshProcesses() {
        try {
            // Read the script file as a resource
            InputStream inputStream = getClass().getResourceAsStream("scripts/list_running_processes.sh");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            // Execute the script and capture the output
            ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash");
            Process process = processBuilder.start();
            process.getOutputStream().write(reader.lines().collect(Collectors.joining("\n")).getBytes());
            process.getOutputStream().close();

            // Read the output from the script
            List<String[]> processData = new ArrayList<>();
            String line;
            BufferedReader processOutputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = processOutputReader.readLine()) != null) {
                String[] processInfo = line.split("\\s+");
                processData.add(processInfo);
            }

            // Update the table model with the process data
            DefaultTableModel tableModel = (DefaultTableModel) processTable.getModel();
            tableModel.setRowCount(0); // Clear previous data
            for (String[] processInfo : processData) {
                tableModel.addRow(processInfo);
            }

            // Calculate CPU and Memory percentages
            double cpuPercentage = calculateCpuPercentage(processData) * 100;
            double memoryPercentage = calculateMemoryPercentage(processData) * 100;

            // Update the CPU and Memory progress bars
            cpuProgressBar.setValue((int) cpuPercentage);
            cpuProgressBar.setString(String.format("CPU: %.0f%%", cpuPercentage));
            memoryProgressBar.setValue((int) memoryPercentage);
            memoryProgressBar.setString(String.format("Memory: %.0f%%", memoryPercentage));

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

    private double calculateCpuPercentage(List<String[]> processData) {
        double totalCpu = 0.0;
        for (String[] processInfo : processData) {
            double cpu = Double.parseDouble(processInfo[2]);
            totalCpu += cpu;
        }
        return totalCpu / processData.size();
    }

    private double calculateMemoryPercentage(List<String[]> processData) {
        double totalMemory = 0.0;
        for (String[] processInfo : processData) {
            double memory = Double.parseDouble(processInfo[3]);
            totalMemory += memory;
        }
        return totalMemory / processData.size();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("ProcessesPanel Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new ProcessesPanel());
        frame.pack();
        frame.setVisible(true);
    }
}


