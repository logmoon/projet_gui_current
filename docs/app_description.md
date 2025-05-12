# AgroAssist: Comprehensive Farm Management Application

## Application Overview
AgroAssist is a JavaFX desktop application designed to streamline farm management for agricultural producers. The application focuses on field (parcelle) management, crop tracking, task organization, and weather monitoring to optimize farming operations.

## User Interface Framework
The application utilizes a **modular sidebar navigation system** that appears consistently across all views, providing:
- Quick access to all major application sections
- User profile information and logout option
- Notification indicators
- Contextual actions relevant to the current view
You can find a starter implementation of this sidebar in the page_profile.fxml file.

## User Workflows

### Farmer User Workflow

**Login & Authentication**
- Farmers log in with their credentials
- Password recovery available through verification code system
- Failed login attempts are logged for security

**Dashboard (Home View)**
- Upon successful login, farmers see their personalized dashboard
- Visual summary of all fields with status indicators
- List of upcoming tasks with priority levels
- Weather warnings specifically attached to their fields
- Recent notifications and reminders
- Quick action buttons for common operations

**Field Management (Parcelles)**
- Comprehensive list view of all fields owned by the farmer
- Each field displays:
  - Field name, size, and area
  - Current status (Normal/Needs Attention/Critical)
  - Field location
  - Field image (if available)
- Detailed view for each field showing:
  - All crops currently planted
  - Tasks associated with the field
  - Weather data specific to the field
  - Crop history and yields
- CRUD operations for managing fields:
  - Create new fields with required information
  - Update field details and status
  - Delete fields (with appropriate safeguards)

**Crop Management (Cultures)**
- View all available crop types with images and descriptions
- Add/remove crops to specific fields
- Track crop status (Healthy/Sick/Harvested/Failed)
- Record and view crop reports
- Associate diseases (maladies) with crops if they're sick
- Disease information appears in both crop and field views
- View historical crop data including yields

**Task Management**
- Create, edit, and delete tasks associated with specific fields
- Set priority levels and deadlines
- Mark tasks as In Progress or Complete
- Filter tasks by status, priority, or field
- Set reminders for important farming activities

**Weather & Disease Information**
- View current and recent weather data specific to each field
- Browse common plant diseases and their treatments
- Weather alerts displayed prominently when conditions are concerning
- Disease reference guide with symptoms and recommended actions

**Profile & Security**
- Edit personal information
- Change password
- View login history
- Monitor security actions on the account

### Administrator Workflow

**Admin Dashboard**
- System-wide overview of all users, fields, and activities
- Alert indicators for security events or issues requiring attention
- Quick access to admin-specific functions

**User Management**
- View complete list of system users
- Create new user accounts
- Edit user information
- Assign user roles (Admin/Farmer)
- Activate/deactivate accounts
- Delete user accounts
- Review user login history

**Data Management**
- Full access to all fields in the system regardless of owner
- View and manage all crop types
- Add/edit disease information
- Monitor all tasks and reminders in the system
- View all weather data and locations

**System Administration**
- Send system-wide notifications to users
- Review security logs
- Manage password reset requests
- Configure application settings

## Password Recovery Process
1. User requests password reset through login screen
2. System generates unique verification code and stores in `ReinitialisationMotDePasse` table
3. Code is sent to user's email (implementation to be determined)
4. User enters code in password reset form
5. If valid code is provided, user can set new password
6. Admin can view/manage reset requests through admin interface

## Technical Details

### Database Structure
The application uses a relational database with tables for:
- User management (`Utilisateur`, `Role`, `AuthToken`)
- Field tracking (`Parcelle`, `Localisation`) including images and area
- Crop management (`Culture`, `ParcelleCulture`, `RapportCulture`)
- Task organization (`Tache`, `Rappel`)
- Weather data (`Meteo`) with alert functionality
- Disease information (`Maladie`)
- System logs (`JournalConnexion`, `JournalSecurite`)
- Application settings (`ParametresApplication`)

### Key Features
- CRUD operations for all major entities (fields, crops, tasks, etc.)
- Image storage for fields and crops
- Status tracking for fields, crops, and tasks
- Weather monitoring and alerts
- Disease reference and tracking
- User role-based access control
- Comprehensive security logging

The application aims to provide farmers with a complete digital management system that enhances productivity, helps track field conditions, and simplifies agricultural planning and monitoring.