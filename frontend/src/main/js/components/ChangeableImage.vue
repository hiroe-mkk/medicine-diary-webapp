<template>
  <div class="is-flex is-justify-content-center">
    <figure class="image is-128x128" v-if="profileImage.url !== ''">
      <img class="is-rounded" :src="profileImage.url" />
    </figure>
  </div>

  <div class="modal" :class="{ 'is-active': isProfileImageChangeModalActive }">
    <div
      class="modal-background"
      @click="isProfileImageChangeModalActive = false"
    ></div>
    <div class="modal-content">
      <div class="notification p-3 has-background-white-bis has-text-centered">
        <div class="is-size-5 has-text-link-dark has-text-centered">
          プロフィール画像を変更する
        </div>
        <form
          class="form"
          method="post"
          enctype="multipart/form-data"
          @submit.prevent="submitProfileImageChangeForm()"
        >
          <div class="container m-3" ref="trimmingContainer"></div>
          <p
            class="help is-danger"
            v-for="error in fieldErrors.get('profileImage')"
          >
            {{ error }}
          </p>
          <div
            class="file m-3 is-small is-info is-centered"
            v-show="!profileImageTrimmingManager.isTrimming"
          >
            <label class="file-label">
              <input
                class="file-input"
                type="file"
                accept="image/*"
                @change="fileSelected($event)"
              />
              <span class="file-cta">
                <span class="file-label is-rounded">ファイルを選択する </span>
              </span>
            </label>
          </div>
          <div
            class="field is-grouped is-grouped-centered"
            v-show="profileImageTrimmingManager.isTrimming"
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
import { ref, reactive, defineExpose } from 'vue';
import {
  HttpRequestClient,
  HttpRequestFailedError,
} from '@main/js/composables/HttpRequestClient.js';
import { FieldErrors } from '@main/js/composables/model/FieldErrors.js';
import ResultMessage from '@main/js/components/ResultMessage.vue';
import { ProfileImageTrimmingManager } from '@main/js/composables/model/ProfileImageTrimmingManager.js';

const props = defineProps({
  profileImage: String,
  csrf: String,
});

defineExpose({ activate });

const profileImage = reactive({ url: props.profileImage });
const isProfileImageChangeModalActive = ref(false);
const trimmingContainer = ref(null);
const profileImageTrimmingManager = reactive(new ProfileImageTrimmingManager());

const fieldErrors = reactive(new FieldErrors());
const resultMessage = ref(null);

function activate() {
  fieldErrors.clear();
  profileImageTrimmingManager.destroy();
  isProfileImageChangeModalActive.value = true;
}

function fileSelected(event) {
  const file = event.target.files[0];
  if (!file) return;

  profileImageTrimmingManager.setFile(file, trimmingContainer.value);
  event.target.value = ''; // 続けて同じファイルが選択された場合に change イベントを発火させるために必要
}

async function submitProfileImageChangeForm() {
  const result = await profileImageTrimmingManager.result();
  const form = new FormData();
  form.set('profileImage', result);
  form.set('_csrf', props.csrf);

  HttpRequestClient.submitPostRequest('/api/profile/profileimage/change', form)
    .then(() => {
      profileImage.url = URL.createObjectURL(result);
      isProfileImageChangeModalActive.value = false;
      resultMessage.value.activate(
        'INFO',
        'プロフィール画像の変更が完了しました。'
      );
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
</script>

<style scoped>
@import 'croppie/croppie.css';
</style>
