<template>
  <nav id="navbar" class="navbar is-fixed-top has-background-info-dark">
    <div class="navbar-brand is-flex is-justify-content-space-between">
      <a class="navbar-item" :href="props.isAuthenticated ? '/mypage' : '/'">
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
        <div class="navbar-item py-0" v-if="props.isAuthenticated">
          <a class="has-text-info-light is-hidden-touch mr-2" href="/mypage">
            <span
              class="icon fas fa-2x is-flex is-align-items-center is-medium"
            >
              <i class="fa-solid fa-circle-user"></i>
            </span>
          </a>
          <a
            class="icon-text has-text-link-dark is-hidden-desktop mb-2"
            href="/mypage"
          >
            <span class="icon fas fa-lg is-flex is-align-items-center">
              <i class="fa-solid fa-circle-user"></i>
            </span>
            <strong>マイページ</strong>
          </a>
        </div>
        <div class="navbar-item py-0" v-if="props.isAuthenticated">
          <a class="has-text-info-light is-hidden-touch mr-2" href="/medicines">
            <span
              class="icon fas fa-2x is-flex is-align-items-center is-medium"
            >
              <i class="fa-solid fa-capsules"></i>
            </span>
          </a>
          <a
            class="icon-text has-text-link-dark is-hidden-desktop mb-2"
            href="/medicines"
          >
            <span class="icon fas fa-lg is-flex is-align-items-center">
              <i class="fa-solid fa-capsules"></i>
            </span>
            <strong>お薬一覧</strong>
          </a>
        </div>
        <div class="navbar-item py-0" v-if="props.isAuthenticated">
          <a class="has-text-info-light is-hidden-touch mr-2" href="/calendar">
            <span
              class="icon fas fa-2x is-flex is-align-items-center is-medium"
            >
              <i class="fa-solid fa-calendar-days"></i>
            </span>
          </a>
          <a
            class="icon-text has-text-link-dark is-hidden-desktop mb-2"
            href="/calendar"
          >
            <span class="icon fas fa-lg is-flex is-align-items-center">
              <i class="fa-solid fa-calendar-days"></i>
            </span>
            <strong>カレンダー</strong>
          </a>
        </div>
        <div class="navbar-item py-0" v-if="props.isAuthenticated">
          <a
            class="has-text-info-light is-hidden-touch mr-2"
            href="/medication-records"
          >
            <span
              class="icon fas fa-2x is-flex is-align-items-center is-medium"
            >
              <i class="fa-solid fa-book-open"></i>
            </span>
          </a>
          <a
            class="icon-text has-text-link-dark is-hidden-desktop mb-2"
            href="/medication-records"
          >
            <span class="icon fas fa-lg is-flex is-align-items-center">
              <i class="fa-solid fa-book-open"></i>
            </span>
            <strong>服用記録一覧</strong>
          </a>
        </div>
        <div class="navbar-item py-0" v-if="props.isAuthenticated">
          <a class="has-text-info-light is-hidden-touch mr-2" href="/about">
            <span
              class="icon fas fa-2x is-flex is-align-items-center is-medium"
            >
              <i class="fa-regular fa-circle-question"></i>
            </span>
          </a>
          <a
            class="icon-text has-text-link-dark is-hidden-desktop"
            href="/about"
          >
            <span class="icon fas fa-lg is-flex is-align-items-center">
              <i class="fa-regular fa-circle-question"></i>
            </span>
            <strong>お薬日記とは？</strong>
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
            class="button is-small is-rounded has-text-info-light has-background-info-dark py-0 px-3 mx-1"
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
