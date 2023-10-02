import {
  HttpRequestClient,
  HttpRequestFailedError,
} from '@main/js/composables/HttpRequestClient.js';

describe('HttpRequestClientTest', () => {
  test('submitPOSTRequest', async () => {
    //given:
    const response = Promise.resolve({
      ok: true,
      status: 204,
      headers: new Headers(),
    });
    global.fetch = jest.fn().mockImplementation(() => response);
    const form = new URLSearchParams({ username: 'newUsername' });

    //when:
    const result = await HttpRequestClient.submitPostRequest(
      '/api/profile/username/change',
      form
    );

    //then:
    expect(result).toEqual({});
  });

  test('validationErrorOccurs, submittingPOSTRequestFails', () => {
    //given:
    const response = Promise.resolve({
      ok: false,
      status: 400,
      headers: new Headers({ 'Content-Type': 'json' }),
      json: () => {
        return {
          fieldErrors: { username: ['30文字以内で入力してください。'] },
        };
      },
    });
    global.fetch = jest.fn().mockImplementation(() => response);
    const form = new URLSearchParams({
      username: 'abcdefghijklmnopqrstuvwxyzABCDE',
    });

    //when:
    const target = () =>
      HttpRequestClient.submitPostRequest('/api/profile/username/change', form);

    //then:
    expect(target).rejects.toThrowError(HttpRequestFailedError);
  });
});