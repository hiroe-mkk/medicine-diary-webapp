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