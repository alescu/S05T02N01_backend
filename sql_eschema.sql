DROP DATABASE IF EXISTS pet_academy_db
CREATE DATABASE pet_academy_db

CREATE TABLE users
(
    id serial primary key,
    email VARCHAR(40) not null unique,
    user_name  VARCHAR(40) not null unique,
    full_name  VARCHAR(40),
    city  VARCHAR(40),
    country  VARCHAR(40),
    rol  VARCHAR(40),
    password VARCHAR(400),
    failed_login_attempts smallint,
    created_at timestamp,
    updated_at timestamp,
    password_pending_renewal_at timestamp,
    password_updated_at timestamp,
    account_blocked_at timestamp,
    account_locked_at timestamp
);

-- Function to update creation, modification, and password modification timestamps
CREATE OR REPLACE FUNCTION update_timestamps_and_password_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    -- Set created_at only on initial insertion if it's null
    IF TG_OP = 'INSERT' THEN
        NEW.created_at = NOW();
    END IF;

    -- Always update updated_at on insert or update
    NEW.updated_at = NOW();

    -- Update password_updated_at ONLY if the password field has changed during an UPDATE
    IF TG_OP = 'UPDATE' AND OLD.password IS DISTINCT FROM NEW.password THEN
        NEW.password_updated_at = NOW();
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


-- Trigger per a la taula users
CREATE TRIGGER update_timestamps_and_password_timestamp
BEFORE INSERT OR UPDATE ON users
FOR EACH ROW
EXECUTE FUNCTION update_timestamps_and_password_timestamp();