<template>
  <div class="content has-text-centered">
    <strong class="is-size-4 has-text-grey-dark">
      {{ profile.username }}
    </strong>
  </div>

  <div class="container is-max-desktop">
    <div class="panel is-white">
      <div class="panel-heading py-1 my-0"></div>
      <div
        class="panel-block has-text-grey has-background-white is-flex is-justify-content-space-between is-clickable"
        @click="activateUsernameChangeModal()"
      >
        <strong class="has-text-grey">ユーザー名</strong>
        <span class="icon is-small">
          <i class="fa-solid fa-greater-than"></i>
        </span>
      </div>
    </div>
  </div>

  <div class="modal" :class="{ 'is-active': isUsernameChangeModalActive }">
    <div
      class="modal-background"
      @click="isUsernameChangeModalActive = false"
    ></div>
    <div class="modal-content">
      <div class="notification is-white">
        <div class="title is-5 has-text-link-dark has-text-centered">
          ユーザー名を変更する
        </div>
        <section class="content">
          <form
            class="form"
            method="post"
            @submit.prevent="submitUsernameChangeForm()"
          >
            <div class="field">
              <div class="control">
                <input
                  class="input is-info"
                  type="text"
                  name="username"
                  v-model="editingUsername"
                  maxlength="30"
                  placeholder="ユーザー名"
                />
              </div>
            </div>
            <div class="field is-grouped is-grouped-centered">
              <p class="control">
                <button class="button is-small is-rounded is-link">完了</button>
              </p>
              <p class="control">
                <button
                  type="button"
                  class="button is-small is-rounded is-danger"
                  @click="isUsernameChangeModalActive = false"
                >
                  キャンセル
                </button>
              </p>
            </div>
          </form>
        </section>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, reactive } from 'vue';
import { HttpRequestClient } from '@main/js/composables/HttpRequestClient.js';

const props = defineProps({ username: String, csrf: String });

const profile = reactive({ username: props.username });
const isUsernameChangeModalActive = ref(false);
const editingUsername = ref('');

function activateUsernameChangeModal() {
  editingUsername.value = profile.username;
  isUsernameChangeModalActive.value = true;
}

function submitUsernameChangeForm() {
  const form = new URLSearchParams();
  form.set('username', editingUsername.value);
  form.set('_csrf', props.csrf);

  HttpRequestClient.submitPostRequest('/api/profile/username/change', form)
    .then(() => {
      profile.username = editingUsername.value;
      isUsernameChangeModalActive.value = false;
    })
    .catch((error) => {
      // TODO
    });
}
</script>
