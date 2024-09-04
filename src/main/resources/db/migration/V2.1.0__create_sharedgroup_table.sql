CREATE TABLE IF NOT EXISTS sharedgroups (
    shared_group_id VARCHAR(36) NOT NULL PRIMARY KEY
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

ALTER TABLE members
ADD CONSTRAINT fk_members_sharedgroups
FOREIGN KEY (shared_group_id) REFERENCES sharedgroups (shared_group_id);

ALTER TABLE invitees
ADD CONSTRAINT fk_invitees_sharedgroups
FOREIGN KEY (shared_group_id) REFERENCES sharedgroups (shared_group_id);

ALTER TABLE medicines
ADD CONSTRAINT fk_accounts_sharedgroups
FOREIGN KEY (account_id) REFERENCES accounts (account_id);

ALTER TABLE medicines
ADD CONSTRAINT fk_medicines_sharedgroups
FOREIGN KEY (shared_group_id) REFERENCES sharedgroups (shared_group_id);
