DROP TABLE IF EXISTS material;
DROP TABLE IF EXISTS step;
DROP TABLE IF EXISTS project_category;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS project;

CREATE TABLE project (
	project_id INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
	project_name VARCHAR(128) NOT NULL,
	estimated_hours DECIMAL(7,2),
	actual_hours DECIMAL(7,2),
	difficulty INT,
	notes TEXT
);

CREATE TABLE category (
	category_id INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
	category_name VARCHAR(128)
);

CREATE TABLE project_category (
	project_id INT NOT NULL,
	category_id INT NOT NULL,
	FOREIGN KEY (project_id) REFERENCES project (project_id) ON DELETE CASCADE,
	FOREIGN KEY (category_id) REFERENCES category (category_id) ON DELETE CASCADE,
	UNIQUE KEY (project_id, category_id)
);

CREATE TABLE step (
	step_id INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
	project_id INT NOT NULL,
	step_text TEXT NOT NULL,
	step_order INT NOT NULL,
	FOREIGN KEY(project_id) REFERENCES project (project_id)
		ON DELETE CASCADE
);

CREATE TABLE material (
	material_id INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
	project_id INT NOT NULL,
	material_name VARCHAR(128) NOT NULL,
	num_required INT,
	cost DECIMAL(7,2),
    FOREIGN KEY(project_id) REFERENCES project(project_id)
    	ON DELETE CASCADE
);

INSERT INTO project (project_id, project_name, estimated_hours, actual_hours, difficulty, notes) VALUES 
(1,'Hang a door', 10, 15, 3, 'Hang a new door'), (2,'Unclog a drain', 2, 1, 2, 'Unclog bath'), (3,'Pull weeds', 2, 1, 1, 'Pull weeds');

INSERT INTO category (category_id, category_name) VALUES (1,'Doors and Windows'), (2,'Plumbing'), (3,'Yardwork');

INSERT INTO material (project_id, material_name, num_required) VALUES (1, '2-inch screws', 20), (2, 'Drain Cleaner',1), (3, 'Gloves', 1);

INSERT INTO step (project_id, step_text, step_order) VALUES 
(1, 'Screw door hangers on the top, middle, and bottom of one side of the door frame', 1),
(1, 'Screw door hangers on top, middle, and bottom of door', 2),
(1, 'Use pin to join hangers on door and hangers on door frame', 3),
(2, 'Pour half of a bottle of drain cleaner in bathtub drain',1),
(2, 'Wait 30 minutes to see if drain is unclogged',2),
(2, 'If drain is not unclogged, pour remaining half of drain cleaner in bathtub drain and wait another 30 minutes',3),
(2, 'Call plumber if drain is still clogged',4),
(3, 'Put on gloves to protect hands', 1),
(3, 'Grab hold of weed as close to the ground as possible', 2),
(3, 'Pull weed out of ground, trying to pull the roots out as well', 3);

INSERT INTO project_category (project_id, category_id) VALUES (1, 1), (2, 2), (3, 3);
