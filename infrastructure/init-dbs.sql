-- Create databases for each service
CREATE DATABASE registration_db;
CREATE DATABASE academic_db;
CREATE DATABASE notification_db;

-- We could also create specific users for each database here if we wanted stricter isolation,
-- but for simplicity in this project, we'll use the 'sump_admin' superuser for all.
