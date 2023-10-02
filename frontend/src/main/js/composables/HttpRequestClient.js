export class HttpRequestClient {
  static async submitPostRequest(url, from) {
    const response = await fetch(url, { method: 'POST', body: from });
    return parseResponse(response);
  }
}

// HTTP リクエストに失敗したことを示すエラー
export class HttpRequestFailedError extends Error {
  constructor(status, body) {
    super();
    this.status = status;
    this.body = body;

    this.name = new.target.name;
    if (Error.captureStackTrace) {
      Error.captureStackTrace(this, this.constructor);
    }
  }

  isBodyEmpty() {
    return this.body === undefined;
  }
}

// HTTP レスポンスを解析する
async function parseResponse(response) {
  const body = await extractJsonBody(response);
  if (!response.ok) throw new HttpRequestFailedError(response.status, body);

  return body;
}

// HTTP レスポンスからコンテンツタイプが JSON のボディを抽出する
// ボディが空だった場合は、undefined を返す。
async function extractJsonBody(response) {
  const contentType = response.headers.get('Content-Type');
  if (contentType != null && contentType.indexOf('json') != -1) {
    return await response.json();
  }
  return undefined;
}