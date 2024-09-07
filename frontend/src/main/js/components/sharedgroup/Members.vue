<template>
  <div class="content m-2">
    <div class="is-flex is-align-items-center" v-if="members.length !== 0">
      <div class="has-text-centered mx-2" v-for="member in members">
        <div class="is-flex is-justify-content-center">
          <component
            :is="props.isClickable ? 'a' : 'p'"
            class="image is-64x64 m-0"
            v-bind="
              props.isClickable ? { href: `/users/${member.accountId}` } : {}
            "
          >
            <img
              class="is-rounded"
              :src="member.profileImageURL || noProfileImage"
              alt=""
            />
          </component>
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
import { HttpRequestClient } from '@main/js/composables/HttpRequestClient.js';
import { onMounted, reactive } from 'vue';
import noProfileImage from '@main/images/no_profile_image.png';

const props = defineProps({
  isClickable: { type: Boolean, default: false },
  sharedGroupId: { type: String, default: undefined },
});

const members = reactive([]);

onMounted(() => {
  if (props.sharedGroupId === undefined) return;

  HttpRequestClient.submitGetRequest(
    `/api/users?sharedGroupId=${props.sharedGroupId}`
  )
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
