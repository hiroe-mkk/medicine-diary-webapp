import { MedicationRecordOverviews } from '@main/js/composables/model/MedicationRecordOverviews.js';
import { MedicationRecordDAO } from '@main/js/composables/MedicationRecordDAO.js';
jest.mock('@main/js/composables/MedicationRecordDAO.js');

describe('loadMedicationRecordOverviews', () => {
  test('load', async () => {
    //given:
    const values = createTestmedicationRecordOverviews(3);
    MedicationRecordDAO.findMedicationRecordOverviews.mockReturnValue({
      number: 0,
      totalPages: 1,
      medicationRecordOverviews: values,
    });
    const filter = { medicineid: 'medicineId' };
    const medicationRecordOverviews = new MedicationRecordOverviews(filter);

    //when:
    await medicationRecordOverviews.load();

    //then:
    expect(medicationRecordOverviews.size).toEqual(3);
    expect(medicationRecordOverviews.canLoadMore).toBeFalsy();
    expect(medicationRecordOverviews.values[values[0].medicationRecordId]).toEqual(
      values[0]
    );
    expect(medicationRecordOverviews.values[values[1].medicationRecordId]).toEqual(
      values[1]
    );
    expect(medicationRecordOverviews.values[values[2].medicationRecordId]).toEqual(
      values[2]
    );
  });
});

function createTestMedicationRecordOverviews(size) {
  return [...Array(size)].map((_, index) =>
    createTestMedicationRecordOverview(index)
  );
}

function createTestMedicationRecordOverview(index) {
  return {
    medicationRecordId: `medicationRecordId${index}`,
    beforeMedication: `beforeMedication${index}`,
    afterMedication: `afterMedication${index}`,
    takenAt: `takenAt${index}`,
    medicineId: `medicineId${index}`,
    medicineName: `medicineName${index}`,
    accountId: `accountId${index}`,
    username: `username${index}`,
    profileImageURL: `profileImageURL${index}`,
  };
}