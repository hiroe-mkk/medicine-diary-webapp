import { TakingRecordOverviewDAO } from '@main/js/composables/TakingRecordOverviewDAO.js';

export class TakingRecordOverviews {
  constructor(filter) {
    this._page = 0;
    this._sizePerPage = 10;
    this._totalPages = 0;
    this._filter = filter;
    this._idToTakingRecordOverview = {};
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

  async load() {
    const result = await TakingRecordOverviewDAO.load(
      this._page,
      this._sizePerPage,
      this._filter
    );
    result.takingRecordOverviews.forEach((takingRecordOverview) => {
      this._idToTakingRecordOverview[takingRecordOverview.takingRecordId] =
        takingRecordOverview;
    });

    this._page++;
    this._totalPages = result.totalPages;
  }
}
