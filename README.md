# Linux Task Manager (LTM) Version 1.0
![Alt text](/screenshots/ltmlogo_50.jpg?raw=true "Process")

LTM (Linux Task Manager) is an open-source project that provides a graphical user interface (GUI) for monitoring and managing various aspects of a Linux system. It offers a convenient way to view and control processes, monitor system performance, manage startup applications, access user information, and handle services.

## Key Features

- Process Management: View and manage running processes, terminate or prioritize processes
- System Performance Monitoring: Real-time CPU, memory, disk, and network usage metrics.
- Application History: Track launched applications and their execution history.
- Startup Applications: Only Display the applications to launch at system startup.
- User Management: Monitor active users, login times, and associated processes.
- Service Control: Display the system services in real time.

## Key Features are still under development 
- Update Checking: Check for updates and download the latest version of the application.
- Service Control: Start, stop, and restart system services.
- Startup Applications: Configure applications to launch at system startup.
- System Performance Monitoring: Make it more advance.
- About us: Make it more professional.
- Fix the refresh problem by making the refresh from the Main.java to all the files by removing the refresh by each file on itself.
 
## Launch the application
java -jar LTM.jar 

## Things we used to develop the app:
- Eclipse IDE for Java Developers Version: 2022-09 (4.25.0)
- JavaSE-1.8
- Shell script

## Systems are tested on:
- Debian.
- Kali Linux.

## Project Directory/Structure/Tree
    .

    └── LICENSE
    └── LTM.jar
    └── README.md
    ├── bin
    │    └── jfreechart.jar
    │    ├── ltm
    │    │    └── AppHistoryPanel$1.class
    │    │    └── AppHistoryPanel$AppLaunchInfo.class
    │    │    └── AppHistoryPanel.class
    │    │    └── DetailsPanel.class
    │    │    └── Main.class
    │    │    └── PerformancePanel.class
    │    │    └── ProcessesPanel$1.class
    │    │    └── ProcessesPanel$2.class
    │    │    └── ProcessesPanel.class
    │    │    └── ServicesPanel.class
    │    │    └── StartupPanel$1.class
    │    │    └── StartupPanel$2.class
    │    │    └── StartupPanel$3.class
    │    │    └── StartupPanel$4.class
    │    │    └── StartupPanel$5.class
    │    │    └── StartupPanel$6.class
    │    │    └── StartupPanel.class
    │    │    └── UsersPanel.class
    │    │    ├── scripts
    │    │    │    └── app_history.sh
    │    │    │    └── details.sh
    │    │    │    └── list_running_processes.sh
    │    │    │    └── performance_data.sh
    │    │    │    └── services_data.sh
    │    │    │    └── set_execute_permission.sh
    │    │    │    └── startup_data.sh
    │    │    │    └── system_details.sh
    │    │    │    └── user_data.sh
    ├── screenshots
    │    └── ApplicationHistory.jpg
    │    └── PCdetials.jpg
    │    └── Process.jpg
    │    └── ServiceControl.jpg
    │    └── StartupApplications.jpg
    │    └── SystemPerformanceMonitoring.jpg
    │    └── UserManagement.jpg
    │    └── ltmlogo.jpg
    │    └── ltmlogo_50.jpg
    ├── src
    │    └── jfreechart.jar
    │    ├── ltm
    │    │    └── AppHistoryPanel.java
    │    │    └── DetailsPanel.java
    │    │    └── LTMApplication.java
    │    │    └── Main.java
    │    │    └── PerformancePanel.java
    │    │    └── ProcessesPanel.java
    │    │    └── ServicesPanel.java
    │    │    └── StartupPanel.java
    │    │    └── UsersPanel.java
    │    │    ├── scripts
    │    │    │    └── app_history.sh
    │    │    │    └── details.sh
    │    │    │    └── list_running_processes.sh
    │    │    │    └── performance_data.sh
    │    │    │    └── services_data.sh
    │    │    │    └── set_execute_permission.sh
    │    │    │    └── startup_data.sh
    │    │    │    └── system_details.sh
    │    │    │    └── user_data.sh
    └── version.txt






## Acknowledgements

The LTM project relies on various open-source libraries and resources. Special thanks to the contributors of these projects for their valuable work.

- [FlatLaf](https://www.formdev.com/flatlaf/): Java Swing Look and Feel library.

## Contributing

Contributions to LTM are welcome!
Contributions to this project are welcome. If you have any suggestions, bug reports, or feature requests, please open an issue or submit a pull request.

## License
This project is licensed under the [MIT license](LICENSE).

## Screenshots

![Alt text](/screenshots/Process.jpg?raw=true "Process")
![Alt text](/screenshots/SystemPerformanceMonitoring.jpg?raw=true "Process")
![Alt text](/screenshots/ApplicationHistory.jpg?raw=true "Process")
![Alt text](/screenshots/StartupApplications.jpg?raw=true "Process")
![Alt text](/screenshots/UserManagement.jpg?raw=true "Process")
![Alt text](/screenshots/PCdetials.jpg?raw=true "Process")
![Alt text](/screenshots/ServiceControl.jpg?raw=true "Process")


