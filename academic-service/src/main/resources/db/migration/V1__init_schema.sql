-- V1__init_schema.sql

CREATE TABLE courses (
    id UUID PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    code VARCHAR(20) UNIQUE NOT NULL,
    capacity INT NOT NULL,
    current_enrollment INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE enrollments (
    id UUID PRIMARY KEY,
    student_id UUID NOT NULL,
    course_id UUID NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    enrolled_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (student_id, course_id)
);

CREATE INDEX idx_enrollments_student_id ON enrollments(student_id);
CREATE INDEX idx_courses_code ON courses(code);

-- Insert some dummy courses
INSERT INTO courses (id, title, code, capacity) VALUES 
(gen_random_uuid(), 'Introduction to Computer Science', 'CS101', 50),
(gen_random_uuid(), 'Data Structures and Algorithms', 'CS201', 30),
(gen_random_uuid(), 'Database Systems', 'CS301', 40);
