import { TakingRecordDAO } from '@main/js/composables/TakingRecordDAO.js';

export class TakingRecordDetail {
  constructor() {
    this._takingRecordDetail = {};
  }

  get value() {
    return this._takingRecordDetail;
  }

  get hasValue() {
    return this._takingRecordDetail.takingRecordId !== undefined;
  }

  async load(takingRecordId) {
    const result = await TakingRecordDAO.findTakingRecordDetail(takingRecordId);
    this._takingRecordDetail = result;
  }
}
