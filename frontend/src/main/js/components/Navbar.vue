<template>
  <nav id="navbar" class="navbar is-fixed-top has-background-info-dark">
    <div class="navbar-brand is-flex is-justify-content-space-between">
      <a class="navbar-item" href="/">
        <strong class="is-size-4 has-text-white">お薬日記</strong>
      </a>
      <p
        class="navbar-burger has-text-info-light"
        :class="{ 'is-active': isMenuOpened }"
        @click="isMenuOpened = !isMenuOpened"
      >
        <span></span>
        <span></span>
        <span></span>
      </p>
    </div>

    <div class="navbar-menu" :class="{ 'is-active': isMenuOpened }">
      <div class="navbar-end">
        <div
          class="navbar-item has-dropdown is-hoverable is-hidden-touch"
          v-if="props.isAuthenticated"
        >
          <a class="navbar-link has-background-white"> </a>

          <div class="navbar-dropdown is-right">
            <a class="navbar-item" href="/">
              <span class="has-text-link-dark pl-3 pr-0">
                <i class="fa-solid fa-circle-user"></i>
                <strong class="px-2">ホーム</strong>
              </span>
            </a>
            <a class="navbar-item" href="/medicines">
              <span class="has-text-link-dark pl-3 pr-0">
                <i class="fa-solid fa-capsules"></i>
                <strong class="px-2">お薬一覧</strong>
              </span>
            </a>
            <a class="navbar-item" href="/calendar">
              <span class="has-text-link-dark pl-3 pr-0">
                <i class="fa-solid fa-calendar-days"></i>
                <strong class="px-2">カレンダー</strong>
              </span>
            </a>
            <a class="navbar-item" href="/setting">
              <span class="has-text-link-dark pl-3 pr-0">
                <i class="fa-solid fa-gear"></i>
                <strong class="px-2">設定</strong>
              </span>
            </a>
            <a class="navbar-item" href="/about">
              <span class="has-text-link-dark pl-3 pr-0">
                <i class="fa-regular fa-circle-question"></i>
                <strong class="px-2">お薬日記とは？</strong>
              </span>
            </a>
          </div>
        </div>

        <div
          class="navbar-item is-hidden-desktop py-1 px-5 mt-2"
          v-if="props.isAuthenticated"
        >
          <a class="has-text-link-dark" href="/">
            <i class="fa-solid fa-house-chimney"></i>
            <strong class="px-2">ホーム</strong>
          </a>
        </div>
        <div
          class="navbar-item is-hidden-desktop py-1 px-5"
          v-if="props.isAuthenticated"
        >
          <a class="has-text-link-dark is-hidden-desktop" href="/medicines">
            <i class="fa-solid fa-capsules"></i>
            <strong class="px-2">お薬一覧</strong>
          </a>
        </div>
        <div
          class="navbar-item is-hidden-desktop py-1 px-5"
          v-if="props.isAuthenticated"
        >
          <a class="has-text-link-dark" href="/calendar">
            <i class="fa-solid fa-calendar-days"></i>
            <strong class="px-2">カレンダー</strong>
          </a>
        </div>
        <div
          class="navbar-item is-hidden-desktop py-1 px-5"
          v-if="props.isAuthenticated"
        >
          <a class="has-text-link-dark" href="/setting">
            <i class="fa-solid fa-gear"></i>
            <strong class="px-2">設定</strong>
          </a>
        </div>
        <div
          class="navbar-item is-hidden-desktop py-1 px-5 mb-2"
          v-if="props.isAuthenticated"
        >
          <a class="has-text-link-dark" href="/about">
            <i class="fa-regular fa-circle-question"></i>
            <strong class="px-2">お薬日記とは？</strong>
          </a>
        </div>

        <div class="navbar-item" v-if="!props.isAuthenticated">
          <Authentication ref="authentication"></Authentication>
          <p
            class="button is-small is-rounded has-text-link-dark has-background-info-light py-0 px-3 mx-1"
            @click="activateLoginModal()"
          >
            <strong>ログイン</strong>
          </p>
          <p
            class="button is-small is-rounded has-text-info-light has-background-link-dark py-0 px-3 mx-1"
            @click="activateAccountRegistrationModal()"
          >
            <strong>新規登録</strong>
          </p>
        </div>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref } from 'vue';
import Authentication from '@main/js/components/Authentication.vue';

const props = defineProps({ isAuthenticated: Boolean, csrf: String });
const isMenuOpened = ref(false);
const authentication = ref(null);

function activateLoginModal() {
  authentication.value.activateLoginModal();
}
function activateAccountRegistrationModal() {
  authentication.value.activateAccountRegistrationModal();
}
</script>