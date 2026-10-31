# Smart Waste Segregation and Collection Management System

Java 17 • Swing • FlatLaf (dark theme) • JFreeChart • MySQL • JDBC • Maven

## 1. Install requirements
1. **JDK 17 or newer** (IntelliJ: File > Project Structure > SDK > Download JDK > 17+).
2. **MySQL Server 8** running on localhost:3306 (MySQL Installer on Windows).
3. IntelliJ IDEA (Community is fine).

## 2. Configure MySQL
Open `src/main/resources/db.properties` and set your MySQL password:
```
db.user=root
db.password=YOUR_PASSWORD
```
You do NOT need to create anything manually: on first start the app creates the database
`smart_waste_management`, all tables and the sample data automatically.
(Optional: run `database/schema.sql` in MySQL Workbench yourself.)

## 3. Open and run in IntelliJ IDEA
1. Extract the zip.
2. IntelliJ: **File > Open** > select the `SmartWasteManagement` folder (the one containing `pom.xml`) > **Open as Project**. Choose "Trust Project".
3. Wait for Maven to download dependencies (progress bar at the bottom right).
4. If asked, set the Project SDK to JDK 17+ (File > Project Structure > Project).
5. Open `src/main/java/main/Main.java` and click the green run arrow next to `main`.
Alternative from a terminal: `mvn compile exec:java`

## 4. Demo logins
| Role  | Username | Password |
|-------|----------|----------|
| Admin | admin    | admin123 |
| Staff | staff    | staff123 |
Staff users cannot see Staff Management or delete records. New staff accounts can be registered on the login page.

## 5. Modules
- **Login/Registration** - role-based login (PBKDF2 hashed passwords), staff registration, session, logout.
- **Dashboard** - 8 stat cards, weekly bar chart, 6-month trend, category pie, status chart, recent activities.
- **Waste Segregation** - add/edit/delete waste, 4 categories with colour pills, mark as segregated.
- **Waste Collection** - create requests, location, category, quantity, assign staff, date, status update.
- **Staff Management** (admin) - add/edit/delete staff, assign pending requests to staff.
- **Collection Tracking** - status counts, assigned staff, completed-on history, mark completed.
- **Reports** - daily / weekly / monthly period, category statistics, staff performance (JFreeChart).
All tables support search, column sorting and filtering.

## 6. Packages
`main` (Main, SplashScreen) • `config` (DBConnection, DatabaseInitializer) • `model` • `dao` • `service` • `ui` • `utils`

## 7. Testing checklist
1. Start app: splash screen then login. Wrong password shows an error toast.
2. Login as admin: dashboard shows cards and charts with sample data.
3. Waste Segregation: add a Hazardous 5 kg record; check it appears red; Mark Segregated; delete asks for confirmation.
4. Waste Collection: create a request without staff (Pending); edit and choose staff (becomes Assigned); Update Status > Completed.
5. Staff: add staff with empty name (validation error), add valid one, assign a pending task.
6. Tracking: filter by Completed, see Completed On column.
7. Reports: switch Daily / Weekly / Monthly and watch charts change.
8. Dashboard: numbers and recent activity update. Logout, login as `staff` and confirm Staff menu is hidden.

## Troubleshooting
- "Access denied": wrong password in db.properties.
- "Communications link failure": MySQL is not running.
- To reset data: in MySQL run `DROP DATABASE smart_waste_management;` and start the app again.
