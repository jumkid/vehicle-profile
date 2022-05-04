CREATE TABLE vehicle_master (
    vehicle_id VARCHAR(100) PRIMARY KEY,
    name VARCHAR(255),
    make VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    model_year INTEGER,

    created_by VARCHAR(255),
    creation_date DATETIME,
    modified_by VARCHAR(255),
    modification_date DATETIME
);


CREATE TABLE vehicle_engine (
    vehicle_engine_id INTEGER AUTO_INCREMENT PRIMARY KEY,
    vehicle_id VARCHAR(100),
    name VARCHAR(255) NOT NULL,
    type VARCHAR(100),
    cylinder SMALLINT,
    fuel_type VARCHAR(100),
    horsepower SMALLINT,
    torque SMALLINT,
    manufacturer_engine_code VARCHAR(100),
    FOREIGN KEY (vehicle_id) REFERENCES vehicle_master(vehicle_id) ON DELETE CASCADE
)
