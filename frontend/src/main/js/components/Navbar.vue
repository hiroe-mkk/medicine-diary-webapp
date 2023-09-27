<template>
  <nav id="navbar" class="navbar is-fixed-top has-background-link-dark">
    <div class="navbar-brand is-flex is-justify-content-space-between">
      <span
        class="navbar-burger has-text-white"
        :class="{ 'is-active': isOpened }"
        @click="isOpened = !isOpened"
      >
        <span></span>
        <span></span>
        <span></span>
      </span>
    </div>

    <div class="navbar-menu" :class="{ 'is-active': isOpened }">
      <div class="navbar-end px-3">
        <div class="navbar-item pb-0 mr-2" v-if="props.isAuthenticated">
          <a class="has-text-white is-hidden-touch" href="/mypage">
            <span
              class="icon is-medium fas fa-2x is-flex is-align-items-center"
            >
              <i class="fa-solid fa-circle-user"></i>
            </span>
          </a>
          <a
            class="icon-text has-text-link-dark is-hidden-desktop"
            href="/mypage"
          >
            <span class="icon fas fa-lg is-flex is-align-items-center">
              <i class="fa-solid fa-circle-user"></i>
            </span>
            <strong>マイページ</strong>
          </a>
        </div>
        <div class="navbar-item pb-0 mr-3" v-if="props.isAuthenticated">
          <a
            class="has-text-white is-hidden-touch"
            @click="isSettingModalActive = true"
          >
            <span
              class="icon is-medium fas fa-2x is-flex is-align-items-center"
            >
              <i class="fa-solid fa-gear"></i>
            </span>
          </a>
          <a
            class="icon-text has-text-link-dark is-hidden-desktop"
            @click="isSettingModalActive = true"
          >
            <span class="icon fas fa-lg is-flex is-align-items-center">
              <i class="fa-solid fa-gear"></i>
            </span>
            <strong>設定</strong>
          </a>
        </div>
        <div class="navbar-item pb-0" v-if="!props.isAuthenticated">
          <!-- TODO: Google でログイン ボタンに変更する -->
          <a
            class="button is-small is-rounded has-text-link-dark has-background-white is-hidden-touch"
            href="/oauth2/authorization/google"
          >
            <strong>ログイン</strong>
          </a>
          <a
            class="button is-small is-rounded has-text-white has-background-link-dark is-hidden-desktop"
            href="/oauth2/authorization/google"
          >
            <strong>ログイン</strong>
          </a>
        </div>

        <div class="navbar-item pb-0 mr-3" v-if="props.isAuthenticated">
          <!-- TODO: 移動する -->
          <form method="post" action="/logout">
            <input name="_csrf" :value="props.csrf" hidden />
            <button
              class="button is-small is-rounded has-text-link-dark has-background-white is-hidden-touch"
            >
              <strong>ログアウト</strong>
            </button>
            <button
              class="button is-small is-rounded has-text-white has-background-link-dark is-hidden-desktop"
            >
              <strong>ログアウト</strong>
            </button>
          </form>
        </div>
      </div>
    </div>
  </nav>

  <div
    class="modal"
    :class="{ 'is-active': isSettingModalActive }"
    v-if="props.isAuthenticated"
  >
    <div class="modal-background" @click="isSettingModalActive = false"></div>
    <div class="modal-content is-flex is-justify-content-center">
      <div class="content">
        <a
          class="button is-dark is-fullwidth is-small mb-1"
          href="/profile/edit"
        >
          <strong class="mx-6">プロフィール編集</strong>
        </a>
        <button
          type="button"
          class="button is-dark is-fullwidth is-small has-text-danger mb-1"
          @click="isSettingModalActive = false"
        >
          <strong class="mx-6">キャンセル</strong>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const props = defineProps({ isAuthenticated: Boolean, csrf: String });

const isOpened = ref(false);
const isSettingModalActive = ref(false);
</script>
