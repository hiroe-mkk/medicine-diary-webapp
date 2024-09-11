<template>
  <div class="content has-text-centered m-2">
    <div
      class="is-flex is-align-items-center is-justify-content-center"
      v-if="members.length !== 0"
    >
      <div v-for="member in members">
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

        <p
          class="is-size-7 has-text-weight-bold has-text-grey-dark"
          v-if="member.username !== ''"
        >
          {{ member.username }}
        </p>
        <p class="is-size-7 has-text-weight-bold has-text-grey" v-else>
          ( unknown )
        </p>
      </div>
    </div>

    <div v-if="members.length === 0">
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
