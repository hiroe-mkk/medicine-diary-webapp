export class HttpRequestClient {
  static async submitPostRequest(
    url,
    from,
    activateResultMessage,
    fieldErrors
  ) {
    try {
      const response = await fetch(url, { method: 'POST', body: from });
      return await parseResponse(response);
    } catch (error) {
      if (error instanceof HttpRequestFailedError) {
        if (error.status === 400) {
          // バインドエラーが発生した場合
          if (
            fieldErrors &&
            !error.isBodyEmpty() &&
            error.body.fieldErrors !== undefined
          ) {
            fieldErrors.set(error.body.fieldErrors);
            throw error;
          }
        } else if (error.status === 401) {
          throw error;
        } else if (error.status === 500) {
          activateResultMessage(
            'ERROR',
            'システムエラーが発生しました。',
            'お手数ですが、再度お試しください。'
          );
          throw error;
        } else if (error.hasMessage()) {
          activateResultMessage(
            'ERROR',
            'エラーが発生しました。',
            error.getMessage()
          );
          throw error;
        }
      }

      activateResultMessage(
        'ERROR',
        'エラーが発生しました。',
        '通信状態をご確認のうえ、再度お試しください。'
      );

      // 呼び出し元にもエラーを伝えるためにエラーを再スローする
      throw error;
    }
  }

  static async submitGetRequest(url, activateResultMessage) {
    try {
      const response = await fetch(url, { method: 'GET' });
      return await parseResponse(response);
    } catch (error) {
      if (error instanceof HttpRequestFailedError) {
        if (error.status === 401) {
          location.reload();
          throw error;
        } else if (error.status === 500) {
          activateResultMessage(
            'ERROR',
            'システムエラーが発生しました。',
            'お手数ですが、再度お試しください。'
          );
          throw error;
        } else if (error.hasMessage()) {
          activateResultMessage(
            'ERROR',
            'エラーが発生しました。',
            error.getMessage()
          );
          throw error;
        }
      }

      activateResultMessage(
        'ERROR',
        'エラーが発生しました。',
        '通信状態をご確認のうえ、再度お試しください。'
      );

      // 呼び出し元にもエラーを伝えるためにエラーを再スローする
      throw error;
    }
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

  hasMessage() {
    if (this.isBodyEmpty()) return false;

    return (
      this.body.error !== undefined && this.body.error.message !== undefined
    );
  }

  getMessage() {
    if (!this.hasMessage()) return '';

    const message = this.body.error.message;
    const details = this.body.error.details;
    return details === undefined ? message : `${message}(${details})`;
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
  if (contentType != null && contentType.includes('json')) {
    return await response.json();
  }
  return undefined;
}
