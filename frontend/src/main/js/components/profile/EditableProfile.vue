<template>
  <div class="content has-text-centered">
    <div class="tile is-ancestor">
      <div class="tile is-parent is-3 p-0">
        <div class="tile is-child">
          <ChangeableImage
            ref="changeableImage"
            :image="props.profileImage"
            :csrf="props.csrf"
            executePath="/api/profile/profileimage/change"
            imageName="プロフィール画像"
            :noImage="noProfileImage"
            :isRounded="true"
            :isFixedSize="true"
          ></ChangeableImage>
        </div>
      </div>
      <div class="tile my-1">
        <div class="tile is-parent is-vertical">
          <div class="tile is-child is-hidden-mobile">
            <div class="icon-text has-text-link-dark">
              <strong
                class="is-size-4 has-text-grey-dark"
                v-if="username !== undefined"
              >
                {{ username }}
              </strong>
              <strong
                class="is-size-5 has-text-grey"
                v-if="username === undefined"
              >
                ( unknown )
              </strong>
              <span
                class="icon fas fa-lg is-flex is-clickable"
                @click="isMenuModalActive = true"
              >
                <i class="fa-solid fa-gear"></i>
              </span>
            </div>
          </div>
          <div
            class="tile is-child is-hidden-tablet is-flex is-justify-content-center"
          >
            <div class="icon-text has-text-link-dark">
              <strong
                class="is-size-4 has-text-grey-dark"
                v-if="username !== undefined"
              >
                {{ username }}
              </strong>
              <strong
                class="is-size-5 has-text-grey"
                v-if="username === undefined"
              >
                ( unknown )
              </strong>
              <span
                class="icon fas fa-lg is-flex is-clickable"
                @click="isMenuModalActive = true"
              >
                <i class="fa-solid fa-gear"></i>
              </span>
            </div>
          </div>
          <div class="tile is-child">
            <div class="notification has-background-white-bis"></div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <div class="modal" :class="{ 'is-active': isMenuModalActive }">
    <div class="modal-background" @click="isMenuModalActive = false"></div>
    <div class="modal-content is-flex is-justify-content-center">
      <div class="content">
        <button
          type="button"
          class="button is-dark is-fullwidth is-small mb-1"
          @click="activateUsernameChangeModal()"
        >
          <strong class="mx-6">ユーザー名を変更する</strong>
        </button>
        <div
          class="button is-dark is-fullwidth is-small mb-1"
          @click="activateProfileImageChangeModal()"
        >
          <strong class="mx-6">プロフィール画像を変更する</strong>
        </div>
        <form method="post" action="/logout">
          <input name="_csrf" :value="props.csrf" hidden />
          <button class="button is-dark is-fullwidth is-small mb-1">
            <strong class="mx-6">ログアウト</strong>
          </button>
        </form>
        <button
          type="button"
          class="button is-dark is-fullwidth is-small has-text-danger mb-1"
          @click="isMenuModalActive = false"
        >
          <strong class="mx-6">キャンセル</strong>
        </button>
      </div>
    </div>
  </div>

  <div class="modal" :class="{ 'is-active': isUsernameChangeModalActive }">
    <div
      class="modal-background"
      @click="isUsernameChangeModalActive = false"
    ></div>
    <div class="modal-content">
      <div class="notification py-3 px-5 is-white">
        <div class="has-text-right">
          <button
            class="delete"
            type="button"
            @click="isUsernameChangeModalActive = false"
          ></button>
        </div>
        <div
          class="is-size-5 has-text-weight-bold has-text-link-dark has-text-centered"
        >
          ユーザー名を変更する
        </div>
        <form
          class="form"
          method="post"
          @submit.prevent="submitUsernameChangeForm()"
        >
          <div class="field m-3">
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
            <p
              class="help is-danger"
              v-for="error in fieldErrors.get('username')"
            >
              {{ error }}
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
      </div>
    </div>
  </div>

  <ResultMessage ref="resultMessage"></ResultMessage>
</template>
<script setup>
import { ref, reactive } from 'vue';
import {
  HttpRequestClient,
  HttpRequestFailedError,
} from '@main/js/composables/HttpRequestClient.js';
import { FieldErrors } from '@main/js/composables/model/FieldErrors.js';
import ResultMessage from '@main/js/components/ResultMessage.vue';
import ChangeableImage from '@main/js/components/ChangeableImage.vue';
import noProfileImage from '@main/images/no_profile_image.png';

const props = defineProps({
  username: String,
  profileImage: String,
  csrf: String,
});

const isMenuModalActive = ref(false);

const username = ref(props.username);
const isUsernameChangeModalActive = ref(false);
const editingUsername = ref('');

const fieldErrors = reactive(new FieldErrors());
const resultMessage = ref(null);

const changeableImage = ref(null);

function activateUsernameChangeModal() {
  fieldErrors.clear();
  editingUsername.value = username.value;
  isUsernameChangeModalActive.value = true;
  isMenuModalActive.value = false;
}

function submitUsernameChangeForm() {
  if (username.value == editingUsername.value) {
    changeUsernameCompleted();
    return;
  }

  const form = new URLSearchParams();
  form.set('username', editingUsername.value);
  form.set('_csrf', props.csrf);

  HttpRequestClient.submitPostRequest('/api/profile/username/change', form)
    .then(() => {
      changeUsernameCompleted();
      return;
    })
    .catch((error) => {
      if (error instanceof HttpRequestFailedError) {
        if (error.status == 400) {
          // バインドエラーが発生した場合
          if (!error.isBodyEmpty() && error.body.fieldErrors !== undefined) {
            fieldErrors.set(error.body.fieldErrors);
            return;
          }
        } else if (error.status == 409) {
          resultMessage.value.activate(
            'ERROR',
            'ユーザー名の変更に失敗しました。',
            error.getMessage()
          );
          return;
        } else if (error.status == 401) {
          // 認証エラーが発生した場合
          location.reload();
          return;
        } else if (error.status == 500) {
          resultMessage.value.activate(
            'ERROR',
            'システムエラーが発生しました。',
            'お手数ですが、再度お試しください。'
          );
          return;
        } else if (error.hasMessage()) {
          resultMessage.value.activate(
            'ERROR',
            'エラーが発生しました。',
            error.getMessage()
          );
          return;
        }
      }

      resultMessage.value.activate(
        'ERROR',
        'エラーが発生しました。',
        '通信状態をご確認のうえ、再度お試しください。'
      );
    });
}

function changeUsernameCompleted() {
  username.value = editingUsername.value;
  isUsernameChangeModalActive.value = false;
  resultMessage.value.activate('INFO', 'ユーザー名の変更が完了しました。');
}

function activateProfileImageChangeModal() {
  changeableImage.value.activateChangeModal();
  isMenuModalActive.value = false;
}
</script>