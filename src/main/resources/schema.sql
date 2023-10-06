CREATE TABLE IF NOT EXISTS accounts (
  account_id VARCHAR(36) PRIMARY KEY,
  credential_type VARCHAR(15) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS oauth2_credentials (
  account_id VARCHAR(36) PRIMARY KEY,
  id_p VARCHAR(30) NOT NULL,
  subject VARCHAR(255) NOT NULL,
  UNIQUE(id_p, subject),
  FOREIGN KEY(account_id) REFERENCES accounts(account_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS profiles (
  account_id VARCHAR(36) PRIMARY KEY,
  username VARCHAR(30) NOT NULL,
  profile_image_url_endpoint VARCHAR(50),
  profile_image_url_path VARCHAR(100),
  FOREIGN KEY(account_id) REFERENCES accounts(account_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;


CREATE TABLE IF NOT EXISTS medicines (
  medicine_id VARCHAR(36) PRIMARY KEY,
  owner VARCHAR(36) NOT NULL,
  name VARCHAR(30) NOT NULL,
  taking_unit VARCHAR(10) NOT NULL,
  quantity DOUBLE NOT NULL,
  times_per_day INT NOT NULL,
  precautions VARCHAR(500) NOT NULL,
  registered_at TIMESTAMP NOT NULL,
  FOREIGN KEY(owner) REFERENCES accounts(account_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS timing_options (
  medicine_id VARCHAR(36),
  ordering INT,
  timing_option VARCHAR(30) NOT NULL,
  PRIMARY KEY(medicine_id, ordering),
  FOREIGN KEY(medicine_id) REFERENCES medicines(medicine_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS effects (
  medicine_id VARCHAR(36),
  ordering INT,
  effect VARCHAR(30) NOT NULL,
  PRIMARY KEY(medicine_id, ordering),
  FOREIGN KEY(medicine_id) REFERENCES medicines(medicine_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;