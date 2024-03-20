TRUNCATE TABLE flyway_schema_history;

SET FOREIGN_KEY_CHECKS = 0;

ALTER TABLE vehicle_master AUTO_INCREMENT = 1;
TRUNCATE TABLE vehicle_master;
ALTER TABLE vehicle_engine AUTO_INCREMENT = 1;
TRUNCATE TABLE vehicle_engine;
ALTER TABLE vehicle_transmission AUTO_INCREMENT = 1;
TRUNCATE TABLE vehicle_transmission;
ALTER TABLE vehicle_pricing AUTO_INCREMENT = 1;
TRUNCATE TABLE vehicle_pricing;

SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO vehicle_engine (name, type, cylinder, displacement, fuel_type, horsepower, horsepower_rpm, torque, torque_rpm)
VALUES ('2.0L I-4', 'Gas Inline 4', 4, 2, 'gasoline', 261, 5000, 295, 1800);

INSERT INTO vehicle_transmission (name, availability, automatic_type, type, drivetrain, number_of_speeds)
VALUES ('7 speed PDK', '', 'AMT', 'Auto-Shift Manual w/OD', 'AWD', 7);

INSERT INTO vehicle_pricing (msrp) VALUES (86000);

INSERT INTO vehicle_master (vehicle_id, name, make, model, model_year, access_scope, trim_level, vehicle_engine_id, vehicle_transmission_id, vehicle_pricing_id, category)
VALUES ('abc-123', 'system-stock', 'porsche', 'macan', 2015, 'PUBLIC', 'S', 1, 1, 1, 'suv');

COMMIT;