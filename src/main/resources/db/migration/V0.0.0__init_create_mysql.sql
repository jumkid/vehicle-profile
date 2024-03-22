CREATE TABLE IF NOT EXISTS vehicle_engine (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    type VARCHAR(100),
    cylinder SMALLINT,
    displacement FLOAT,
    fuel_type VARCHAR(100),
    horsepower SMALLINT,
    horsepower_rpm SMALLINT,
    torque SMALLINT,
    torque_rpm SMALLINT,
    manufacturer_engine_code VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS vehicle_transmission (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    availability VARCHAR(100),
    automatic_type VARCHAR(100),
    type VARCHAR(100),
    drivetrain VARCHAR(100),
    number_of_speeds SMALLINT
);

CREATE TABLE IF NOT EXISTS vehicle_pricing (
     id INTEGER AUTO_INCREMENT PRIMARY KEY,
     msrp FLOAT
);

CREATE TABLE IF NOT EXISTS vehicle_master (
    vehicle_id VARCHAR(100) PRIMARY KEY,
    name VARCHAR(255),
    make VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    model_year INTEGER,
    access_scope VARCHAR(32),
    trim_level VARCHAR(32),
    vehicle_engine_id INTEGER,
    vehicle_transmission_id INTEGER,
    vehicle_pricing_id INTEGER,
    media_gallery_id VARCHAR(100),
    category VARCHAR(64),

    created_by VARCHAR(255),
    created_on DATETIME(3),
    modified_by VARCHAR(255),
    modified_on DATETIME(3),
    FOREIGN KEY (vehicle_engine_id) REFERENCES vehicle_engine(id) ON DELETE CASCADE,
    FOREIGN KEY (vehicle_transmission_id) REFERENCES vehicle_transmission(id) ON DELETE CASCADE,
    FOREIGN KEY (vehicle_pricing_id) REFERENCES vehicle_pricing(id) ON DELETE CASCADE
);
