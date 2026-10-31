-- Smart Waste Segregation and Collection Management System
-- Run this whole script in MySQL Workbench, or let the application run it automatically on first start.
CREATE DATABASE IF NOT EXISTS smart_waste_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE smart_waste_management;

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(200) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role ENUM('ADMIN','STAFF') NOT NULL DEFAULT 'STAFF',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS staff (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    area VARCHAR(100),
    status ENUM('Active','Inactive') NOT NULL DEFAULT 'Active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS waste_categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(40) NOT NULL UNIQUE,
    description VARCHAR(150)
);

CREATE TABLE IF NOT EXISTS waste_records (
    id INT AUTO_INCREMENT PRIMARY KEY,
    category_id INT NOT NULL,
    quantity_kg DECIMAL(10,2) NOT NULL,
    record_date DATE NOT NULL,
    status ENUM('Pending','Segregated') NOT NULL DEFAULT 'Pending',
    notes VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_waste_category FOREIGN KEY (category_id) REFERENCES waste_categories(id)
);

CREATE TABLE IF NOT EXISTS collection_requests (
    id INT AUTO_INCREMENT PRIMARY KEY,
    location VARCHAR(150) NOT NULL,
    category_id INT NOT NULL,
    quantity_kg DECIMAL(10,2) NOT NULL,
    staff_id INT NULL,
    collection_date DATE NOT NULL,
    status ENUM('Pending','Assigned','Completed') NOT NULL DEFAULT 'Pending',
    completed_at DATETIME NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_req_category FOREIGN KEY (category_id) REFERENCES waste_categories(id),
    CONSTRAINT fk_req_staff FOREIGN KEY (staff_id) REFERENCES staff(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS activity_log (
    id INT AUTO_INCREMENT PRIMARY KEY,
    message VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ---------- SAMPLE DATA ----------
INSERT INTO waste_categories (name, description) VALUES
('Biodegradable','Food scraps, garden waste, organic matter'),
('Recyclable','Paper, plastic, glass, metal'),
('Non-Recyclable','Mixed waste that cannot be recycled'),
('Hazardous','Batteries, chemicals, medical and e-waste');

INSERT INTO users (username,password_hash,full_name,role) VALUES
('admin','HyYtNDtCSVBXXmVsc3qBiA==$5fgPaygAQefMz/FuQFzr516LwHLU4h9/iSGRiX5CE+M=','System Administrator','ADMIN'),
('staff','PkVMU1phaG92fYSLkpmgpw==$q9y7YPlDwQi31WRBFmtFUApWuKfoOfs/n2zvzJZPodg=','Ravi Collector','STAFF');

INSERT INTO staff (name,phone,email,area,status) VALUES
('Arun Kumar','9876501234','arun@smartwaste.com','Gandhi Nagar','Active'),
('Priya Sharma','9876502345','priya@smartwaste.com','Market Road','Active'),
('Karthik Raj','9876503456','karthik@smartwaste.com','Bus Stand Zone','Active'),
('Meena Devi','9876504567','meena@smartwaste.com','Hospital Area','Active'),
('Suresh Babu','9876505678','suresh@smartwaste.com','Industrial Estate','Active'),
('Divya Lakshmi','9876506789','divya@smartwaste.com','Lake View','Inactive');

INSERT INTO waste_records (category_id,quantity_kg,record_date,status,notes) VALUES
(3,41.85,DATE_SUB(CURDATE(),INTERVAL 0 DAY),'Pending','Thermocol'),
(1,12.89,DATE_SUB(CURDATE(),INTERVAL 0 DAY),'Segregated','Kitchen scraps'),
(2,9.95,DATE_SUB(CURDATE(),INTERVAL 0 DAY),'Pending','Paper and cardboard'),
(2,11.07,DATE_SUB(CURDATE(),INTERVAL 1 DAY),'Segregated','Paper and cardboard'),
(1,57.27,DATE_SUB(CURDATE(),INTERVAL 1 DAY),'Segregated','Market vegetable waste'),
(4,9.50,DATE_SUB(CURDATE(),INTERVAL 1 DAY),'Segregated','Paint containers'),
(1,36.95,DATE_SUB(CURDATE(),INTERVAL 2 DAY),'Segregated','Kitchen scraps'),
(4,5.76,DATE_SUB(CURDATE(),INTERVAL 2 DAY),'Segregated','E-waste'),
(3,36.12,DATE_SUB(CURDATE(),INTERVAL 2 DAY),'Segregated','Thermocol'),
(3,13.36,DATE_SUB(CURDATE(),INTERVAL 3 DAY),'Segregated','Thermocol'),
(4,10.31,DATE_SUB(CURDATE(),INTERVAL 3 DAY),'Segregated','Medical waste'),
(1,13.07,DATE_SUB(CURDATE(),INTERVAL 3 DAY),'Segregated','Market vegetable waste'),
(1,33.81,DATE_SUB(CURDATE(),INTERVAL 4 DAY),'Segregated','Restaurant leftovers'),
(3,48.42,DATE_SUB(CURDATE(),INTERVAL 4 DAY),'Segregated','Soiled packaging'),
(4,9.61,DATE_SUB(CURDATE(),INTERVAL 4 DAY),'Segregated','Paint containers'),
(3,49.31,DATE_SUB(CURDATE(),INTERVAL 5 DAY),'Segregated','Thermocol'),
(2,48.55,DATE_SUB(CURDATE(),INTERVAL 5 DAY),'Segregated','Paper and cardboard'),
(1,37.87,DATE_SUB(CURDATE(),INTERVAL 5 DAY),'Segregated','Restaurant leftovers'),
(3,22.97,DATE_SUB(CURDATE(),INTERVAL 6 DAY),'Segregated','Mixed street sweepings'),
(4,3.53,DATE_SUB(CURDATE(),INTERVAL 6 DAY),'Segregated','Paint containers'),
(2,16.58,DATE_SUB(CURDATE(),INTERVAL 6 DAY),'Segregated','Glass bottles'),
(2,10.04,DATE_SUB(CURDATE(),INTERVAL 7 DAY),'Segregated','Paper and cardboard'),
(4,11.94,DATE_SUB(CURDATE(),INTERVAL 7 DAY),'Segregated','Medical waste'),
(3,25.69,DATE_SUB(CURDATE(),INTERVAL 7 DAY),'Segregated','Soiled packaging'),
(4,14.75,DATE_SUB(CURDATE(),INTERVAL 9 DAY),'Segregated','Used batteries'),
(3,60.40,DATE_SUB(CURDATE(),INTERVAL 9 DAY),'Segregated','Soiled packaging'),
(4,12.63,DATE_SUB(CURDATE(),INTERVAL 12 DAY),'Segregated','Used batteries'),
(3,53.87,DATE_SUB(CURDATE(),INTERVAL 12 DAY),'Segregated','Soiled packaging'),
(4,13.47,DATE_SUB(CURDATE(),INTERVAL 15 DAY),'Segregated','Medical waste'),
(2,11.35,DATE_SUB(CURDATE(),INTERVAL 15 DAY),'Segregated','Metal cans'),
(3,46.66,DATE_SUB(CURDATE(),INTERVAL 18 DAY),'Segregated','Soiled packaging'),
(1,13.54,DATE_SUB(CURDATE(),INTERVAL 18 DAY),'Segregated','Garden clippings'),
(2,24.86,DATE_SUB(CURDATE(),INTERVAL 21 DAY),'Segregated','Metal cans'),
(3,65.01,DATE_SUB(CURDATE(),INTERVAL 21 DAY),'Segregated','Soiled packaging'),
(1,36.95,DATE_SUB(CURDATE(),INTERVAL 24 DAY),'Segregated','Garden clippings'),
(4,16.13,DATE_SUB(CURDATE(),INTERVAL 24 DAY),'Segregated','Paint containers'),
(3,34.92,DATE_SUB(CURDATE(),INTERVAL 27 DAY),'Segregated','Soiled packaging'),
(4,12.92,DATE_SUB(CURDATE(),INTERVAL 27 DAY),'Segregated','Paint containers'),
(2,14.98,DATE_SUB(CURDATE(),INTERVAL 30 DAY),'Segregated','Plastic bottles'),
(1,23.92,DATE_SUB(CURDATE(),INTERVAL 30 DAY),'Segregated','Kitchen scraps'),
(1,59.87,DATE_SUB(CURDATE(),INTERVAL 33 DAY),'Segregated','Kitchen scraps'),
(2,25.76,DATE_SUB(CURDATE(),INTERVAL 33 DAY),'Segregated','Paper and cardboard'),
(2,42.08,DATE_SUB(CURDATE(),INTERVAL 36 DAY),'Segregated','Glass bottles'),
(4,17.25,DATE_SUB(CURDATE(),INTERVAL 36 DAY),'Segregated','Used batteries'),
(4,14.77,DATE_SUB(CURDATE(),INTERVAL 39 DAY),'Segregated','Paint containers'),
(3,33.88,DATE_SUB(CURDATE(),INTERVAL 39 DAY),'Segregated','Soiled packaging'),
(1,48.06,DATE_SUB(CURDATE(),INTERVAL 42 DAY),'Segregated','Market vegetable waste'),
(2,21.44,DATE_SUB(CURDATE(),INTERVAL 42 DAY),'Segregated','Plastic bottles'),
(4,3.76,DATE_SUB(CURDATE(),INTERVAL 45 DAY),'Segregated','Used batteries'),
(1,16.14,DATE_SUB(CURDATE(),INTERVAL 45 DAY),'Segregated','Kitchen scraps'),
(1,46.82,DATE_SUB(CURDATE(),INTERVAL 48 DAY),'Segregated','Market vegetable waste'),
(2,62.46,DATE_SUB(CURDATE(),INTERVAL 48 DAY),'Segregated','Metal cans'),
(2,25.14,DATE_SUB(CURDATE(),INTERVAL 51 DAY),'Segregated','Glass bottles'),
(3,46.14,DATE_SUB(CURDATE(),INTERVAL 51 DAY),'Segregated','Soiled packaging'),
(1,60.94,DATE_SUB(CURDATE(),INTERVAL 54 DAY),'Segregated','Restaurant leftovers'),
(4,9.69,DATE_SUB(CURDATE(),INTERVAL 54 DAY),'Segregated','Medical waste'),
(1,16.13,DATE_SUB(CURDATE(),INTERVAL 57 DAY),'Segregated','Garden clippings'),
(4,13.85,DATE_SUB(CURDATE(),INTERVAL 57 DAY),'Segregated','Paint containers'),
(2,11.39,DATE_SUB(CURDATE(),INTERVAL 60 DAY),'Segregated','Glass bottles'),
(3,18.80,DATE_SUB(CURDATE(),INTERVAL 60 DAY),'Segregated','Thermocol'),
(1,27.89,DATE_SUB(CURDATE(),INTERVAL 66 DAY),'Segregated','Market vegetable waste'),
(3,51.77,DATE_SUB(CURDATE(),INTERVAL 66 DAY),'Segregated','Soiled packaging'),
(3,31.34,DATE_SUB(CURDATE(),INTERVAL 72 DAY),'Segregated','Mixed street sweepings'),
(1,41.96,DATE_SUB(CURDATE(),INTERVAL 72 DAY),'Segregated','Garden clippings'),
(2,58.69,DATE_SUB(CURDATE(),INTERVAL 78 DAY),'Segregated','Plastic bottles'),
(3,58.36,DATE_SUB(CURDATE(),INTERVAL 78 DAY),'Segregated','Soiled packaging'),
(2,41.06,DATE_SUB(CURDATE(),INTERVAL 84 DAY),'Segregated','Glass bottles'),
(1,53.86,DATE_SUB(CURDATE(),INTERVAL 84 DAY),'Segregated','Market vegetable waste'),
(3,25.55,DATE_SUB(CURDATE(),INTERVAL 90 DAY),'Segregated','Thermocol'),
(2,46.31,DATE_SUB(CURDATE(),INTERVAL 90 DAY),'Segregated','Glass bottles'),
(4,17.81,DATE_SUB(CURDATE(),INTERVAL 96 DAY),'Segregated','Medical waste'),
(3,14.83,DATE_SUB(CURDATE(),INTERVAL 96 DAY),'Segregated','Mixed street sweepings'),
(2,21.80,DATE_SUB(CURDATE(),INTERVAL 102 DAY),'Segregated','Plastic bottles'),
(4,9.72,DATE_SUB(CURDATE(),INTERVAL 102 DAY),'Segregated','Used batteries'),
(4,7.50,DATE_SUB(CURDATE(),INTERVAL 108 DAY),'Segregated','Used batteries'),
(3,60.08,DATE_SUB(CURDATE(),INTERVAL 108 DAY),'Segregated','Mixed street sweepings'),
(4,14.00,DATE_SUB(CURDATE(),INTERVAL 114 DAY),'Segregated','Paint containers'),
(3,63.34,DATE_SUB(CURDATE(),INTERVAL 114 DAY),'Segregated','Soiled packaging'),
(3,58.05,DATE_SUB(CURDATE(),INTERVAL 120 DAY),'Segregated','Thermocol'),
(1,33.75,DATE_SUB(CURDATE(),INTERVAL 120 DAY),'Segregated','Restaurant leftovers'),
(1,19.53,DATE_SUB(CURDATE(),INTERVAL 126 DAY),'Segregated','Kitchen scraps'),
(3,11.65,DATE_SUB(CURDATE(),INTERVAL 126 DAY),'Segregated','Thermocol'),
(4,4.34,DATE_SUB(CURDATE(),INTERVAL 132 DAY),'Segregated','Paint containers'),
(3,49.44,DATE_SUB(CURDATE(),INTERVAL 132 DAY),'Segregated','Soiled packaging'),
(2,42.90,DATE_SUB(CURDATE(),INTERVAL 138 DAY),'Segregated','Paper and cardboard'),
(3,10.85,DATE_SUB(CURDATE(),INTERVAL 138 DAY),'Segregated','Thermocol'),
(1,54.97,DATE_SUB(CURDATE(),INTERVAL 144 DAY),'Segregated','Kitchen scraps'),
(3,36.03,DATE_SUB(CURDATE(),INTERVAL 144 DAY),'Segregated','Mixed street sweepings'),
(2,25.11,DATE_SUB(CURDATE(),INTERVAL 150 DAY),'Segregated','Glass bottles'),
(1,40.07,DATE_SUB(CURDATE(),INTERVAL 150 DAY),'Segregated','Garden clippings'),
(3,35.14,DATE_SUB(CURDATE(),INTERVAL 156 DAY),'Segregated','Mixed street sweepings'),
(4,2.97,DATE_SUB(CURDATE(),INTERVAL 156 DAY),'Segregated','Medical waste'),
(4,11.33,DATE_SUB(CURDATE(),INTERVAL 162 DAY),'Segregated','Paint containers'),
(3,59.63,DATE_SUB(CURDATE(),INTERVAL 162 DAY),'Segregated','Thermocol'),
(2,19.11,DATE_SUB(CURDATE(),INTERVAL 168 DAY),'Segregated','Paper and cardboard'),
(3,62.37,DATE_SUB(CURDATE(),INTERVAL 168 DAY),'Segregated','Mixed street sweepings');

INSERT INTO collection_requests (location,category_id,quantity_kg,staff_id,collection_date,status,completed_at) VALUES
('Gandhi Nagar Market',1,96.48,NULL,DATE_ADD(CURDATE(),INTERVAL 1 DAY),'Pending',NULL),
('City Hospital Back Gate',2,33.10,NULL,DATE_ADD(CURDATE(),INTERVAL 2 DAY),'Pending',NULL),
('Central Bus Stand',4,80.01,NULL,DATE_ADD(CURDATE(),INTERVAL 3 DAY),'Pending',NULL),
('Lake View Apartments',1,73.43,NULL,DATE_ADD(CURDATE(),INTERVAL 4 DAY),'Pending',NULL),
('Industrial Estate Gate 2',3,86.64,NULL,DATE_ADD(CURDATE(),INTERVAL 5 DAY),'Pending',NULL),
('Railway Station Road',4,97.35,1,DATE_ADD(CURDATE(),INTERVAL 1 DAY),'Assigned',NULL),
('Town Hall Park',1,41.09,3,DATE_ADD(CURDATE(),INTERVAL 2 DAY),'Assigned',NULL),
('Green Valley School',1,96.09,5,DATE_ADD(CURDATE(),INTERVAL 3 DAY),'Assigned',NULL),
('Anna Salai Shopping Complex',4,73.98,1,DATE_SUB(CURDATE(),INTERVAL 2 DAY),'Completed',DATE_SUB(NOW(),INTERVAL 1 DAY)),
('Temple Street Colony',4,49.19,5,DATE_SUB(CURDATE(),INTERVAL 3 DAY),'Completed',DATE_SUB(NOW(),INTERVAL 2 DAY)),
('Riverside Community Hall',2,87.74,4,DATE_SUB(CURDATE(),INTERVAL 4 DAY),'Completed',DATE_SUB(NOW(),INTERVAL 3 DAY)),
('Old Market Lane',4,68.31,2,DATE_SUB(CURDATE(),INTERVAL 5 DAY),'Completed',DATE_SUB(NOW(),INTERVAL 4 DAY)),
('Tech Park Cafeteria',3,111.89,2,DATE_SUB(CURDATE(),INTERVAL 6 DAY),'Completed',DATE_SUB(NOW(),INTERVAL 5 DAY)),
('Bypass Road Junction',4,29.40,1,DATE_SUB(CURDATE(),INTERVAL 7 DAY),'Completed',DATE_SUB(NOW(),INTERVAL 6 DAY));

INSERT INTO activity_log (message,created_at) VALUES
('System initialised with sample data',DATE_SUB(NOW(),INTERVAL 40 HOUR)),
('Collection completed at Old Market Lane',DATE_SUB(NOW(),INTERVAL 35 HOUR)),
('Waste record added: Hazardous 7.5 kg',DATE_SUB(NOW(),INTERVAL 30 HOUR)),
('Staff Priya Sharma assigned to Temple Street Colony',DATE_SUB(NOW(),INTERVAL 25 HOUR)),
('New collection request: Railway Station Road',DATE_SUB(NOW(),INTERVAL 20 HOUR)),
('Waste segregated: Recyclable 32.0 kg',DATE_SUB(NOW(),INTERVAL 15 HOUR)),
('Staff Arun Kumar added',DATE_SUB(NOW(),INTERVAL 10 HOUR)),
('Collection completed at Riverside Community Hall',DATE_SUB(NOW(),INTERVAL 5 HOUR));
