-- =====================================================
-- 1. Create the car rental database (safe creation)
-- =====================================================
CREATE DATABASE IF NOT EXISTS DBCarRentals;
USE DBCarRentals;

-- =====================================================
-- 2. Drop existing tables safely (optional cleanup)
-- =====================================================
-- Disable FK checks for clean re-creation
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS 
    return_details,
    violation_details,
    cancellation_details,
    rental_details,
    car_record,
    renter_record,
    staff_record,
    branch_record,
    location_record,
    job_record,
    department_record;

-- Re-enable FK checks
SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================
-- 3. Create main tables safely
-- =====================================================

CREATE TABLE IF NOT EXISTS department_record (
    department_id VARCHAR(10) NOT NULL,
    department_name VARCHAR(50) UNIQUE NOT NULL,
    PRIMARY KEY (department_id)
);

CREATE TABLE IF NOT EXISTS job_record (
    job_id VARCHAR(20) NOT NULL,
    job_title VARCHAR(100) UNIQUE NOT NULL,
    job_department_id VARCHAR(10) NOT NULL,
    job_salary DECIMAL(10, 2) NOT NULL,
    PRIMARY KEY (job_id),
    FOREIGN KEY (job_department_id) REFERENCES department_record(department_id)
);

CREATE TABLE IF NOT EXISTS location_record (
    location_id VARCHAR(10) NOT NULL,
    location_city VARCHAR(50) NOT NULL,
    location_province VARCHAR(50) NOT NULL,
    PRIMARY KEY (location_id),
    UNIQUE (location_city, location_province)
);

CREATE TABLE IF NOT EXISTS branch_record (
    branch_id VARCHAR(6) NOT NULL,
    branch_name VARCHAR(50) UNIQUE NOT NULL,
    branch_email_address VARCHAR(100) UNIQUE NOT NULL,
    branch_location_id VARCHAR(10) NOT NULL,
    PRIMARY KEY (branch_id),
    FOREIGN KEY (branch_location_id) REFERENCES location_record(location_id)
);

CREATE TABLE IF NOT EXISTS staff_record (
    staff_id VARCHAR(6) NOT NULL,
    staff_first_name VARCHAR(50) NOT NULL,
    staff_last_name VARCHAR(50) NOT NULL,
    staff_job_id VARCHAR(20) NOT NULL,
    staff_branch_id VARCHAR(6) NOT NULL,
    PRIMARY KEY (staff_id),
    FOREIGN KEY (staff_job_id) REFERENCES job_record(job_id),
    FOREIGN KEY (staff_branch_id) REFERENCES branch_record(branch_id)
);

CREATE TABLE IF NOT EXISTS renter_record (
    renter_dl_number VARCHAR(20) NOT NULL,
    renter_first_name VARCHAR(50) NOT NULL,
    renter_last_name VARCHAR(50) NOT NULL,
    renter_phone_number VARCHAR(11) NOT NULL,
    renter_email_address VARCHAR(100) NOT NULL,
    PRIMARY KEY (renter_dl_number)
);

CREATE TABLE IF NOT EXISTS car_record (
    car_plate_number VARCHAR(7) NOT NULL,
    car_transmission ENUM('Manual', 'Automatic') NOT NULL,
    car_model VARCHAR(50) NOT NULL,
    car_brand VARCHAR(50) NOT NULL,
    car_year_manufactured YEAR NOT NULL,
    car_mileage INT NOT NULL,
    car_seat_number INT NOT NULL,
    car_status ENUM('Available', 'Rented', 'Under Maintenance') NOT NULL DEFAULT 'Available',
    car_branch_id VARCHAR(6) NOT NULL,
    PRIMARY KEY (car_plate_number),
    FOREIGN KEY (car_branch_id) REFERENCES branch_record(branch_id)
);

-- =====================================================
-- 4. Transactional tables (with IF NOT EXISTS)
-- =====================================================

