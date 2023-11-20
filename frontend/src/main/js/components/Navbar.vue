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
          <p>
            <span
              class="button is-small is-rounded has-text-link-dark has-background-info-light py-0 px-3 mx-1"
              @click="isLoginModalActive = true"
            >
              <strong>ログイン</strong>
            </span>
            <span
              class="button is-small is-rounded has-text-info-light has-background-info-dark py-0 px-3 mx-1"
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
                class="notification has-background-white has-text-centered px-3 pt-3 pb-5"
              >
                <div class="has-text-right">
                  <button
                    class="delete"
                    type="button"
                    @click="isLoginModalActive = false"
                  ></button>
                </div>
                <div class="block m-0">
                  <p class="has-text-weight-bold has-text-link is-size-5 py-0">
                    ログイン
                  </p>
                </div>
                <div class="block is-inline-block m-3">
                  <p class="my-1">
                    <a
                      class="yahoo-color button is-danger has-text-white is-fullwidth is-small px-5"
                      href="/oauth2/authorization/yahoo"
                    >
                      <span
                        class="icon fas fa-lg has-text-danger is-flex is-align-items-center mr-2"
                      >
                        <img :src="yahoo_icon" alr="YahooIcon" />
                      </span>
                      <strong>Yahoo! JAPANでログイン</strong>
                    </a>
                  </p>
                  <p class="my-1">
                    <a
                      class="line-color button is-success has-text-white is-fullwidth is-small px-5"
                      href="/oauth2/authorization/line"
                    >
                      <span
                        class="icon fas fa-lg is-flex is-align-items-center mr-2"
                      >
                        <img :src="line_icon" alr="LineIcon" />
                      </span>
                      <strong>LINEでログイン</strong>
                    </a>
                  </p>
                  <p class="my-1">
                    <a
                      class="github-color button is-black has-text-white is-fullwidth is-small px-5"
                      href="/oauth2/authorization/github"
                    >
                      <span
                        class="icon fas fa-lg is-flex is-align-items-center mr-2"
                      >
                        <img :src="github_icon" alr="GitHubIcon" />
                      </span>
                      <strong>GitHubでログイン</strong>
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
                    アカウントをお持ちでない方はこちらから
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
                class="notification has-background-white has-text-centered px-3 pt-3 pb-5"
              >
                <div class="has-text-right">
                  <button
                    class="delete"
                    type="button"
                    @click="isAccountRegistrationModalActive = false"
                  ></button>
                </div>
                <div class="block m-0 py-0">
                  <p class="is-size-5 has-text-weight-bold has-text-link p-1">
                    お薬日記へようこそ！
                  </p>
                  <p class="has-text-grey-dark">
                    新規登録(無料)して利用を開始しましょう。
                  </p>
                </div>
                <div class="block is-inline-block m-3">
                  <p class="my-1">
                    <a
                      class="yahoo-color button is-danger has-text-white is-fullwidth is-small px-5"
                      href="/oauth2/authorization/yahoo"
                    >
                      <span
                        class="icon fas fa-lg has-text-danger is-flex is-align-items-center mr-2"
                      >
                        <img :src="yahoo_icon" alr="YahooIcon" />
                      </span>
                      <strong>Yahoo! JAPANでログイン</strong>
                    </a>
                  </p>
                  <p class="my-1">
                    <a
                      class="line-color button is-success has-text-white is-fullwidth is-small px-5"
                      href="/oauth2/authorization/line"
                    >
                      <span
                        class="icon fas fa-lg is-flex is-align-items-center mr-2"
                      >
                        <img :src="line_icon" alr="LineIcon" />
                      </span>
                      <strong>LINEでログイン</strong>
                    </a>
                  </p>
                  <p class="my-1">
                    <a
                      class="github-color button is-black has-text-white is-fullwidth is-small px-5"
                      href="/oauth2/authorization/github"
                    >
                      <span
                        class="icon fas fa-lg is-flex is-align-items-center mr-2"
                      >
                        <img :src="github_icon" alr="GitHubIcon" />
                      </span>
                      <strong>GitHubでログイン</strong>
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
import yahoo_icon from '@main/images/yahoo_icon.png';
import line_icon from '@main/images/line_icon.png';
import github_icon from '@main/images/github_icon.png';

const props = defineProps({ isAuthenticated: Boolean, csrf: String });

const isMenuOpened = ref(false);
const isAccountRegistrationModalActive = ref(false);
const isLoginModalActive = ref(false);
</script>

<style scoped>
.yahoo-color {
  background-color: #ff0033;
}

.line-color {
  background-color: #06c755;
}

.github-color {
  background-color: #24292f;
}
</style>
