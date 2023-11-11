<template>
  <nav id="navbar" class="navbar is-fixed-top has-background-info-dark">
    <div class="navbar-brand is-flex is-justify-content-space-between">
      <a class="navbar-item" href="/">
        <img :src="logo" alt="Logo" />
      </a>
      <p
        class="navbar-burger has-text-info-light"
        :class="{ 'is-active': isOpened }"
        @click="isOpened = !isOpened"
      >
        <span></span>
        <span></span>
        <span></span>
      </p>
    </div>

    <div class="navbar-menu" :class="{ 'is-active': isOpened }">
      <div class="navbar-end px-3">
        <div class="navbar-item pb-0 mr-2" v-if="props.isAuthenticated">
          <a class="has-text-info-light is-hidden-touch" href="/mypage">
            <span
              class="icon fas fa-2x is-flex is-align-items-center is-medium"
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
        <div class="navbar-item pb-0 mr-2" v-if="props.isAuthenticated">
          <a class="has-text-info-light is-hidden-touch" href="/medicines">
            <span
              class="icon fas fa-2x is-flex is-align-items-center is-medium"
            >
              <i class="fa-solid fa-capsules"></i>
            </span>
          </a>
          <a
            class="icon-text has-text-link-dark is-hidden-desktop"
            href="/medicines"
          >
            <span class="icon fas fa-lg is-flex is-align-items-center">
              <i class="fa-solid fa-capsules"></i>
            </span>
            <strong>お薬一覧</strong>
          </a>
        </div>
        <div class="navbar-item pb-0 mr-2" v-if="props.isAuthenticated">
          <a class="has-text-info-light is-hidden-touch" href="/takingrecords">
            <span
              class="icon fas fa-2x is-flex is-align-items-center is-medium"
            >
              <i class="fa-solid fa-book-open"></i>
            </span>
          </a>
          <a
            class="icon-text has-text-link-dark is-hidden-desktop"
            href="/takingrecords"
          >
            <span class="icon fas fa-lg is-flex is-align-items-center">
              <i class="fa-solid fa-book-open"></i>
            </span>
            <strong>服用記録一覧</strong>
          </a>
        </div>
        <div class="navbar-item pb-0" v-if="!props.isAuthenticated">
          <!-- TODO: Google でログイン ボタンに変更する -->
          <a
            class="button is-small is-rounded has-text-link-dark has-background-info-light is-hidden-touch"
            href="/oauth2/authorization/google"
          >
            <strong>ログイン</strong>
          </a>
          <a
            class="button is-small is-rounded has-text-white has-background-info-dark is-hidden-desktop"
            href="/oauth2/authorization/google"
          >
            <strong>ログイン</strong>
          </a>
        </div>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref } from 'vue';
import logo from '@main/images/logo_header.png';

const props = defineProps({ isAuthenticated: Boolean, csrf: String });

const isOpened = ref(false);
</script>
