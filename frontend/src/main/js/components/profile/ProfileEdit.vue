<template>
  <div class="content has-text-centered">
    <div class="is-flex is-justify-content-center">
      <figure
        class="image is-128x128"
        v-if="profile.profileImage !== undefined"
      >
        <img class="is-rounded" :src="profile.profileImage" />
      </figure>
    </div>
    <strong class="is-size-4 has-text-grey-dark">
      {{ profile.username }}
    </strong>
  </div>

  <div class="container is-max-desktop">
    <div class="panel is-white">
      <div class="panel-heading py-1 my-0"></div>
      <div
        class="panel-block has-text-grey has-background-white is-flex is-justify-content-space-between is-clickable"
        @click="activateUsernameChangeModal()"
      >
        <strong class="has-text-grey">ユーザー名</strong>
        <span class="icon is-small">
          <i class="fa-solid fa-greater-than"></i>
        </span>
      </div>
      <div
        class="panel-block has-text-grey has-background-white is-flex is-justify-content-space-between is-clickable"
        @click="activateProfileImageChangeModal()"
      >
        <strong class="has-text-grey">プロフィール画像</strong>
        <span class="icon is-small">
          <i class="fa-solid fa-greater-than"></i>
        </span>
      </div>
    </div>
  </div>

  <div class="modal" :class="{ 'is-active': isUsernameChangeModalActive }">
    <div
      class="modal-background"
      @click="isUsernameChangeModalActive = false"
    ></div>
    <div class="modal-content">
      <div class="notification is-white">
        <div class="title is-5 has-text-link-dark has-text-centered">
          ユーザー名を変更する
        </div>
        <section class="content">
          <form
            class="form"
            method="post"
            @submit.prevent="submitUsernameChangeForm()"
          >
            <div class="field">
              <div class="control">
                <input
                  class="input is-info"
                  type="text"
                  name="username"
                  v-model="editingUsername"
                  maxlength="30"
                  placeholder="ユーザー名"
                  :class="{ 'is-danger': fieldErrors.contains('username') }"
                />
              </div>
              <p class="help" v-if="!fieldErrors.contains('username')">
                ※30文字以内で入力してください。
              </p>
              <p
                class="help is-danger"
                v-for="error in fieldErrors.get('username')"
              >
                ※{{ error }}
              </p>
            </div>
            <div class="field is-grouped is-grouped-centered">
              <p class="control">
                <button class="button is-small is-rounded is-link">完了</button>
              </p>
              <p class="control">
                <button
                  type="button"
                  class="button is-small is-rounded is-danger"
                  @click="isUsernameChangeModalActive = false"
                >
                  キャンセル
                </button>
              </p>
            </div>
          </form>
        </section>
      </div>
    </div>
  </div>

  <div class="modal" :class="{ 'is-active': isProfileImageChangeModalActive }">
    <div
      class="modal-background"
      @click="isProfileImageChangeModalActive = false"
    ></div>
    <div class="modal-content">
      <div class="notification has-background-white-bis has-text-centered">
        <div class="title is-5 has-text-link-dark has-text-centered">
          プロフィール画像を変更する
        </div>
        <form class="form" method="post" enctype="multipart/form-data">
          <div class="container mb-2" ref="trimmingContainer"></div>
          <div class="file is-small is-info is-centered p-3">
            <label class="file-label">
              <input
                class="file-input"
                type="file"
                accept="image/*"
                @change="fileSelected($event)"
              />
              <span class="file-cta">
                <span class="file-label is-rounded">ファイルを選択する</span>
              </span>
            </label>
          </div>
          <div
            class="field is-grouped is-grouped-centered"
            v-show="ProfileImageTrimmingManager.isTrimming"
          >
            <p class="control">
              <button class="button is-small is-rounded is-link">完了</button>
            </p>
            <p class="control">
              <button
                type="button"
                class="button is-small is-rounded is-danger"
                @click="isProfileImageChangeModalActive = false"
              >
                キャンセル
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
import { ref, reactive } from 'vue';
import {
  HttpRequestClient,
  HTTPRequestFailedError,
} from '@main/js/composables/HttpRequestClient.js';
import { ProfileImageTrimmingManager } from '@main/js/composables/model/ProfileImageTrimmingManager.js';
import { FieldErrors } from '@main/js/composables/model/FieldErrors.js';
import ResultMessage from '@main/js/components/ResultMessage.vue';

const props = defineProps({
  username: String,
  profileImage: String,
  csrf: String,
});

const profile = reactive({
  username: props.username,
  profileImage: props.profileImage,
});
const isUsernameChangeModalActive = ref(false);
const editingUsername = ref('');

const isProfileImageChangeModalActive = ref(false);
const trimmingContainer = ref(null);
const profileImageTrimmingManager = new ProfileImageTrimmingManager();

const fieldErrors = reactive(new FieldErrors());
const resultMessage = ref(null);

function activateUsernameChangeModal() {
  fieldErrors.clear();
  editingUsername.value = profile.username;
  isUsernameChangeModalActive.value = true;
}

function submitUsernameChangeForm() {
  const form = new URLSearchParams();
  form.set('username', editingUsername.value);
  form.set('_csrf', props.csrf);

  HttpRequestClient.submitPostRequest('/api/profile/username/change', form)
    .then(() => {
      profile.username = editingUsername.value;
      isUsernameChangeModalActive.value = false;
      resultMessage.value.activate('INFO', 'ユーザー名の変更が完了しました。');
    })
    .catch((error) => {
      if (error instanceof HTTPRequestFailedError) {
        if (error.status == 400) {
          // バインドエラーが発生した場合
          if (!error.isBodyEmpty() && error.body.fieldErrors !== undefined) {
            fieldErrors.set(error.body.fieldErrors);
            return;
          }
        } else if (error.status == 401) {
          // 認証エラーが発生した場合
          location.reload();
          return;
        }
      }
    });
}

function activateProfileImageChangeModal() {
  profileImageTrimmingManager.destroy();
  isProfileImageChangeModalActive.value = true;
}

function fileSelected(event) {
  const file = event.target.files[0];
  if (!file) return;

  profileImageTrimmingManager.setFile(file, trimmingContainer.value);
  event.target.value = ''; // 続けて同じファイルが選択された場合に change イベントを発火させるために必要
}
</script>

<style scoped>
@import 'croppie/croppie.css';
</style>
