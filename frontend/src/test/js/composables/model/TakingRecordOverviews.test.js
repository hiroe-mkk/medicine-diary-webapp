import { TakingRecordOverviews } from '@main/js/composables/model/TakingRecordOverviews.js';
import { TakingRecordOverviewDAO } from '@main/js/composables/TakingRecordOverviewDAO.js';
jest.mock('@main/js/composables/TakingRecordOverviewDAO.js');

describe('loadTakingRecordOverviews', () => {
  test('load', async () => {
    //given:
    const values = createTestTakingRecordOverviews(3);
    TakingRecordOverviewDAO.load.mockReturnValue({
      number: 0,
      totalPages: 1,
      takingRecordOverviews: values,
    });
    const filter = { medicineid: 'medicineId' };
    const takingRecordOverviews = new TakingRecordOverviews(filter);

    //when:
    await takingRecordOverviews.load();

    //then:
    expect(takingRecordOverviews.size).toEqual(3);
    expect(takingRecordOverviews.canLoadMore).toBeFalsy();
    expect(takingRecordOverviews.values[values[0].takingRecordId]).toEqual(
      values[0]
    );
    expect(takingRecordOverviews.values[values[1].takingRecordId]).toEqual(
      values[1]
    );
    expect(takingRecordOverviews.values[values[2].takingRecordId]).toEqual(
      values[2]
    );
  });
});

function createTestTakingRecordOverviews(size) {
  return [...Array(size)].map((_, index) =>
    createTestTakingRecordOverview(index)
  );
}

function createTestTakingRecordOverview(index) {
  return {
    takingRecordId: `takingRecordId${index}`,
    beforeTaking: `beforeTaking${index}`,
    afterTaking: `afterTaking${index}`,
    takenAt: `takenAt${index}`,
    medicineId: `medicineId${index}`,
    medicineName: `medicineName${index}`,
    accountId: `accountId${index}`,
    username: `username${index}`,
    profileImageURL: `profileImageURL${index}`,
  };
}
