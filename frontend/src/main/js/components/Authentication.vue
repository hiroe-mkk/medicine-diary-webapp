<template>
  <div class="modal" :class="{ 'is-active': isLoginModalActive }">
    <div class="modal-background" @click="isLoginModalActive = false"></div>
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
                <img src="@main/images/yahoo_icon.png" alt="" />
              </span>
              <strong>Yahoo! JAPANでログイン</strong>
            </a>
          </p>
          <p class="my-1">
            <a
              class="line-color button is-success has-text-white is-fullwidth is-small px-5"
              href="/oauth2/authorization/line"
            >
              <span class="icon fas fa-lg is-flex is-align-items-center mr-2">
                <img src="@main/images/line_icon.png" alt="" />
              </span>
              <strong>LINEでログイン</strong>
            </a>
          </p>
          <p class="my-1">
            <a
              class="github-color button is-black has-text-white is-fullwidth is-small px-5"
              href="/oauth2/authorization/github"
            >
              <span class="icon fas fa-lg is-flex is-align-items-center mr-2">
                <img src="@main/images/github_icon.png" alt="" />
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

  <div class="modal" :class="{ 'is-active': isAccountRegistrationModalActive }">
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
          <p class="has-text-grey-dark has-text-weight-semibold">
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
                <img src="@main/images/yahoo_icon.png" alt="" />
              </span>
              <strong>Yahoo! JAPANでログイン</strong>
            </a>
          </p>
          <p class="my-1">
            <a
              class="line-color button is-success has-text-white is-fullwidth is-small px-5"
              href="/oauth2/authorization/line"
            >
              <span class="icon fas fa-lg is-flex is-align-items-center mr-2">
                <img src="@main/images/line_icon.png" alt="" />
              </span>
              <strong>LINEでログイン</strong>
            </a>
          </p>
          <p class="my-1">
            <a
              class="github-color button is-black has-text-white is-fullwidth is-small px-5"
              href="/oauth2/authorization/github"
            >
              <span class="icon fas fa-lg is-flex is-align-items-center mr-2">
                <img src="@main/images/github_icon.png" alt="" />
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
        <div class="block">
          <a
            class="is-size-7 has-text-weight-bold has-text-info"
            href="/agreement"
            >利用規約
          </a>
          <span class="is-size-7 has-text-grey-dark"
            >に同意した上で、アカウント登録にお進みください。
          </span>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, defineExpose } from 'vue';

defineExpose({ activateLoginModal, activateAccountRegistrationModal });

const isAccountRegistrationModalActive = ref(false);
const isLoginModalActive = ref(false);

function activateLoginModal() {
  isLoginModalActive.value = true;
}
function activateAccountRegistrationModal() {
  isAccountRegistrationModalActive.value = true;
}
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