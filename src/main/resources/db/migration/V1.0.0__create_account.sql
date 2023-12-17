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