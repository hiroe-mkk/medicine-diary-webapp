import { TakingRecordOverviewDAO } from '@main/js/composables/TakingRecordOverviewDAO.js';

describe('TakingRecordOverviewDAOTest', () => {
  test('createParams', async () => {
    //given:
    const page = 0;
    const size = 10;
    const filter = { medicineid: 'medicineId' };

    //when:
    const actual = TakingRecordOverviewDAO._createParams(page, size, filter);

    //then:
    expect(actual.get('page')).toEqual(page.toString());
    expect(actual.get('size')).toEqual(size.toString());
    expect(actual.get('medicineid')).toEqual('medicineId');
  });
});
