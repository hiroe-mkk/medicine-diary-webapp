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
  medicine_name VARCHAR(30) NOT NULL,
  quantity DOUBLE NOT NULL,
  taking_unit VARCHAR(10) NOT NULL,
  times_per_day INT NOT NULL,
  precautions VARCHAR(500) NOT NULL,
  medicine_image_url_endpoint VARCHAR(50),
  medicine_image_url_path VARCHAR(100),
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


CREATE TABLE IF NOT EXISTS taking_records (
  taking_record_id VARCHAR(36) PRIMARY KEY,
  recorder VARCHAR(36),
  taken_medicine VARCHAR(36),
  quantity DOUBLE NOT NULL,
  symptom VARCHAR(30) NOT NULL,
  before_taking VARCHAR(15) NOT NULL,
  after_taking VARCHAR(15),
  note VARCHAR(500) NOT NULL,
  taken_at TIMESTAMP NOT NULL,
  FOREIGN KEY(recorder) REFERENCES accounts(account_id),
  FOREIGN KEY(taken_medicine) REFERENCES medicines(medicine_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;


CREATE TABLE IF NOT EXISTS shared_groups (
  shared_group_id VARCHAR(36) PRIMARY KEY
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS members (
  shared_group_id VARCHAR(36),
  member VARCHAR(36),
  PRIMARY KEY(shared_group_id, member),
  FOREIGN KEY(shared_group_id) REFERENCES shared_groups(shared_group_id),
  FOREIGN KEY(member) REFERENCES accounts(account_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS invitees (
  shared_group_id VARCHAR(36),
  invitee VARCHAR(36),
  PRIMARY KEY(shared_group_id, invitee),
  FOREIGN KEY(shared_group_id) REFERENCES shared_groups(shared_group_id),
  FOREIGN KEY(invitee) REFERENCES accounts(account_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;