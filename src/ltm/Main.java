package ltm;

import javax.swing.*;
import java.awt.*;

import com.formdev.flatlaf.FlatLightLaf;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            // Create the main frame
            JFrame frame = new JFrame("Linux Task Manager");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);

            // Create the menu bar
            JMenuBar menuBar = new JMenuBar();

            // Create the "File" menu
            JMenu fileMenu = new JMenu("File");

            // Create the "Check for Update" menu item
            JMenuItem checkUpdateMenuItem = new JMenuItem("Check for Update");
            checkUpdateMenuItem.addActionListener(e -> {
                // Perform the action when "Check for Update" is clicked
                JOptionPane.showMessageDialog(frame, "Checking for update...");
                // TODO: Add your update check logic here
            });
            fileMenu.add(checkUpdateMenuItem);

            // Create the "About Us" menu item
            JMenuItem aboutUsMenuItem = new JMenuItem("About Us");
            aboutUsMenuItem.addActionListener(e -> {
                // Perform the action when "About Us" is clicked
                JOptionPane.showMessageDialog(frame, "This is our application. Version 1.0");
                // TODO: Add your about us logic here
            });
            fileMenu.add(aboutUsMenuItem);

            // Add the "File" menu to the menu bar
            menuBar.add(fileMenu);

            // Set the menu bar to the frame
            frame.setJMenuBar(menuBar);

            // Create the tabbed pane
            JTabbedPane tabbedPane = new JTabbedPane();

            // Create and add tabs
            tabbedPane.addTab("Processes", new ProcessesPanel());
            tabbedPane.addTab("Performance", new PerformancePanel());
            tabbedPane.addTab("App History", new AppHistoryPanel());
            tabbedPane.addTab("Startup", new StartupPanel());
            tabbedPane.addTab("Users", new UsersPanel());
            tabbedPane.addTab("Details", new DetailsPanel());
            tabbedPane.addTab("Services", new ServicesPanel());

            // Set the tabbed pane as the content pane of the frame
            frame.setContentPane(tabbedPane);

            // Show the frame
            frame.setVisible(true);
        });
    }
}
