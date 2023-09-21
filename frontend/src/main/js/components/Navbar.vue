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
      <div class="navbar-end">
        <div class="navbar-item pb-0 mr-3">
          <!-- Google でログイン ボタンに変更する -->
          <a
            class="button is-small is-rounded has-text-link-dark has-background-white is-hidden-touch"
            href="/oauth2/authorization/google"
          >
            <span class="icon fas fa-lg is-flex is-align-items-center">
              <i class="fa-solid fa-right-to-bracket"></i>
            </span>
            <strong>ログイン</strong>
          </a>
          <a
            class="button is-small is-rounded has-text-white has-background-link-dark is-hidden-desktop"
            href="/oauth2/authorization/google"
          >
            <span class="icon fas is-flex is-align-items-center">
              <i class="fa-solid fa-right-to-bracket"></i>
            </span>
            <strong>ログイン</strong>
          </a>
        </div>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref } from 'vue';

const isOpened = ref(false);
</script>