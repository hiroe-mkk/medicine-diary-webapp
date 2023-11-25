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
            <span class="help">
              ※ユーザー名が設定されていないユーザーを検索することはできません。
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

  <ConfirmationMessage
    ref="confirmationMessage"
    :message="
      props.sharedGroupId === undefined
        ? 'このユーザーと共有しますか？'
        : 'このユーザーを招待しますか？'
    "
    :button-label="props.sharedGroupId === undefined ? '共有する' : '招待する'"
    :path="
      props.sharedGroupId === undefined
        ? '/shared-group/share'
        : '/shared-group/invite'
    "
    :csrf="props.csrf"
  >
  </ConfirmationMessage>

  <ResultMessage ref="resultMessage"></ResultMessage>
</template>

<script setup>
import { ref, reactive, defineExpose } from 'vue';
import { HttpRequestClient } from '@main/js/composables/HttpRequestClient.js';
import ResultMessage from '@main/js/components/ResultMessage.vue';
import ConfirmationMessage from '@main/js/components/ConfirmationMessage.vue';

const props = defineProps({
  sharedGroupId: String,
  csrf: String,
});
defineExpose({ activateSearchModal });

const isSearchModalActive = ref(false);
const keyword = ref('');
const searchResults = reactive([]);
const isSearchSucceeds = ref(false);

const resultMessage = ref(null);
const confirmationMessage = ref(null);

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
      resultMessage.value.activate(
        'ERROR',
        'エラーが発生しました。',
        '通信状態をご確認のうえ、再度お試しください。'
      );
    });
}

function selected(user) {
  const params = { accountId: user.accountId };
  if (props.sharedGroupId !== undefined) {
    params.sharedGroupId = props.sharedGroupId;
  }
  confirmationMessage.value.activate(params);
}
</script>