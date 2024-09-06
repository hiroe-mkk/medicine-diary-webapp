DROP TABLE invitees;

CREATE TABLE IF NOT EXISTS pending_invitations (
  invite_code VARCHAR(8) PRIMARY KEY,
  shared_group_id VARCHAR(36) NOT NULL,
  invited_on DATE NOT NULL,
  FOREIGN KEY(shared_group_id) REFERENCES sharedgroups(shared_group_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
