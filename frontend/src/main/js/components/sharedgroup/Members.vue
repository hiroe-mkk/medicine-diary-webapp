<template>
  <div class="content m-2" v-if="members.length !== 0">
    <div class="is-flex is-align-items-center">
      <div class="has-text-centered mx-2" v-for="member in members">
        <div class="is-flex is-justify-content-center">
          <figure class="image is-64x64 m-0">
            <img
              class="is-rounded"
              :src="member.profileImageURL"
              v-if="member.profileImageURL !== undefined"
            />
            <img
              class="is-rounded"
              :src="noProfileImage"
              v-if="member.profileImageURL === undefined"
            />
          </figure>
        </div>
        <p class="is-size-7 has-text-weight-bold has-text-grey-dark m-0 p-0">
          {{ member.username }}
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { HttpRequestClient } from '@main/js/composables/HttpRequestClient.js';
import noProfileImage from '@main/images/no_profile_image.png';

const members = reactive([]);
const resultMessage = ref(null);

onMounted(async () => {
  await HttpRequestClient.submitGetRequest('/api/users?members')
    .then((data) => {
      members.push(...data.users);
    })
    .catch(() => {
      resultMessage.value.activate(
        'ERROR',
        'エラーが発生しました。',
        '通信状態をご確認のうえ、再度お試しください。'
      );
    });
});
</script>
