<template>
  <div class="content" v-if="joinedSharedGroup.id === undefined">
    <div class="notification is-white py-5 px-6">
      <div class="block">
        <p>
          <strong class="underline-marker has-text-link">
            情報共有によるストレス軽減
          </strong>
        </p>
        <p class="is-size-7 has-text-weight-semibold mb-4">
          薬の服用状況や体調の変化を家族やパートナーとリアルタイムで共有できます。<br />
          お互いの健康状態を把握することで、ストレスや不安の軽減に繋がります。<br />
        </p>
      </div>
      <div class="block">
        <p>
          <strong class="underline-marker has-text-link">お薬の管理</strong>
        </p>
        <p class="is-size-7 has-text-weight-semibold mb-4">
          家族やパートナーが服用しているお薬の情報や在庫状況が確認できます。<br />
          スマホからいつでも確認できるため、誤って購入してしまったり、必要な時に不足してしまったりするのを防ぐことができます。
        </p>
      </div>
      <div class="block">
        <p>
          <strong class="underline-marker has-text-link">
            プライバシー保護
          </strong>
        </p>
        <p class="is-size-7 has-text-weight-semibold mb-4">
          共有したくないお薬とその服用記録は、簡単に非公開設定にできます。<br />
          また、いつでも共有グループから脱退できるので安心してください。
        </p>
      </div>

      <button class="button is-link is-rounded is-outlined">
        <strong>共有グループをつくる</strong>
        <span class="icon fas fa-lg is-flex is-align-items-center mr-0">
          <i class="fa-solid fa-user-plus"></i>
        </span>
      </button>
    </div>
  </div>

  <div v-if="joinedSharedGroup.id !== undefined">
    <div class="notification is-white is-inline-block py-4 px-6">
      <p class="has-text-weight-semibold has-text-grey-dark pb-4">
        現在参加している共有グループ
      </p>
      <SharedGroup
        :sharedGroupMembers="joinedSharedGroup.members"
        :isJoined="true"
        :csrf="props.csrf"
      ></SharedGroup>
      <div class="field is-grouped is-grouped-centered pt-4 pb-2">
        <p class="control">
          <span class="button is-small is-rounded is-outlined is-link">
            <strong>ユーザーを招待する</strong>
            <span class="icon fas fa-lg is-flex is-align-items-center m-0">
              <i class="fa-solid fa-plus"></i>
            </span>
          </span>
        </p>
        <p class="control">
          <span
            class="button is-small is-rounded is-outlined is-danger"
            @click="isLeaveSharedGroupConfirmationModalActive = true"
          >
            <strong>共有グループから脱退する</strong>
            <span class="icon fas fa-lg is-flex is-align-items-center m-0">
              <i class="fa-solid fa-users-slash"></i>
            </span>
          </span>
        </p>
      </div>
    </div>

    <div
      class="modal"
      :class="{ 'is-active': isLeaveSharedGroupConfirmationModalActive }"
    >
      <div
        class="modal-background"
        @click="isLeaveSharedGroupConfirmationModalActive = false"
      ></div>
      <div class="modal-content is-flex is-justify-content-center">
        <div class="message is-inline-block is-info">
          <div class="message-body">
            <div class="content">
              <p class="has-text-centered mb-2">
                <strong class="is-size-5 mb-1">
                  ほんとうに共有グループから脱退してよろしいですか？
                </strong>
                <br />
              </p>
              <div class="field is-grouped is-grouped-centered p-2">
                <p class="control">
                  <button
                    type="button"
                    class="button is-small is-rounded is-link"
                    @click="leaveSharedGroup()"
                  >
                    共有グループから脱退する
                  </button>
                </p>
                <p class="control">
                  <button
                    type="button"
                    class="button is-small is-rounded is-outlined is-danger"
                    @click="isLeaveSharedGroupConfirmationModalActive = false"
                  >
                    キャンセル
                  </button>
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, inject } from 'vue';
import {
  HttpRequestClient,
  HttpRequestFailedError,
} from '@main/js/composables/HttpRequestClient.js';
import SharedGroup from '@main/js/components/sharedgroup/SharedGroup.vue';

const props = defineProps({
  joinedSharedGroupId: { type: String, default: undefined },
  csrf: String,
});
const activateResultMessage = inject('activateResultMessage');

const joinedSharedGroup = reactive({
  id: props.joinedSharedGroupId,
  members: [],
});

const isLeaveSharedGroupConfirmationModalActive = ref(false);

onMounted(async () => {
  if (joinedSharedGroup.id === undefined) return;

  HttpRequestClient.submitGetRequest(
    `/api/users?sharedGroupId=${joinedSharedGroup.id}`
  )
    .then((data) => {
      joinedSharedGroup.members = data.users;
    })
    .catch(() => {
      handleError(error);
    });
});

function leaveSharedGroup() {
  const form = new FormData();
  form.set('_csrf', props.csrf);

  HttpRequestClient.submitPostRequest('/api/shared-group/leave', form)
    .then(() => {
      activateResultMessage('INFO', `共有グループから脱退しました。`);
      isLeaveSharedGroupConfirmationModalActive.value = false;
      joinedSharedGroup.id = undefined;
      joinedSharedGroup.members = [];
    })
    .catch((error) => {
      if (error instanceof HttpRequestFailedError) {
        if (error.status == 401) {
          // 認証エラーが発生した場合
          location.reload();
          return;
        } else if (error.status == 500) {
          activateResultMessage(
            'ERROR',
            'システムエラーが発生しました。',
            'お手数ですが、再度お試しください。'
          );
          return;
        } else if (error.hasMessage()) {
          activateResultMessage(
            'ERROR',
            'エラーが発生しました。',
            error.getMessage()
          );
          return;
        }
      }

      activateResultMessage(
        'ERROR',
        'エラーが発生しました。',
        '通信状態をご確認のうえ、再度お試しください。'
      );
    });
}
</script>
