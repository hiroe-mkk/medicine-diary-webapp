import { HttpRequestClient } from '@main/js/composables/HttpRequestClient.js';

export class TakingRecordOverviews {
  constructor() {
    this._filter = undefined;
    this._idToTakingRecordOverview = {};

    this._page = 0;
    this._sizePerPage = 10;
    this._totalPages = 0;
  }

  get values() {
    return this._idToTakingRecordOverview;
  }

  get size() {
    return Object.keys(this._idToTakingRecordOverview).length;
  }

  get canLoadMore() {
    return this._page < this._totalPages;
  }

  async load(filter) {
    this._filter = filter.copy();
    this._idToTakingRecordOverview = {};

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
      data.takingRecordOverviews.forEach((takingRecordOverview) => {
        this._idToTakingRecordOverview[takingRecordOverview.takingRecordId] =
          takingRecordOverview;
      });

      this._page++;
      this._totalPages = data.totalPages;
    });
  }

  delete(takingRecordId) {
    delete this._idToTakingRecordOverview[takingRecordId];
  }
}

export class Filter {
  constructor(medicineId, allMembers) {
    this.medicine = medicineId;
    this._members = {};
    if (allMembers !== undefined) {
      allMembers.forEach((accountId) => (this._members[accountId] = true));
    }
    this.start = undefined;
    this.end = undefined;
  }

  enableMember(accountId) {
    this._members[accountId].isEnabled = true;
  }

  disableMember(accountId) {
    this._members[accountId].isEnabled = false;
  }

  copy() {
    const copiedFilter = new Filter();
    copiedFilter._medicine = this.medicine;
    copiedFilter._members = { ...this._members };
    copiedFilter._start = this.start;
    copiedFilter._end = this.end;
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
