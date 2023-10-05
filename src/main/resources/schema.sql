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
  username VARCHAR(50) NOT NULL,
  profile_image_endpoint VARCHAR(50),
  profile_image_path VARCHAR(100),
  FOREIGN KEY(account_id) REFERENCES accounts(account_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;