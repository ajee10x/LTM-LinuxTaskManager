package ltm;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class PerformancePanel extends JPanel {
    private JTextArea performanceTextArea;
    private Timer refreshTimer;

    public PerformancePanel() {
        // Set the layout for the panel
        setLayout(new BorderLayout());

        // Create the components for the Performance tab
        JLabel titleLabel = new JLabel("Performance");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16)); // Set the font and size for the title label

        performanceTextArea = new JTextArea();
        performanceTextArea.setEditable(false); // Disable editing
        performanceTextArea.setFont(new Font("Consolas", Font.PLAIN, 12)); // Set the font and size for the text area
        performanceTextArea.setLineWrap(true); // Enable line wrapping
        performanceTextArea.setWrapStyleWord(true); // Wrap at word boundaries

        // Create a panel for the title label
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.add(titleLabel, BorderLayout.WEST);

        // Create a panel for the text area
        JPanel textAreaPanel = new JPanel(new BorderLayout());
        textAreaPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding
        textAreaPanel.add(new JScrollPane(performanceTextArea), BorderLayout.CENTER);

        // Add the components to the panel
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding to the panel
        add(titlePanel, BorderLayout.NORTH);
        add(textAreaPanel, BorderLayout.CENTER);

        // Create a timer to refresh the performance data at a specified interval
        int refreshInterval = 5000; // Refresh interval in milliseconds (e.g., 5000 = 5 seconds)
        refreshTimer = new Timer(refreshInterval, e -> refreshPerformance());
        refreshTimer.start();

        // Refresh the performance data initially
        refreshPerformance();
    }

    private void refreshPerformance() {
        try {
            // Read the script file as a resource
            InputStream inputStream = getClass().getResourceAsStream("scripts/performance_data.sh");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            // Execute the script and capture the output
            ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash");
            Process process = processBuilder.start();
            process.getOutputStream().write(reader.lines().collect(Collectors.joining("\n")).getBytes());
            process.getOutputStream().close();

            // Read the output from the script
            StringBuilder output = new StringBuilder();
            String line;
            BufferedReader processOutputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = processOutputReader.readLine()) != null) {
                output.append(line).append("\n");
            }

            // Display the output in the text area
            performanceTextArea.setText(output.toString());

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
}

