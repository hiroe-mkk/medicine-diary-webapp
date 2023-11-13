<template>
  <nav id="navbar" class="navbar is-fixed-top has-background-info-dark">
    <div class="navbar-brand is-flex is-justify-content-space-between">
      <a class="navbar-item" href="/">
        <img :src="logo" alt="Logo" />
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
      <div class="navbar-end px-3">
        <div class="navbar-item py-0 mr-2" v-if="props.isAuthenticated">
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
        <div class="navbar-item py-0 mr-2" v-if="props.isAuthenticated">
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
        <div class="navbar-item py-0 mr-2" v-if="props.isAuthenticated">
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

        <div class="navbar-item" v-if="!props.isAuthenticated">
          <!-- TODO: Google でログイン ボタンに変更する -->
          <p>
            <span
              class="button is-small is-rounded has-text-link-dark has-background-info-light py-0 px-3 mx-1"
              @click="isLoginModalActive = true"
            >
              <strong>ログイン</strong>
            </span>
            <span
              class="button is-small is-rounded has-text-info-light has-background-link-dark py-0 px-3 mx-1"
              @click="isAccountRegistrationModalActive = true"
            >
              <strong>新規登録</strong>
            </span>
          </p>

          <div class="modal" :class="{ 'is-active': isLoginModalActive }">
            <div
              class="modal-background"
              @click="isLoginModalActive = false"
            ></div>
            <div class="modal-content">
              <div
                class="notification has-background-white has-text-centered py-3 px-5"
              >
                <div class="block m-2">
                  <img :src="logo_dark" alt="Logo" />
                  <p class="has-text-weight-bold has-text-grey is-size-5 p-2">
                    ログイン
                  </p>
                </div>
                <div class="block is-inline-block">
                  <p class="my-1">
                    <a
                      class="button is-fullwidth is-small px-5"
                      href="/oauth2/authorization/google"
                    >
                      <span
                        class="icon fas fa-lg is-flex is-align-items-center mr-0"
                      >
                        <i class="fa-brands fa-google"></i>
                      </span>
                      <strong>Googleでログイン</strong>
                    </a>
                  </p>
                  <p class="my-1">
                    <a
                      class="button is-fullwidth is-small px-5"
                      href="/oauth2/authorization/github"
                    >
                      <span
                        class="icon fas fa-lg is-flex is-align-items-center mr-0"
                      >
                        <i class="fa-brands fa-github"></i>
                      </span>
                      <strong>GitHubでログイン</strong>
                    </a>
                  </p>
                  <p class="my-1">
                    <a
                      class="button is-fullwidth is-small px-5"
                      href="/oauth2/authorization/line"
                    >
                      <span
                        class="icon fas fa-lg is-flex is-align-items-center mr-0"
                      >
                        <i class="has-text-success fa-brands fa-line"></i>
                      </span>
                      <strong>LINEでログイン</strong>
                    </a>
                  </p>
                </div>
                <div class="block is-size-7">
                  <p
                    class="has-text-weight-bold has-text-info is-clickable"
                    @click="
                      isAccountRegistrationModalActive = true;
                      isLoginModalActive = false;
                    "
                  >
                    新規登録
                  </p>
                </div>
              </div>
            </div>
          </div>

          <div
            class="modal"
            :class="{ 'is-active': isAccountRegistrationModalActive }"
          >
            <div
              class="modal-background"
              @click="isAccountRegistrationModalActive = false"
            ></div>
            <div class="modal-content">
              <div
                class="notification has-background-white has-text-centered py-3 px-5"
              >
                <div class="block m-2">
                  <img :src="logo_dark" alt="Logo" />
                  <p class="is-size-5 has-text-weight-bold has-text-link p-1">
                    お薬日記へようこそ！
                  </p>
                  <p class="has-text-grey-dark">
                    新規登録(無料)して利用を開始しましょう。
                  </p>
                </div>
                <div class="block is-inline-block">
                  <p class="my-1">
                    <a
                      class="button is-fullwidth is-small px-5"
                      href="/oauth2/authorization/google"
                    >
                      <span
                        class="icon fas fa-lg is-flex is-align-items-center mr-0"
                      >
                        <i class="fa-brands fa-google"></i>
                      </span>
                      <strong>Googleでログイン</strong>
                    </a>
                  </p>
                  <p class="my-1">
                    <a
                      class="button is-fullwidth is-small px-5"
                      href="/oauth2/authorization/github"
                    >
                      <span
                        class="icon fas fa-lg is-flex is-align-items-center mr-0"
                      >
                        <i class="fa-brands fa-github"></i>
                      </span>
                      <strong>GitHubでログイン</strong>
                    </a>
                  </p>
                  <p class="my-1">
                    <a
                      class="button is-fullwidth is-small px-5"
                      href="/oauth2/authorization/line"
                    >
                      <span
                        class="icon fas fa-lg is-flex is-align-items-center mr-0"
                      >
                        <i class="has-text-success fa-brands fa-line"></i>
                      </span>
                      <strong>LINEでログイン</strong>
                    </a>
                  </p>
                </div>
                <div class="block is-size-7">
                  <p
                    class="has-text-weight-bold has-text-info is-clickable"
                    @click="
                      isLoginModalActive = true;
                      isAccountRegistrationModalActive = false;
                    "
                  >
                    アカウントをお持ちの方はこちら
                  </p>
                </div>
                <div class="block is-size-7">
                  <a
                    class="has-text-weight-bold has-text-info"
                    href="/service/agreement"
                    >利用規約
                  </a>
                  に同意した上で、アカウント登録にお進みください。
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref } from 'vue';
import logo from '@main/images/logo_header.png';
import logo_dark from '@main/images/logo_header_dark.png';

const props = defineProps({ isAuthenticated: Boolean, csrf: String });

const isMenuOpened = ref(false);
const isAccountRegistrationModalActive = ref(false);
const isLoginModalActive = ref(false);
</script>
