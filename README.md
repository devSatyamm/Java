# CampusOS - Smart Campus Management System

CampusOS is a Java Swing-based desktop application for managing campus operations including Student Management, Hostel Allocation, Fee Management, and Complaint Tracking.

## Features
- **Authentication**: Role-based login (Admin and Student).
- **Student Management**: View, add, update, and manage student details.
- **Hostel Management**: Manage room allocations and occupancy.
- **Fee Management**: Track and update student fee payments.
- **Complaint Management**: Log and resolve campus grievances.

## Tech Stack
- **Language**: Java (JDK 8+)
- **UI Framework**: Java Swing / AWT
- **Data Storage**: File-based persistence (`data/`)

## Getting Started

### Prerequisites
- Java Development Kit (JDK) 8 or higher

### Compile and Run
To compile from the project root:
```bash
javac -d out src/campusos/*.java src/campusos/**/*.java
```

To run the application:
```bash
java -cp out campusos.Main
```

### Default Credentials
- **Student**: STU101 / `student123`
- **Admin**: ADM101 / `admin123`
