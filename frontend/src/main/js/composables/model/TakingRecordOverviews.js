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
  constructor() {
    this._medicine = undefined;
    this._members = [];
    this._start = undefined;
    this._end = undefined;
  }

  changeMedicine(medicine) {
    this._medicine = medicine;
  }

  addMember(member) {
    this._members.push(member);
  }

  removeMember(member) {
    this._members = this._members.filter((element) => element !== member);
  }

  changeStart(start) {
    this._start = start;
  }

  changeEnd(end) {
    this._end = end;
  }

  copy() {
    const copiedFilter = new Filter();
    copiedFilter._medicine = this._medicine;
    copiedFilter._members = [...this._members];
    copiedFilter._start = this._start;
    copiedFilter._end = this._end;
    return copiedFilter;
  }

  createParams() {
    const params = new URLSearchParams();
    if (this._medicine !== undefined) params.append('medicine', this._medicine);
    this._members.forEach((member) => {
      params.append('members', member);
    });
    if (this._start !== undefined) params.append('start', this._start);
    if (this._end !== undefined) params.append('end', this.end);

    return params;
  }
}
