ALTER TABLE medication_records
  ADD COLUMN taken_medicine_on DATE,
  ADD COLUMN taken_medicine_at TIME;

UPDATE medication_records
  SET taken_medicine_on = DATE(taken_at),
      taken_medicine_at = TIME(taken_at);

ALTER TABLE medication_records
  DROP COLUMN taken_at;