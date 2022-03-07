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

