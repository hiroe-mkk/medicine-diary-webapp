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