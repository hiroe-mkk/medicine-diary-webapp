<template>
  <div class="content m-2">
    <div class="is-flex is-align-items-center" v-if="members.length !== 0">
      <div class="has-text-centered mx-2" v-for="member in members">
        <div class="is-flex is-justify-content-center">
          <a class="image is-64x64 m-0" :href="`/users/${member.accountId}`">
            <img
              class="is-rounded"
              :src="member.profileImageURL"
              alt=""
              v-if="member.profileImageURL !== undefined"
            />
            <img
              src="@main/images/no_profile_image.png"
              alt=""
              class="is-rounded"
              v-if="member.profileImageURL === undefined"
            />
          </a>
        </div>
        <p class="has-text-weight-bold has-text-grey-dark m-0 p-0">
          {{ member.username }}
        </p>
      </div>
    </div>
    <div class="has-text-centered py-2" v-if="members.length === 0">
      <p class="is-size-7 has-text-weight-semibold has-text-grey">
        メンバーがいません。
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { HttpRequestClient } from '@main/js/composables/HttpRequestClient.js';

const members = reactive([]);

onMounted(() => {
  HttpRequestClient.submitGetRequest('/api/users?members')
    .then((data) => {
      members.push(...data.users);
    })
    .catch(() => {
      activateResultMessage(
        'ERROR',
        'エラーが発生しました。',
        '通信状態をご確認のうえ、再度お試しください。'
      );
    });
});
</script>