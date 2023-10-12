import { HttpRequestClient } from '@main/js/composables/HttpRequestClient.js';

// バックエンドから服用記録を取得する DAO
export class TakingRecordDAO {
  static findTakingRecordOverviews(page, sizePerPage, filter) {
    return HttpRequestClient.submitGetRequest(
      '/api/takingrecords?' +
        this._createParams(page, sizePerPage, filter).toString()
    ).catch((error) => {
      throw new TakingRecordLoadingFailed();
    });
  }

  static findTakingRecordDetail(takingRecordId) {
    return HttpRequestClient.submitGetRequest(
      `/api/takingrecords/${takingRecordId}`
    ).catch((error) => {
      throw new TakingRecordLoadingFailed();
    });
  }

  static _createParams(page, sizePerPage, filter) {
    const params = new URLSearchParams();
    params.set('page', page);
    params.set('size', sizePerPage);
    Object.keys(filter).forEach((key) => {
      params.set(key, filter[key]);
    });
    return params;
  }
}

export class TakingRecordLoadingFailed extends Error {
  constructor() {
    super();

    this.name = new.target.name;
    if (Error.captureStackTrace) {
      Error.captureStackTrace(this, this.constructor);
    }
  }
}
