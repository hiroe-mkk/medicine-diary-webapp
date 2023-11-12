import { HttpRequestClient } from '@main/js/composables/HttpRequestClient.js';

export class TakingRecords {
  constructor() {
    this._idToTakingRecord = {};
    this._isLoaded = false;
    this._filter = undefined;

    this._page = 0;
    this._sizePerPage = 10;
    this._totalPages = 0;
  }

  get values() {
    return this._idToTakingRecord;
  }

  get size() {
    return Object.keys(this._idToTakingRecord).length;
  }

  get isLoaded() {
    return this._isLoaded;
  }

  get canLoadMore() {
    return this._page < this._totalPages;
  }

  getTakingRecord(takingRecordId) {
    return this._idToTakingRecord[takingRecordId];
  }

  async load(filter) {
    this._isLoaded = false;
    this._filter = filter.copy();
    this._idToTakingRecord = {};

    this._page = 0;
    this._sizePerPage = 10;
    this._totalPages = 0;

    this.loadMore();
  }

  async loadMore() {
    const params = this._filter.createParams();
    params.append('page', this._page);
    params.append('size', this._sizePerPage);

    HttpRequestClient.submitGetRequest(
      '/api/takingrecords?' + params.toString()
    ).then((data) => {
      data.takingRecords.forEach((takingRecord) => {
        this._idToTakingRecord[takingRecord.takingRecordId] = takingRecord;
      });

      this._page++;
      this._totalPages = data.totalPages;
      this._isLoaded = true;
    });
  }

  delete(takingRecordId) {
    delete this._idToTakingRecord[takingRecordId];
  }
}

export class TakingRecordUtils {
  static toTime(dateTime) {
    return dateTime.slice(11, 19);
  }

  static toIcon(conditionLevel) {
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

export class Filter {
  constructor() {
    this.medicine = undefined;
    this._members = undefined;
    this.start = undefined;
    this.end = undefined;
  }

  initializeMembers(accountIds) {
    this._members = {};
    if (accountIds !== undefined) {
      accountIds.forEach((accountId) => (this._members[accountId] = true));
    }
  }

  isMemberActive(accountId) {
    return this._members[accountId];
  }

  toggleMemberActive(accountId) {
    this._members[accountId] = !this._members[accountId];
  }

  copy() {
    const copiedFilter = new Filter();
    copiedFilter.medicine = this.medicine;
    copiedFilter._members = this._members;
    copiedFilter.start = this.start;
    copiedFilter.end = this.end;
    return copiedFilter;
  }

  createParams() {
    const params = new URLSearchParams();
    if (this.medicine !== undefined) params.append('medicine', this.medicine);
    Object.keys(this._members).forEach((accountId) => {
      if (this._members[accountId]) params.append('members', accountId);
    });
    if (this.start !== undefined) params.append('start', this.start);
    if (this.end !== undefined) params.append('end', this.end);

    return params;
  }
}
