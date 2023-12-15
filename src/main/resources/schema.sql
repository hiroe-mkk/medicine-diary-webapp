CREATE TABLE IF NOT EXISTS accounts (
  account_id VARCHAR(36) NOT NULL PRIMARY KEY,
  credential_type VARCHAR(15) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS oauth2_credentials (
  account_id VARCHAR(36) NOT NULL PRIMARY KEY,
  id_p VARCHAR(30) NOT NULL,
  subject VARCHAR(255) NOT NULL,
  UNIQUE(id_p, subject),
  FOREIGN KEY(account_id) REFERENCES accounts(account_id),
  INDEX id_p_subject_index (id_p, subject)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS profiles (
  account_id VARCHAR(36) NOT NULL PRIMARY KEY,
  username VARCHAR(30) NOT NULL,
  profile_image_url_endpoint VARCHAR(50),
  profile_image_url_path VARCHAR(100),
  FOREIGN KEY(account_id) REFERENCES accounts(account_id),
  INDEX username_index (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;


CREATE TABLE IF NOT EXISTS members (
  shared_group_id VARCHAR(36) NOT NULL,
  member VARCHAR(36) NOT NULL,
  PRIMARY KEY(shared_group_id, member),
  FOREIGN KEY(member) REFERENCES accounts(account_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS invitees (
  shared_group_id VARCHAR(36) NOT NULL,
  invitee VARCHAR(36) NOT NULL,
  PRIMARY KEY(shared_group_id, invitee),
  FOREIGN KEY(invitee) REFERENCES accounts(account_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;


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
  registered_at DATETIME NOT NULL,
  FOREIGN KEY(account_id) REFERENCES accounts(account_id),
  FOREIGN KEY(shared_group_id) REFERENCES members(shared_group_id)
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


CREATE TABLE IF NOT EXISTS medication_records (
  medication_record_id VARCHAR(36) NOT NULL PRIMARY KEY,
  recorder VARCHAR(36) NOT NULL,
  taken_medicine VARCHAR(36) NOT NULL,
  quantity DOUBLE NOT NULL,
  symptom VARCHAR(30) NOT NULL,
  before_medication VARCHAR(15) NOT NULL,
  after_medication VARCHAR(15),
  note VARCHAR(500) NOT NULL,
  taken_at DATETIME NOT NULL,
  FOREIGN KEY(recorder) REFERENCES accounts(account_id),
  FOREIGN KEY(taken_medicine) REFERENCES medicines(medicine_id),
  INDEX recorder_taken_medicine_index (recorder, taken_medicine)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;