CREATE TABLE IF NOT EXISTS medicines (
  medicine_id VARCHAR(36) NOT NULL PRIMARY KEY,
  account_id VARCHAR(36),
  shared_group_id VARCHAR(36),
  medicine_name VARCHAR(30) NOT NULL,
  quantity DOUBLE NOT NULL,
  dose_unit VARCHAR(10) NOT NULL,
  times_per_day INT NOT NULL,
  precautions VARCHAR(500) NOT NULL,
  medicine_image_url_endpoint VARCHAR(50),
  medicine_image_url_path VARCHAR(100),
  is_public BOOLEAN NOT NULL,
  registered_at DATETIME NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS inventories (
  medicine_id VARCHAR(36) NOT NULL PRIMARY KEY,
  remaining_quantity DOUBLE NOT NULL,
  quantity_per_package DOUBLE NOT NULL,
  started_on DATE,
  expiration_on DATE,
  unused_package INT,
  FOREIGN KEY(medicine_id) REFERENCES medicines(medicine_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS timing_options (
  medicine_id VARCHAR(36) NOT NULL,
  ordering INT NOT NULL,
  timing_option VARCHAR(30) NOT NULL,
  PRIMARY KEY(medicine_id, ordering),
  FOREIGN KEY(medicine_id) REFERENCES medicines(medicine_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS effects (
  medicine_id VARCHAR(36) NOT NULL,
  ordering INT NOT NULL,
  effect VARCHAR(30) NOT NULL,
  PRIMARY KEY(medicine_id, ordering),
  FOREIGN KEY(medicine_id) REFERENCES medicines(medicine_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;