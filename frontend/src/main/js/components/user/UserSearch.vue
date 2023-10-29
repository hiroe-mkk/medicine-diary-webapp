<template>
  <div class="modal" :class="{ 'is-active': isSearchModalActive }">
    <div class="modal-background" @click="isSearchModalActive = false"></div>
    <div class="modal-content">
      <div class="notification has-background-white-bis">
        <div class="has-text-right">
          <button
            class="delete"
            type="button"
            @click="isSearchModalActive = false"
          ></button>
        </div>
        <div class="content has-text-centered">
          <h1 class="title is-spaced is-4">ユーザー検索</h1>
          <div class="field is-grouped">
            <p class="control is-expanded">
              <input
                class="input is-rounded is-info"
                type="text"
                v-model="keyword"
                placeholder="ユーザー名"
              />
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
        </div>
        <div class="content" v-if="searchResults.value !== undefined">
          <template v-for="user in searchResults.value.users">
            <div
              class="media px-3 is-flex is-align-items-center is-clickable"
              @click="selected(user)"
            >
              <div class="media-left">
                <figure class="image is-64x64">
                  <img
                    :src="user.profileImageURL"
                    class="is-rounded"
                    v-if="user.profileImageURL !== undefined"
                  />
                  <img
                    :src="noProfileImage"
                    class="is-rounded"
                    v-if="user.profileImageURL === undefined"
                  />
                </figure>
              </div>
              <div class="media-content">
                <div class="content px-3 has-text-left">
                  <strong>
                    {{ user.username }}
                  </strong>
                </div>
              </div>
            </div>
          </template>
        </div>
      </div>
    </div>
  </div>

  <div
    class="modal"
    :class="{ 'is-active': isConfirmationModalActive }"
    v-if="selectedUser.value !== undefined"
  >
    <div
      class="modal-background"
      @click="isConfirmationModalActive = false"
    ></div>
    <div class="modal-content is-flex is-justify-content-center">
      <div class="notification py-3 px-5 is-white">
        <div
          class="is-size-6 has-text-weight-bold has-text-link-dark has-text-centered"
        >
          {{ confirmationMessage }}
        </div>
        <div class="content m-3 has-text-centered">
          <div class="is-flex is-justify-content-center">
            <figure class="image is-64x64 m-2">
              <img
                :src="selectedUser.value.profileImageURL"
                class="is-rounded"
                v-if="selectedUser.value.profileImageURL !== undefined"
              />
              <img
                :src="noProfileImage"
                class="is-rounded"
                v-if="selectedUser.value.profileImageURL === undefined"
              />
            </figure>
          </div>
          <strong>
            {{ selectedUser.value.username }}
          </strong>
        </div>
        <form class="form" method="post" :action="path">
          <input
            name="accountId"
            :value="selectedUser.value.accountId"
            hidden
          />
          <input name="_csrf" :value="props.csrf" hidden />
          <div class="field is-grouped is-grouped-centered">
            <p class="control">
              <button class="button is-small is-rounded is-link">はい</button>
            </p>
            <p class="control">
              <button
                type="button"
                class="button is-small is-rounded is-danger"
                @click="isConfirmationModalActive = false"
              >
                いいえ
              </button>
            </p>
          </div>
        </form>
      </div>
    </div>
  </div>

  <ResultMessage ref="resultMessage"></ResultMessage>
</template>

<script setup>
import { ref, reactive, defineExpose } from 'vue';
import noProfileImage from '@main/images/no_profile_image.png';
import { HttpRequestClient } from '@main/js/composables/HttpRequestClient.js';

const props = defineProps({
  confirmationMessage: String,
  path: String,
  csrf: String,
});

defineExpose({ activateSearchModal });

const isSearchModalActive = ref(false);
const keyword = ref('');
const searchResults = reactive({ value: undefined });

const selectedUser = reactive({ value: undefined });
const isConfirmationModalActive = ref('');

const resultMessage = ref(null);

function activateSearchModal() {
  keyword.value = '';
  searchResults.value = undefined;
  isSearchModalActive.value = true;
}

function search() {
  const params = new URLSearchParams();
  params.set('keyword', keyword.value);

  return HttpRequestClient.submitGetRequest('/api/users?' + params.toString())
    .then((data) => {
      searchResults.value = data;
    })
    .catch((error) => {
      resultMessage.value.activate(
        'ERROR',
        'エラーが発生しました。',
        '通信状態をご確認のうえ、再度お試しください。'
      );
    });
}

function selected(user) {
  selectedUser.value = user;
  isConfirmationModalActive.value = true;
}
</script>
