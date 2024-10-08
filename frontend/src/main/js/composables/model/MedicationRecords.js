import { HttpRequestClient } from '@main/js/composables/HttpRequestClient.js';

export class MedicationRecords {
  constructor() {
    this._idToMedicationRecord = {};
    this._isLoaded = false;
    this._filter = undefined;
    this._activateResultMessage = undefined;

    this.__currentPage = 0;
    this._totalPages = 0;
  }

  get values() {
    return this._idToMedicationRecord;
  }

  get valuesWithReverse() {
    return this._idToMedicationRecord;
  }

  get size() {
    return Object.keys(this._idToMedicationRecord).length;
  }

  get isLoaded() {
    return this._isLoaded;
  }

  get canLoadMore() {
    return this.__currentPage < this._totalPages;
  }

  getMedicationRecord(medicationRecordId) {
    return this._idToMedicationRecord[medicationRecordId];
  }

  async load(filter, activateResultMessage) {
    this._isLoaded = false;
    this._filter = filter.copy();
    this._idToMedicationRecord = {};
    this._activateResultMessage = activateResultMessage;

    this.__currentPage = 0;
    this._totalPages = 0;

    await this.loadMore();
  }

  async loadMore() {
    const params = this._filter.createParams();
    params.append('page', this.__currentPage);

    await HttpRequestClient.submitGetRequest(
      '/api/medication-records?' + params.toString(),
      this._activateResultMessage
    ).then((data) => {
      data.medicationRecords.forEach((medicationRecord) => {
        this._idToMedicationRecord[medicationRecord.medicationRecordId] =
          medicationRecord;
      });

      this.__currentPage++;
      this._totalPages = data.totalPages;
      this._isLoaded = true;
    });
  }

  delete(medicationRecordId) {
    delete this._idToMedicationRecord[medicationRecordId];
  }
}

export class Filter {
  constructor() {
    this.medicineId = undefined;
    this.accountId = undefined;
    this.start = undefined;
    this.end = undefined;
    this.sizePerPage = 20;
  }

  copy() {
    const copiedFilter = new Filter();
    copiedFilter.medicineId = this.medicineId;
    copiedFilter.accountId = this.accountId;
    copiedFilter.start = this.start;
    copiedFilter.end = this.end;
    copiedFilter.sizePerPage = this.sizePerPage;
    return copiedFilter;
  }

  createParams() {
    const params = new URLSearchParams();
    if (this.medicineId !== undefined)
      params.append('medicine', this.medicineId);
    if (this.accountId !== undefined) params.append('account', this.accountId);
    if (this.start !== undefined) params.append('start', this.start);
    if (this.end !== undefined) params.append('end', this.end);
    params.append('size', this.sizePerPage);

    return params;
  }
}

export class MedicationRecordUtils {
  static convertConditionLevelToIcon(conditionLevel) {
    let icon;
    switch (conditionLevel) {
      case '良い':
        icon = '<i class="fa-regular fa-face-laugh-squint"></i>';
        break;
      case '普通':
        icon = '<i class="fa-regular fa-face-smile"></i>';
        break;
      case '少し悪い':
        icon = '<i class="fa-regular fa-face-frown"></i>';
        break;
      case '悪い':
        icon = '<i class="fa-regular fa-face-sad-tear"></i>';
        break;
      case 'とても悪い':
        icon = '<i class="fa-regular fa-face-dizzy"></i>';
        break;
    }
    return `<span class="icon is-small mx-1">${icon}</span>`;
  }
}
