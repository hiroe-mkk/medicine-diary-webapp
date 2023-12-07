<template>
  <div class="modal" :class="{ 'is-active': isSearchModalActive }">
    <div class="modal-background" @click="isSearchModalActive = false"></div>
    <div class="modal-content">
      <div class="notification has-background-white py-3 px-5">
        <div class="has-text-right">
          <button
            class="delete"
            type="button"
            @click="isSearchModalActive = false"
          ></button>
        </div>
        <p class="is-size-5 has-text-weight-bold is-link has-text-centered">
          ユーザー検索
        </p>
        <div class="field my-3 is-grouped">
          <p class="control is-expanded">
            <input
              class="input is-rounded is-info"
              type="text"
              v-model="keyword"
              placeholder="ユーザー名"
            />
            <span class="help has-text-left">
              ※ ユーザー名が設定されていないユーザーを検索することはできません。
            </span>
          </p>
          <p class="control">
            <button
              type="button"
              class="button is-rounded is-info"
              @click="search()"
            >
              検索
            </button>
          </p>
        </div>
        <div class="content m-3" v-if="isSearchSucceeds">
          <p
            class="has-text-weight-bold has-text-danger-dark"
            v-if="searchResults.length === 0"
          >
            ユーザーが見つかりませんでした。
          </p>
          <template v-for="user in searchResults">
            <div
              class="media px-3 is-flex is-align-items-center is-clickable p-3 m-0"
              @click="selected(user)"
            >
              <div class="media-left">
                <figure class="image is-64x64 m-0">
                  <img
                    :src="user.profileImageURL"
                    class="is-rounded"
                    v-if="user.profileImageURL !== undefined"
                  />
                  <img
                    src="@main/images/no_profile_image.png"
                    alt="noImage"
                    class="is-rounded"
                    v-if="user.profileImageURL === undefined"
                  />
                </figure>
              </div>
              <div class="media-content px-3 has-text-left">
                <p class="has-text-weight-bold has-text-grey-dark">
                  {{ user.username }}
                </p>
              </div>
            </div>
          </template>
        </div>
      </div>
    </div>
  </div>

  <div class="modal" :class="{ 'is-active': isSelectedModalActive }">
    <div class="modal-background" @click="isSelectedModalActive = false"></div>
    <div class="modal-content is-flex is-justify-content-center">
      <div class="message is-inline-block is-info">
        <div class="message-body">
          <div class="content">
            <p class="has-text-centered mb-2">
              <strong class="is-size-5 mb-1">
                {{
                  props.sharedGroupId === undefined
                    ? 'このユーザーと共有しますか？'
                    : 'このユーザーを招待しますか？'
                }}
              </strong>
              <br />
            </p>
            <div class="field is-grouped is-grouped-centered p-2">
              <p class="control">
                <button
                  type="button"
                  class="button is-small is-rounded is-link"
                  @click="selected()"
                >
                  {{
                    props.sharedGroupId === undefined ? '共有する' : '招待する'
                  }}
                </button>
              </p>
              <p class="control">
                <button
                  type="button"
                  class="button is-small is-rounded is-outlined is-danger"
                  @click="isSelectedModalActive = false"
                >
                  キャンセル
                </button>
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, defineEmits, defineExpose, inject } from 'vue';
import {
  HttpRequestClient,
  HttpRequestFailedError,
} from '@main/js/composables/HttpRequestClient.js';

const props = defineProps({
  sharedGroupId: String,
  csrf: String,
});
const emits = defineEmits(['update']);
const activateResultMessage = inject('activateResultMessage');
defineExpose({ activateSearchModal });

const isSearchModalActive = ref(false);
const keyword = ref('');
const searchResults = reactive([]);
const isSearchSucceeds = ref(false);
const isSelectedModalActive = ref(false);

function activateSearchModal() {
  keyword.value = '';
  searchResults.splice(0, searchResults.length);
  isSearchModalActive.value = true;
}

function search() {
  isSearchSucceeds.value = false;
  searchResults.splice(0, searchResults.length);
  const params = new URLSearchParams();
  params.set('keyword', keyword.value);

  return HttpRequestClient.submitGetRequest('/api/users?' + params.toString())
    .then((data) => {
      searchResults.push(...data.users);
      isSearchSucceeds.value = true;
    })
    .catch(() => {
      activateResultMessage(
        'ERROR',
        'エラーが発生しました。',
        '通信状態をご確認のうえ、再度お試しください。'
      );
    });
}

function selected(user) {
  const form = new FormData();
  form.set('_csrf', props.csrf);
  if (props.sharedGroupId !== undefined) {
    form.set('sharedGroupId', props.sharedGroupId);
  }
  form.set('accountId', user.accountId);

  const path =
    props.sharedGroupId === undefined
      ? '/api/shared-group/share'
      : '/api/shared-group/invite';
  const message =
    props.sharedGroupId === undefined
      ? '共有リクエストを送信しました。'
      : '共有グループに招待しました。';
  HttpRequestClient.submitPostRequest(path, form)
    .then(() => {
      activateResultMessage('INFO', message);
      isSelectedModalActive.value = false;
      emits('update');
    })
    .catch((error) => {
      if (error instanceof HttpRequestFailedError) {
        if (error.status == 401) {
          // 認証エラーが発生した場合
          location.reload();
          return;
        } else if (error.status == 500) {
          activateResultMessage(
            'ERROR',
            'システムエラーが発生しました。',
            'お手数ですが、再度お試しください。'
          );
          return;
        } else if (error.hasMessage()) {
          activateResultMessage(
            'ERROR',
            'エラーが発生しました。',
            error.getMessage()
          );
          return;
        }
      }

      activateResultMessage(
        'ERROR',
        'エラーが発生しました。',
        '通信状態をご確認のうえ、再度お試しください。'
      );
    });
}
</script>