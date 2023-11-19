import { HttpRequestClient } from '@main/js/composables/HttpRequestClient.js';

export class MedicationRecords {
  constructor() {
    this._idToMedicationRecord = {};
    this._isLoaded = false;
    this._filter = undefined;

    this._page = 0;
    this._sizePerPage = 100;
    this._totalPages = 0;
  }

  get values() {
    return this._idToMedicationRecord;
  }

  get size() {
    return Object.keys(this._idToMedicationRecord).length;
  }

  get isLoaded() {
    return this._isLoaded;
  }

  get canLoadMore() {
    return this._page < this._totalPages;
  }

  getMedicationRecord(medicationRecordId) {
    return this._idToMedicationRecord[medicationRecordId];
  }

  async load(filter) {
    this._isLoaded = false;
    this._filter = filter.copy();
    this._idToMedicationRecord = {};

    this._page = 0;
    this._sizePerPage = 10;
    this._totalPages = 0;

    await this.loadMore();
  }

  async loadMore() {
    const params = this._filter.createParams();
    params.append('page', this._page);
    params.append('size', this._sizePerPage);

    await HttpRequestClient.submitGetRequest(
      '/api/medication-records?' + params.toString()
    ).then((data) => {
      data.medicationRecords.forEach((medicationRecord) => {
        this._idToMedicationRecord[medicationRecord.medicationRecordId] =
          medicationRecord;
      });

      this._page++;
      this._totalPages = data.totalPages;
      this._isLoaded = true;
    });
  }

  delete(medicationRecordId) {
    delete this._idToMedicationRecord[medicationRecordId];
  }
}

export class MedicationRecordUtils {
  static toTime(dateTime) {
    return dateTime.slice(11, 19);
  }

  static convertConditionLevelToIcon(conditionLevel) {
    let icon;
    switch (conditionLevel) {
      case 'GOOD':
        icon = '<i class="fa-regular fa-face-laugh-squint"></i>';
        break;
      case 'NOT_BAD':
        icon = '<i class="fa-regular fa-face-smile"></i>';
        break;
      case 'A_LITTLE_BAD':
        icon = '<i class="fa-regular fa-face-frown"></i>';
        break;
      case 'BAD':
        icon = '<i class="fa-regular fa-face-sad-tear"></i>';
        break;
      case 'VERY_BAD':
        icon = '<i class="fa-regular fa-face-dizzy"></i>';
        break;
    }
    return `<span class="icon is-small mx-1">${icon}</span>`;
  }

  static convertConditionLevelToString(conditionLevel) {
    switch (conditionLevel) {
      case 'GOOD':
        return '良い';
      case 'NOT_BAD':
        return '普通';
      case 'A_LITTLE_BAD':
        return '少し悪い';
      case 'BAD':
        return '悪い';
      case 'VERY_BAD':
        return 'とても悪い';
    }
  }
}

export class Filter {
  constructor() {
    this.medicineId = undefined;
    this.accountIds = {};
    this.start = undefined;
    this.end = undefined;
  }

  addAccountId(accountId) {
    this.accountIds[accountId] = true;
  }

  addAllAccountIds(accountIds) {
    accountIds.forEach((accountId) => (this.accountIds[accountId] = true));
  }

  isUserActive(accountId) {
    return this.accountIds[accountId];
  }

  toggleUserActive(accountId) {
    this.accountIds[accountId] = !this.accountIds[accountId];
  }

  activeUserOnly(accountId) {
    Object.keys(this.accountIds).forEach((key) => {
      this.accountIds[key] = false;
    });
    this.accountIds[accountId] = true;
  }

  copy() {
    const copiedFilter = new Filter();
    copiedFilter.medicineId = this.medicineId;
    copiedFilter.accountIds = this.accountIds;
    copiedFilter.start = this.start;
    copiedFilter.end = this.end;
    return copiedFilter;
  }

  createParams() {
    const params = new URLSearchParams();
    if (this.medicineId !== undefined) {
      params.append('medicineid', this.medicineId);
    }
    Object.keys(this.accountIds).forEach((accountId) => {
      if (this.accountIds[accountId]) params.append('accountids', accountId);
    });
    if (this.start !== undefined)
      params.append('start', this.start.replace(/-/g, '/'));
    if (this.end !== undefined)
      params.append('end', this.end.replace(/-/g, '/'));

    return params;
  }
}