CREATE TABLE IF NOT EXISTS rental_details (
    rental_id VARCHAR(10) NOT NULL,
    rental_renter_dl_number VARCHAR(20) NOT NULL,
    rental_car_plate_number VARCHAR(7) NOT NULL,
    rental_branch_id VARCHAR(6) NOT NULL,
    rental_staff_id_pickup VARCHAR(6),
    rental_staff_id_return VARCHAR(6),
    rental_datetime DATETIME NOT NULL,

    rental_expected_pickup_datetime DATETIME NOT NULL,
    rental_actual_pickup_datetime DATETIME,

    rental_expected_return_datetime DATETIME NOT NULL,
    rental_actual_return_datetime DATETIME,

    rental_total_payment DECIMAL(10, 2) NOT NULL,

    rental_status ENUM('UPCOMING', 'ACTIVE', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'UPCOMING',

    PRIMARY KEY (rental_id),

    FOREIGN KEY (rental_renter_dl_number) REFERENCES renter_record(renter_dl_number),
    FOREIGN KEY (rental_car_plate_number) REFERENCES car_record(car_plate_number),
    FOREIGN KEY (rental_branch_id) REFERENCES branch_record(branch_id),
    FOREIGN KEY (rental_staff_id_pickup) REFERENCES staff_record(staff_id),
    FOREIGN KEY (rental_staff_id_return) REFERENCES staff_record(staff_id)
);


CREATE TABLE IF NOT EXISTS cancellation_details (
    cancellation_id VARCHAR(10) NOT NULL,
    cancellation_rental_id VARCHAR(10) UNIQUE NOT NULL,
    cancellation_staff_id VARCHAR(6) NOT NULL,
    cancellation_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cancellation_reason VARCHAR(150) NOT NULL,
    PRIMARY KEY (cancellation_id),
    FOREIGN KEY (cancellation_rental_id) REFERENCES rental_details(rental_id),
    FOREIGN KEY (cancellation_staff_id) REFERENCES staff_record(staff_id)
);   

CREATE TABLE IF NOT EXISTS violation_details (
    violation_id VARCHAR(10) NOT NULL,
    violation_rental_id VARCHAR(10) NOT NULL,
    violation_staff_id VARCHAR(6) NOT NULL,
    violation_type ENUM('Late Return', 'Car Damage', 'Traffic Violation', 'Cleaning Fee', 'Other') NOT NULL,
    violation_penalty_fee DECIMAL(10, 2) NOT NULL,
    violation_reason VARCHAR(255) NOT NULL,
    violation_duration_hours INT NOT NULL DEFAULT 0,
    violation_timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (violation_id),
    FOREIGN KEY (violation_rental_id) REFERENCES rental_details(rental_id),
    FOREIGN KEY (violation_staff_id) REFERENCES staff_record(staff_id)
);

CREATE TABLE IF NOT EXISTS return_details (
    return_id VARCHAR(10) NOT NULL,
    return_rental_id VARCHAR(10) UNIQUE NOT NULL,
    return_staff_id VARCHAR(6) NOT NULL,
    PRIMARY KEY (return_id),
    FOREIGN KEY (return_rental_id) REFERENCES rental_details(rental_id),
    FOREIGN KEY (return_staff_id) REFERENCES staff_record(staff_id)
);

-- =====================================================
-- 5. Default data insertion (safe inserts)
-- =====================================================
-- Use INSERT IGNORE so reruns don’t cause duplicate errors

INSERT IGNORE INTO department_record (department_id, department_name) VALUES
('DEPT_ADM', 'Administration'),
('DEPT_FIN', 'Finance'),
('DEPT_HR', 'Human Resources'),
('DEPT_MKT', 'Marketing'),
('DEPT_SLS', 'Sales'),
('DEPT_IT', 'Information Technology'),
('DEPT_OPS', 'Operations'),
('DEPT_CST', 'Customer Service'),
('DEPT_MTN', 'Maintenance'),
('DEPT_LGL', 'Legal Affairs');

-- (Repeat for other insert sections)
# Job Record
INSERT IGNORE INTO job_record (job_id, job_title, job_department_id, job_salary) VALUES
-- Administration Department
('ADM001', 'President', 'DEPT_ADM', 150000.00),
('ADM002', 'Executive Assistant', 'DEPT_ADM', 60000.00),
('ADM003', 'Administrative Staff', 'DEPT_ADM', 40000.00),

-- Finance Department
('FIN001', 'Finance Manager', 'DEPT_FIN', 90000.00),
('FIN002', 'Accountant', 'DEPT_FIN', 60000.00),
('FIN003', 'Finance Assistant', 'DEPT_FIN', 45000.00),

-- Human Resources Department
('HR001', 'Human Resources Manager', 'DEPT_HR', 85000.00),
('HR002', 'Human Resources Officer', 'DEPT_HR', 55000.00),
('HR003', 'Human Resources Staff', 'DEPT_HR', 42000.00),

-- Marketing Department
('MKT001', 'Marketing Manager', 'DEPT_MKT', 85000.00),
('MKT002', 'Marketing Specialist', 'DEPT_MKT', 50000.00),
('MKT003', 'Social Media Coordinator', 'DEPT_MKT', 42000.00),

-- Sales Department
('SLS001', 'Sales Manager', 'DEPT_SLS', 88000.00),
('SLS002', 'Sales Executive', 'DEPT_SLS', 55000.00),
('SLS003', 'Sales Associate', 'DEPT_SLS', 42000.00),

-- Information Technology Department
('IT001', 'IT Manager', 'DEPT_IT', 95000.00),
('IT002', 'Systems Administrator', 'DEPT_IT', 70000.00),
('IT003', 'Technical Support Staff', 'DEPT_IT', 50000.00),

-- Operations Department
('OPS001', 'Operations Manager', 'DEPT_OPS', 90000.00),
('OPS002', 'Branch Manager', 'DEPT_OPS', 75000.00),
('OPS003', 'Rental Staff', 'DEPT_OPS', 45000.00),

-- Customer Service Department
('CST001', 'Customer Service Manager', 'DEPT_CST', 55000.00),
('CST002', 'Customer Service Representative', 'DEPT_CST', 42000.00),
('CST003', 'Front Desk Clerk', 'DEPT_CST', 40000.00),

-- Maintenance Department
('MTN001', 'Maintenance Manager', 'DEPT_MTN', 75000.00),
('MTN002', 'Mechanic', 'DEPT_MTN', 48000.00),
('MTN003', 'Maintenance Crew', 'DEPT_MTN', 40000.00),

-- Legal Affairs Department
('LGL001', 'Legal Affairs Manager', 'DEPT_LGL', 95000.00),
('LGL002', 'Legal Officer', 'DEPT_LGL', 70000.00),
('LGL003', 'Paralegal Assistant', 'DEPT_LGL', 50000.00);

# Location Record
INSERT IGNORE INTO location_record (location_id, location_city, location_province)
VALUES
-- Metro Manila (multiple key cities)
('MNL001', 'Manila', 'Metro Manila'),
('MNL002', 'Makati', 'Metro Manila'),
('MNL003', 'Quezon City', 'Metro Manila'),

-- Cebu (multiple key cities)
('CEB001', 'Cebu City', 'Cebu'),
('CEB002', 'Mandaue', 'Cebu'),
('CEB003', 'Lapu-Lapu City', 'Cebu'),

-- Other top provinces
('DAV001', 'Davao City', 'Davao del Sur'),
('ILO001', 'Iloilo City', 'Iloilo'),
('BEN001', 'Baguio City', 'Benguet'),
('PAL001', 'Puerto Princesa', 'Palawan'),
('CAV001', 'Tagaytay', 'Cavite'),
('ILN001', 'Laoag City', 'Ilocos Norte'),
('LEY001', 'Tacloban City', 'Leyte'),
('NEG001', 'Bacolod City', 'Negros Occidental');

# Branch Record
INSERT IGNORE INTO branch_record (branch_id, branch_name, branch_email_address, branch_location_id) VALUES
-- Metro Manila
('BRN001', 'Forza Rentals Manila', 'manila@forzarentals.ph', 'MNL001'),
('BRN002', 'Forza Rentals Makati', 'makati@forzarentals.ph', 'MNL002'),
('BRN003', 'Forza Rentals Quezon City', 'quezoncity@forzarentals.ph', 'MNL003'),

-- Cebu
('BRN004', 'Forza Rentals Cebu City', 'cebu@forzarentals.ph', 'CEB001'),
('BRN005', 'Forza Rentals Mandaue', 'mandaue@forzarentals.ph', 'CEB002'),
('BRN006', 'Forza Rentals Lapu-Lapu City', 'lapulapu@forzarentals.ph', 'CEB003'),

-- Other Provinces
('BRN007', 'Forza Rentals Davao City', 'davao@forzarentals.ph', 'DAV001'),
('BRN008', 'Forza Rentals Iloilo City', 'iloilo@forzarentals.ph', 'ILO001'),
('BRN009', 'Forza Rentals Baguio City', 'baguio@forzarentals.ph', 'BEN001'),
('BRN010', 'Forza Rentals Puerto Princesa', 'puertoprincesa@forzarentals.ph', 'PAL001'),
('BRN011', 'Forza Rentals Tagaytay', 'tagaytay@forzarentals.ph', 'CAV001'),
('BRN012', 'Forza Rentals Laoag City', 'laoag@forzarentals.ph', 'ILN001'),
('BRN013', 'Forza Rentals Tacloban City', 'tacloban@forzarentals.ph', 'LEY001'),
('BRN014', 'Forza Rentals Bacolod City', 'bacolod@forzarentals.ph', 'NEG001');

# Staff Record
INSERT IGNORE INTO staff_record (staff_id, staff_first_name, staff_last_name, staff_job_id, staff_branch_id)
VALUES
-- Headquarters / Administration (Metro Manila)
('STF001', 'Isabella', 'Reyes', 'ADM001', 'BRN001'), -- President
('STF002', 'Marcus', 'Tan', 'ADM002', 'BRN001'), -- Executive Assistant

-- Metro Manila Branches
('STF003', 'Lara', 'Santos', 'ADM003', 'BRN002'), -- Administrative Staff (or branch manager if needed)
('STF004', 'John', 'Del Rosario', 'HR002', 'BRN002'), -- Human Resources Officer (as Supervisor)
('STF005', 'Ella', 'Chua', 'CST002', 'BRN003'), -- Customer Service Representative
('STF006', 'Kyle', 'Ramos', 'CST003', 'BRN003'), -- Front Desk Clerk

-- Cebu Branches
('STF007', 'Patrick', 'Lim', 'OPS002', 'BRN004'), -- Branch Manager
('STF008', 'Rina', 'Fernandez', 'MTN002', 'BRN004'), -- Mechanic
('STF009', 'Ella', 'Chua', 'CST002', 'BRN005'), -- Customer Service Representative (duplicate name)
('STF010', 'Andrea', 'Torres', 'CST003', 'BRN006'), -- Front Desk Clerk

-- Provincial Branches
('STF011', 'John', 'Del Rosario', 'HR002', 'BRN007'), -- Human Resources Officer (duplicate name)
('STF012', 'Nina', 'Lopez', 'CST002', 'BRN008'), -- Customer Service Representative
('STF013', 'Patrick', 'Lim', 'MTN002', 'BRN009'), -- Mechanic (duplicate name)
('STF014', 'Carla', 'Gomez', 'CST003', 'BRN010'), -- Front Desk Clerk
('STF015', 'Mia', 'Villanueva', 'CST002', 'BRN011'); -- Customer Service Representative

INSERT IGNORE INTO staff_record (staff_id, staff_first_name, staff_last_name, staff_job_id, staff_branch_id)
VALUES
('STF016', 'Olivia', 'Cruz', 'OPS001', 'BRN001'),
('STF017', 'Oscar', 'Mendoza', 'OPS002', 'BRN002'),
('STF018', 'Owen', 'Santos', 'OPS003', 'BRN003'),
('STF019', 'Olga', 'Tan', 'OPS001', 'BRN004'),
('STF020', 'Omar', 'Reyes', 'OPS002', 'BRN005'),
('STF021', 'Opal', 'Lim', 'OPS003', 'BRN006'),
('STF022', 'Oriel', 'Gomez', 'OPS001', 'BRN007'),
('STF023', 'Oda', 'Chua', 'OPS002', 'BRN008'),
('STF024', 'Otto', 'Villanueva', 'OPS003', 'BRN009'),
('STF025', 'Oona', 'Lopez', 'OPS001', 'BRN010'),
('STF026', 'Owen', 'Dela Cruz', 'OPS002', 'BRN011'),
('STF027', 'Odessa', 'Garcia', 'OPS003', 'BRN012'),
('STF028', 'Osiris', 'Luna', 'OPS001', 'BRN013'),
('STF029', 'Onyx', 'Martinez', 'OPS002', 'BRN014');

# Renter Record    
INSERT IGNORE INTO renter_record (renter_dl_number, renter_first_name, renter_last_name, renter_phone_number, renter_email_address)
VALUES
('MC1234567890', 'Angela', 'Cruz', '09171234567', 'angela.cruz@email.com'),
('LL0000000001', 'Martin', 'Santos', '09281234567', 'martin.santos@email.com'),
('MC9876543210', 'Bianca', 'Torres', '09351234567', 'bianca.torres@email.com'),
('LL1122334455', 'Carlos', 'Reyes', '09451234567', 'carlos.reyes@email.com'),
('MC1029384756', 'Denise', 'Lopez', '09561234567', 'denise.lopez@email.com'),
('LL2233445566', 'Francis', 'Lim', '09671234567', 'francis.lim@email.com'),
('MC5647382910', 'Julia', 'Tan', '09781234567', 'julia.tan@email.com'),
('LL3344556677', 'Nathan', 'Gomez', '09891234567', 'nathan.gomez@email.com'),
('MC1827364556', 'Patricia', 'Villanueva', '09901234567', 'patricia.villanueva@email.com'),
('LL4455667788', 'Rafael', 'Chua', '09183456789', 'rafael.chua@email.com');

# Car Record
INSERT IGNORE INTO car_record (car_plate_number, car_transmission, car_model, car_brand, car_year_manufactured, car_mileage, car_seat_number, car_status, car_branch_id)
VALUES
-- Manila Branch
('ABC1234', 'Automatic', 'Vios', 'Toyota', 2022, 18000, 5, 'Available', 'BRN001'),
('DEF5678', 'Manual', 'Accent', 'Hyundai', 2021, 25000, 5, 'Rented', 'BRN001'),

-- Makati Branch
('GHI9012', 'Automatic', 'Civic', 'Honda', 2023, 12000, 5, 'Available', 'BRN002'),
('JKL3456', 'Automatic', 'Almera', 'Nissan', 2022, 20000, 5, 'Under Maintenance', 'BRN002'),

-- Quezon City Branch
('MNO7890', 'Manual', 'Mirage', 'Mitsubishi', 2020, 35000, 5, 'Available', 'BRN003'),

-- Cebu City Branch
('PQR2345', 'Automatic', 'Fortuner', 'Toyota', 2023, 8000, 7, 'Rented', 'BRN004'),
('STU6789', 'Manual', 'Wigo', 'Toyota', 2021, 22000, 5, 'Available', 'BRN004'),

-- Mandaue Branch
('VWX1122', 'Automatic', 'City', 'Honda', 2022, 15000, 5, 'Available', 'BRN005'),

-- Lapu-Lapu City Branch
('YZA3344', 'Automatic', 'CR-V', 'Honda', 2023, 6000, 7, 'Rented', 'BRN006'),

-- Davao City Branch
('BCD5566', 'Manual', 'Ranger', 'Ford', 2021, 28000, 5, 'Available', 'BRN007'),

-- Iloilo City Branch
('EFG7788', 'Automatic', 'Corolla Cross', 'Toyota', 2023, 9000, 5, 'Available', 'BRN008'),

-- Baguio City Branch
('HIJ9900', 'Manual', 'Jimny', 'Suzuki', 2020, 40000, 4, 'Under Maintenance', 'BRN009'),

-- Puerto Princesa Branch
('KLM2233', 'Automatic', 'Everest', 'Ford', 2022, 17000, 7, 'Available', 'BRN010'),

-- Tagaytay Branch
('NOP4455', 'Automatic', 'Terra', 'Nissan', 2023, 7000, 7, 'Available', 'BRN011');
INSERT IGNORE INTO rental_details (
    rental_id,
    rental_renter_dl_number,
    rental_car_plate_number,
    rental_branch_id,
    rental_staff_id_pickup,
    rental_staff_id_return,
    rental_datetime,
    rental_expected_pickup_datetime,
    rental_actual_pickup_datetime,
    rental_expected_return_datetime,
    rental_actual_return_datetime,
    rental_total_payment,
    rental_status
)
VALUES
-- Completed rental (Manila branch by STF016)
('RNT001', 'MC1234567890', 'ABC1234', 'BRN001', 'STF016', 'STF016',
 '2025-11-01 09:00:00', '2025-11-01 09:10:00', '2025-11-01 09:15:00',
 '2025-11-05 09:00:00', '2025-11-05 08:50:00',
 15000.00, 'COMPLETED'),

-- Active rental (Manila branch by STF016)
('RNT002', 'LL0000000001', 'DEF5678', 'BRN001', 'STF016', NULL,
 '2025-11-06 10:00:00', '2025-11-06 10:10:00', '2025-11-06 10:20:00',
 '2025-11-10 10:00:00', NULL,
 18000.00, 'ACTIVE'),

-- Upcoming rental (Makati branch by STF017)
('RNT003', 'MC9876543210', 'GHI9012', 'BRN002', 'STF017', NULL,
 '2025-11-07 08:00:00', '2025-11-10 08:00:00', NULL,
 '2025-11-15 08:00:00', NULL,
 20000.00, 'UPCOMING'),

-- Completed rental (Makati branch by STF017)
('RNT004', 'LL1122334455', 'JKL3456', 'BRN002', 'STF017', 'STF017',
 '2025-10-25 14:00:00', '2025-10-25 14:10:00', '2025-10-25 14:15:00',
 '2025-10-30 14:00:00', '2025-10-30 13:50:00',
 22000.00, 'COMPLETED'),

-- Upcoming rental (Quezon City branch by STF018)
('RNT005', 'MC1029384756', 'MNO7890', 'BRN003', 'STF018', NULL,
 '2025-11-07 11:00:00', '2025-11-09 11:00:00', NULL,
 '2025-11-14 11:00:00', NULL,
 17000.00, 'UPCOMING'),

-- Active rental (Cebu City branch by STF019)
('RNT006', 'LL2233445566', 'PQR2345', 'BRN004', 'STF019', NULL,
 '2025-11-05 09:30:00', '2025-11-05 09:40:00', '2025-11-05 09:45:00',
 '2025-11-12 09:30:00', NULL,
 25000.00, 'ACTIVE');

-- Mark cars not currently rented as 'Available', but keep 'Under Maintenance' unchanged
SET SQL_SAFE_UPDATES = 0;
UPDATE car_record
SET car_status = 'Available'
WHERE car_plate_number NOT IN (
    SELECT rental_car_plate_number
    FROM rental_details
    WHERE rental_status = 'ACTIVE'
)
AND car_status != 'Under Maintenance';
SET SQL_SAFE_UPDATES = 1;