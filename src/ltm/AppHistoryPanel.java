package ltm;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class AppHistoryPanel extends JPanel {
    private JTable historyTable;
    private Timer refreshTimer;

    public AppHistoryPanel() {
        // Set the layout for the panel
        setLayout(new BorderLayout());

        // Create the components for the App History tab
        JLabel titleLabel = new JLabel("App History");
        historyTable = new JTable();

        // Set up the table model
        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Disable cell editing
            }
        };
        tableModel.addColumn("Application");
        tableModel.addColumn("Exec Command");
        tableModel.addColumn("Last Accessed");
        historyTable.setModel(tableModel);

        // Set table styles
        historyTable.setRowHeight(25);
        historyTable.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        historyTable.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));

        // Create a panel to hold the App History table
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(new JScrollPane(historyTable), BorderLayout.CENTER);

        // Add the components to the panel
        add(titleLabel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        // Create a timer to refresh the app history at a specified interval
        int refreshInterval = 5000; // Refresh interval in milliseconds (e.g., 5000 = 5 seconds)
        refreshTimer = new Timer(refreshInterval, e -> refreshAppHistory());
        refreshTimer.start();

        // Refresh the app history initially
        refreshAppHistory();
    }

    private void refreshAppHistory() {
        try {
            // Get the home directory of the current user
            String userHome = System.getProperty("user.home");
            Path recentlyUsedFile = Paths.get(userHome, ".local", "share", "recently-used.xbel");

            // Check if the recently-used.xbel file exists
            if (!recentlyUsedFile.toFile().exists()) {
                System.err.println("recently-used.xbel file does not exist.");
                return;
            }

            // Load the XML file
            File xmlFile = recentlyUsedFile.toFile();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);

            // Extract the launching information
            List<AppLaunchInfo> launchingInfo = extractLaunchingInfo(document);

            // Update the table model with the launching information
            DefaultTableModel tableModel = (DefaultTableModel) historyTable.getModel();
            tableModel.setRowCount(0); // Clear previous data
            for (AppLaunchInfo info : launchingInfo) {
                tableModel.addRow(new Object[]{info.getApplication(), info.getExecCommand(), info.getLastAccessed()});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<AppLaunchInfo> extractLaunchingInfo(Document document) {
        List<AppLaunchInfo> launchingInfo = new ArrayList<>();
        NodeList bookmarkNodes = document.getElementsByTagName("bookmark");
     // for (int i = 0; i < bookmarkNodes.getLength(); i++) {
        for (int i = bookmarkNodes.getLength() - 1; i >= 0; i--) {
            Element bookmarkElement = (Element) bookmarkNodes.item(i);
            String application = getApplicationName(bookmarkElement);
            String execCommand = getExecCommand(bookmarkElement);
            String lastAccessed = getLastAccessed(bookmarkElement);
            launchingInfo.add(new AppLaunchInfo(application, execCommand, lastAccessed));
        }
        return launchingInfo;
    }

    private String getApplicationName(Element bookmarkElement) {
        Element applicationElement = (Element) bookmarkElement.getElementsByTagName("bookmark:application").item(0);
        return applicationElement.getAttribute("name");
    }

    private String getExecCommand(Element bookmarkElement) {
        Element applicationElement = (Element) bookmarkElement.getElementsByTagName("bookmark:application").item(0);
        return applicationElement.getAttribute("exec");
    }

    private String getLastAccessed(Element bookmarkElement) {
        return bookmarkElement.getAttribute("added");
    }

    private class AppLaunchInfo {
        private String application;
        private String execCommand;
        private String lastAccessed;

        public AppLaunchInfo(String application, String execCommand, String lastAccessed) {
            this.application = application;
            this.execCommand = execCommand;
            this.lastAccessed = lastAccessed;
        }

        public String getApplication() {
            return application;
        }

        public String getExecCommand() {
            return execCommand;
        }

        public String getLastAccessed() {
            return lastAccessed;
        }
    }
}

