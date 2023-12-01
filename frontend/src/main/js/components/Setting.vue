<template>
  <div class="content has-text-centered">
    <ChangeableImage
      ref="changeableImage"
      :image="props.profileImage"
      :csrf="props.csrf"
      executeRootPath="/api/profile/image"
      imageName="プロフィール画像"
      :noImage="noProfileImage"
      :isRounded="true"
      :isFixedSize="true"
    ></ChangeableImage>
    <p
      class="is-size-4 has-text-weight-bold has-text-grey-dark m-0"
      v-if="username !== undefined"
    >
      {{ username }}
    </p>
    <p
      class="is-size-5 has-text-weight-bold has-text-grey m-0"
      v-if="username === undefined"
    >
      ( unknown )
    </p>
  </div>

  <div class="container is-max-desktop p-3">
    <div class="panel is-white">
      <div class="panel-heading py-1 my-0"></div>
      <div
        class="panel-block has-background-white is-flex is-justify-content-space-between is-clickable"
        @click="activateUsernameChangeModal()"
      >
        <span class="icon-text">
          <span class="icon has-text-link ml-3">
            <i class="fa-solid fa-pen"></i>
          </span>
          <strong>ユーザー名変更</strong>
        </span>
      </div>
      <div
        class="panel-block has-background-white is-flex is-justify-content-space-between is-clickable"
        @click="activateProfileImageMenuModal()"
      >
        <span class="icon-text">
          <span class="icon has-text-link ml-3">
            <i class="fa-solid fa-image"></i>
          </span>
          <strong>プロフィール画像変更</strong>
        </span>
      </div>
      <a
        class="panel-block has-background-white is-flex is-justify-content-space-between is-clickable"
        href="/shared-group/management"
      >
        <span class="icon-text">
          <span class="icon has-text-link ml-3">
            <i class="fa-solid fa-user-group"></i>
          </span>
          <strong>共有グループ管理</strong>
        </span>
      </a>
      <form
        method="post"
        action="/logout"
        class="panel-block has-background-white is-flex is-justify-content-space-between is-clickable"
      >
        <input name="_csrf" :value="props.csrf" hidden />
        <button
          class="has-background-white is-size-6 p-0 is-clickable"
          style="border: none"
        >
          <span class="icon-text">
            <span class="icon has-text-link ml-3">
              <i class="fa-solid fa-right-from-bracket"></i>
            </span>
            <strong class="has-text-grey-dark">ログアウト</strong>
          </span>
        </button>
      </form>
      <div
        class="panel-block has-background-white is-flex is-justify-content-space-between is-clickable"
        @click="activateAccountDeletionConfirmationModal()"
      >
        <span class="icon-text">
          <span class="icon has-text-danger ml-3">
            <i class="fa-solid fa-triangle-exclamation"></i>
          </span>
          <strong>アカウント削除</strong>
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
      <div class="notification has-background-white py-3 px-5">
        <div class="has-text-right">
          <button
            class="delete"
            type="button"
            @click="isUsernameChangeModalActive = false"
          ></button>
        </div>
        <p
          class="is-size-5 has-text-weight-bold has-text-link has-text-centered"
        >
          ユーザー名を変更する
        </p>
        <form
          class="form"
          method="post"
          @submit.prevent="submitUsernameChangeForm()"
        >
          <div class="field my-3">
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
          <div class="field is-grouped is-grouped-centered p-2">
            <p class="control">
              <button
                class="button is-small is-rounded is-link"
                :disabled="username === editingUsername"
              >
                完了
              </button>
            </p>
            <p class="control">
              <button
                type="button"
                class="button is-small is-rounded is-outlined is-danger"
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

  <ConfirmationMessage
    ref="accountDeletionConfirmationModal"
    message="アカウントを削除しますか？"
    details="アカウントを削除すると、全てのデータが削除されます。
                  削除後、データを復元することはできません。"
    button-label="削除する"
    path="/account/delete"
    th:attr="csrf=${_csrf.token}"
  >
  </ConfirmationMessage>
  <ResultMessage ref="resultMessage"></ResultMessage>
</template>
<script setup>
import { ref, reactive } from 'vue';
import {
  HttpRequestClient,
  HttpRequestFailedError,
} from '@main/js/composables/HttpRequestClient.js';
import { FieldErrors } from '@main/js/composables/model/FieldErrors.js';
import ChangeableImage from '@main/js/components/ChangeableImage.vue';
import ConfirmationMessage from '@main/js/components/ConfirmationMessage.vue';
import ResultMessage from '@main/js/components/ResultMessage.vue';
import noProfileImage from '@main/images/no_profile_image.png';

const props = defineProps({
  username: String,
  profileImage: String,
  csrf: String,
});

const username = ref(props.username);
const isUsernameChangeModalActive = ref(false);
const editingUsername = ref('');

const changeableImage = ref(null);

const fieldErrors = reactive(new FieldErrors());
const resultMessage = ref(null);
const accountDeletionConfirmationModal = ref(null);

function activateUsernameChangeModal() {
  fieldErrors.clear();
  editingUsername.value = username.value;
  isUsernameChangeModalActive.value = true;
}

function submitUsernameChangeForm() {
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
        } else if (error.status == 401) {
          // 認証エラーが発生した場合
          location.reload();
          return;
        } else if (error.status == 409) {
          resultMessage.value.activate(
            'ERROR',
            'エラーが発生しました。',
            error.getMessage()
          );
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

function activateProfileImageMenuModal() {
  changeableImage.value.activateMenuModal();
}

function activateAccountDeletionConfirmationModal() {
  accountDeletionConfirmationModal.value.activate();
}
</script>
