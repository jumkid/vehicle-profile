CREATE TABLE vehicle_engine (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    type VARCHAR(100),
    cylinder SMALLINT,
    fuel_type VARCHAR(100),
    horsepower SMALLINT,
    torque SMALLINT,
    manufacturer_engine_code VARCHAR(100)
);

CREATE TABLE vehicle_transmission (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    availability VARCHAR(100),
    automatic_type VARCHAR(100),
    type VARCHAR(100),
    number_of_speeds SMALLINT
);

CREATE TABLE vehicle_master (
    vehicle_id VARCHAR(100) PRIMARY KEY,
    name VARCHAR(255),
    make VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    model_year INTEGER,
    access_scope VARCHAR(32),
    trim_level VARCHAR(32),
    vehicle_engine_id INTEGER,
    vehicle_transmission_id INTEGER,

    created_by VARCHAR(255),
    creation_date DATETIME,
    modified_by VARCHAR(255),
    modification_date DATETIME,
    FOREIGN KEY (vehicle_engine_id) REFERENCES vehicle_engine(id) ON DELETE CASCADE
)
