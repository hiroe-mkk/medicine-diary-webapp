<template>
  <div class="is-flex is-justify-content-center">
    <figure class="image m-2" :class="{ 'is-128x128': isFixedSize }">
      <img
        :src="image"
        :class="{ 'is-rounded': isRounded }"
        v-if="image != undefined"
      />
      <img
        :src="noImage"
        :class="{ 'is-rounded': isRounded }"
        v-if="image == undefined"
      />
    </figure>
  </div>

  <div class="modal" :class="{ 'is-active': isImageChangeModalActive }">
    <div
      class="modal-background"
      @click="isImageChangeModalActive = false"
    ></div>
    <div class="modal-content">
      <div
        class="notification has-background-white has-text-centered py-3 px-5"
      >
        <div class="has-text-right">
          <button
            class="delete"
            type="button"
            @click="isImageChangeModalActive = false"
          ></button>
        </div>
        <div
          class="is-size-5 has-text-weight-bold has-text-link has-text-centered"
        >
          {{ `${props.imageName}を変更する` }}
        </div>
        <div class="content has-text-centered m-3">
          <div
            class="is-flex is-justify-content-center"
            v-if="!imageTrimmingManager.isTrimming && image !== undefined"
          >
            <figure class="image is-128x128 m-0">
              <img :src="image" />
            </figure>
          </div>
          <div
            ref="trimmingContainer"
            v-show="imageTrimmingManager.isTrimming"
          ></div>
          <p
            class="help is-danger"
            v-for="error in fieldErrors.get('profileImage')"
          >
            {{ error }}
          </p>
        </div>

        <form
          class="form"
          method="post"
          enctype="multipart/form-data"
          @submit.prevent="submitForm()"
        >
          <div class="file is-small is-info is-centered p-2">
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
            class="field is-grouped is-grouped-centered pb-2"
            v-show="imageTrimmingManager.isTrimming"
          >
            <p class="control">
              <button class="button is-small is-rounded is-link">完了</button>
            </p>
            <p class="control">
              <button
                type="button"
                class="button is-small is-rounded is-outlined is-danger"
                @click="isImageChangeModalActive = false"
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
import { ImageTrimmingManager } from '@main/js/composables/model/ImageTrimmingManager.js';

const props = defineProps({
  image: String,
  csrf: String,
  executePath: String,
  imageName: String,
  noImage: String,
  isRounded: { type: Boolean, default: false },
  isFixedSize: { type: Boolean, default: false },
});

defineExpose({ activateChangeModal });

const image = ref(props.image);
const isImageChangeModalActive = ref(false);
const trimmingContainer = ref(null);
const imageTrimmingManager = reactive(new ImageTrimmingManager());

const fieldErrors = reactive(new FieldErrors());
const resultMessage = ref(null);

function activateChangeModal() {
  fieldErrors.clear();
  imageTrimmingManager.destroy();
  isImageChangeModalActive.value = true;
}

function fileSelected(event) {
  const file = event.target.files[0];
  if (!file) return;

  imageTrimmingManager.setFile(file, trimmingContainer.value);
  event.target.value = ''; // 続けて同じファイルが選択された場合に change イベントを発火させるために必要
}

async function submitForm() {
  const result = await imageTrimmingManager.result();
  const form = new FormData();
  form.set('image', result);
  form.set('_csrf', props.csrf);

  HttpRequestClient.submitPostRequest(props.executePath, form)
    .then(() => {
      image.value = URL.createObjectURL(result);
      isImageChangeModalActive.value = false;
      resultMessage.value.activate(
        'INFO',
        `${props.imageName}の変更が完了しました。`
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