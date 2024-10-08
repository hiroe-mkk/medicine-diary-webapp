<template>
  <div class="is-flex is-justify-content-center">
    <figure
      class="image is-clickable m-2"
      :class="{ 'is-128x128': isFixedSize }"
      @click="activateMenuModal()"
    >
      <img
        :src="image"
        alt=""
        :class="{ 'is-rounded': isRounded }"
        v-if="image != undefined"
      />
      <img
        :src="noImage"
        alt=""
        :class="{ 'is-rounded': isRounded }"
        v-if="image == undefined"
      />
    </figure>
  </div>

  <div class="modal" :class="{ 'is-active': isMenuModalActive }">
    <div class="modal-background" @click="isMenuModalActive = false"></div>
    <div class="modal-content is-flex is-justify-content-center">
      <div class="content">
        <button
          type="button"
          class="button is-dark is-fullwidth is-small mb-1"
          @click="activateTrimmingModal()"
        >
          <strong class="mx-6">画像を変更する</strong>
        </button>
        <button
          class="button is-dark is-fullwidth is-small mb-1"
          v-if="image != undefined"
          @click="deleteImage()"
        >
          <strong class="mx-6">現在の画像を削除する</strong>
        </button>
        <button
          type="button"
          class="button is-dark is-fullwidth is-small has-text-danger-dark mb-1"
          @click="isMenuModalActive = false"
        >
          <strong class="mx-6">キャンセル</strong>
        </button>
      </div>
    </div>
  </div>

  <div class="modal" :class="{ 'is-active': isTrimmingModalActive }">
    <div class="modal-background" @click="isTrimmingModalActive = false"></div>
    <div class="modal-content">
      <div
        class="notification has-background-white has-text-centered py-3 px-5"
      >
        <div class="has-text-right">
          <button
            class="delete"
            type="button"
            @click="isTrimmingModalActive = false"
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
              <img :src="image" alt="" />
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
                @click="isTrimmingModalActive = false"
              >
                キャンセル
              </button>
            </p>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { HttpRequestClient } from '@main/js/composables/HttpRequestClient.js';
import { FieldErrors } from '@main/js/composables/model/FieldErrors.js';
import { ImageTrimmingManager } from '@main/js/composables/model/ImageTrimmingManager.js';
import { defineExpose, inject, reactive, ref } from 'vue';

const props = defineProps({
  image: String,
  csrf: String,
  executeRootPath: String,
  imageName: String,
  noImage: String,
  isRounded: { type: Boolean, default: false },
  isFixedSize: { type: Boolean, default: false },
});
defineExpose({ activateMenuModal });
const activateResultMessage = inject('activateResultMessage');

const image = ref(props.image);
const isMenuModalActive = ref(false);
const isTrimmingModalActive = ref(false);
const trimmingContainer = ref(null);
const imageTrimmingManager = reactive(new ImageTrimmingManager());

const fieldErrors = reactive(new FieldErrors());

function activateMenuModal() {
  if (image.value === undefined) {
    activateTrimmingModal();
  } else {
    isMenuModalActive.value = true;
  }
}

function activateTrimmingModal() {
  fieldErrors.clear();
  imageTrimmingManager.destroy();
  isTrimmingModalActive.value = true;
}

function fileSelected(event) {
  const file = event.target.files[0];
  event.target.value = ''; // 続けて同じファイルが選択された場合に change イベントを発火させるために必要

  if (!file) return;
  if (!file.type.startsWith('image/')) {
    activateResultMessage(
      'ERROR',
      'アップロードに失敗しました。',
      '画像形式のファイルを選択してください。'
    );
  } else {
    imageTrimmingManager.setFile(file, trimmingContainer.value);
  }
}

async function submitForm() {
  fieldErrors.clear();

  const result = await imageTrimmingManager.result();
  const form = new FormData();
  form.set('image', result);
  form.set('_csrf', props.csrf);

  HttpRequestClient.submitPostRequest(
    `${props.executeRootPath}/change`,
    form,
    activateResultMessage,
    fieldErrors
  ).then(() => {
    image.value = URL.createObjectURL(result);
    isTrimmingModalActive.value = false;
    isMenuModalActive.value = false;
    activateResultMessage('INFO', `${props.imageName}の変更が完了しました。`);
  });
}

function deleteImage() {
  const form = new FormData();
  form.set('_csrf', props.csrf);

  HttpRequestClient.submitPostRequest(
    `${props.executeRootPath}/delete`,
    form,
    activateResultMessage
  ).then(() => {
    activateResultMessage('INFO', `画像の削除が完了しました。`);
    isMenuModalActive.value = false;
    image.value = undefined;
  });
}
</script>

<style scoped>
@import 'croppie/croppie.css';
</style>
