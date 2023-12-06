<template>
  <div class="content" v-if="invitedSharedGroups.length !== 0">
    <div
      class="message is-link is-inline-block"
      v-for="invitedSharedGroup in invitedSharedGroups"
    >
      <div class="message-header is-flex is-justify-content-center">
        <p>
          以下の共有グループに招待されています。<br />
          参加しますか？<br />
        </p>
      </div>
      <div class="message-body has-background-white">
        <div class="is-flex is-align-items-end is-justify-content-center">
          <SharedGroup
            :sharedGroup="invitedSharedGroup"
            :isParticipating="false"
            :csrf="props.csrf"
          ></SharedGroup>
        </div>
        <p class="help" v-if="participatingSharedGroup.value !== undefined">
          ※参加できる共有グループは1つまでです。<br />
          　この共有グループに参加するためには、現在参加している共有グループでの共有を停止してください。
        </p>
        <div class="field is-grouped is-grouped-centered p-2">
          <p class="control">
            <button
              class="button is-small is-rounded is-outlined is-link"
              @click="participateIn(invitedSharedGroup.sharedGroupId)"
              :disabled="participatingSharedGroup.value !== undefined"
            >
              参加する
            </button>
          </p>
          <p class="control">
            <button
              class="button is-small is-rounded is-outlined is-danger"
              formaction="/shared-group/reject"
            >
              拒否する
            </button>
          </p>
        </div>
      </div>
    </div>
  </div>

  <div class="content" v-if="participatingSharedGroup.value === undefined">
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
          また、いつでも共有を停止できるので安心してください。
        </p>
      </div>
      <button
        class="button is-link is-rounded is-outlined"
        @click="activateUserSearchModal()"
      >
        <strong>共有する</strong>
        <span class="icon fas fa-lg is-flex is-align-items-center mr-0">
          <i class="fa-solid fa-user-plus"></i>
        </span>
      </button>
    </div>
    <UserSearch ref="userSearch" :csrf="props.csrf"></UserSearch>
  </div>

  <div v-if="participatingSharedGroup.value !== undefined">
    <div class="notification is-white is-inline-block py-4 px-6">
      <p
        class="has-text-weight-semibold has-text-grey-dark pb-4"
        v-if="invitedSharedGroups.length !== 0"
      >
        現在参加している共有グループ
      </p>
      <SharedGroup
        :sharedGroup="participatingSharedGroup.value"
        :isParticipating="true"
        :csrf="props.csrf"
        @update="loadSharedGroup()"
      ></SharedGroup>
      <div class="field is-grouped is-grouped-centered pt-4 pb-2">
        <p class="control">
          <span
            class="button is-small is-rounded is-outlined is-link"
            @click="activateUserSearchModal()"
          >
            <strong>ユーザーを招待する</strong>
            <span class="icon fas fa-lg is-flex is-align-items-center m-0">
              <i class="fa-solid fa-plus"></i>
            </span>
          </span>
        </p>
        <p class="control">
          <span
            class="button is-small is-rounded is-outlined is-danger"
            @click="isUnshareConfirmationModalActive = true"
          >
            <strong>共有を停止する</strong>
            <span class="icon fas fa-lg is-flex is-align-items-center m-0">
              <i class="fa-solid fa-users-slash"></i>
            </span>
          </span>
        </p>
      </div>
    </div>

    <UserSearch
      ref="userSearch"
      :sharedGroupId="participatingSharedGroup.value.sharedGroupId"
      :csrf="props.csrf"
    >
    </UserSearch>
    <div
      class="modal"
      :class="{ 'is-active': isUnshareConfirmationModalActive }"
    >
      <div
        class="modal-background"
        @click="isUnshareConfirmationModalActive = false"
      ></div>
      <div class="modal-content is-flex is-justify-content-center">
        <div class="message is-inline-block is-info">
          <div class="message-body">
            <div class="content">
              <p class="has-text-centered mb-2">
                <strong class="is-size-5 mb-1"> 共有を停止しますか？ </strong>
                <br />
              </p>
              <form class="form" method="post" action="/shared-group/unshare">
                <input name="_csrf" :value="props.csrf" hidden />
                <input
                  name="sharedGroupId"
                  :value="participatingSharedGroup.value.sharedGroupId"
                  hidden
                  v-if="participatingSharedGroup.value !== undefined"
                />
                <div class="field is-grouped is-grouped-centered p-2">
                  <p class="control">
                    <button class="button is-small is-rounded is-link">
                      停止する
                    </button>
                  </p>
                  <p class="control">
                    <button
                      type="button"
                      class="button is-small is-rounded is-outlined is-danger"
                      @click="isUnshareConfirmationModalActive = false"
                    >
                      キャンセル
                    </button>
                  </p>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <ResultMessage ref="resultMessage"></ResultMessage>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { HttpRequestClient } from '@main/js/composables/HttpRequestClient.js';
import UserSearch from '@main/js/components/sharedgroup/UserSearch.vue';
import SharedGroup from '@main/js/components/sharedgroup/SharedGroup.vue';
import ResultMessage from '@main/js/components/ResultMessage.vue';

const props = defineProps({ csrf: String });

const participatingSharedGroup = reactive({ value: undefined });
const invitedSharedGroups = reactive([]);

const isUnshareConfirmationModalActive = ref(false);
const userSearch = ref(null);
const resultMessage = ref(null);

onMounted(async () => {
  loadSharedGroup();
});

async function loadSharedGroup() {
  participatingSharedGroup.value = undefined;
  invitedSharedGroups.splice(0, invitedSharedGroups.length);

  await HttpRequestClient.submitGetRequest('/api/shared-group')
    .then((data) => {
      participatingSharedGroup.value = data.participatingSharedGroup;
      invitedSharedGroups.push(...data.invitedSharedGroups);
    })
    .catch(() => {
      resultMessage.value.activate(
        'ERROR',
        'エラーが発生しました。',
        '通信状態をご確認のうえ、再度お試しください。'
      );
    });
}

function participateIn(sharedGroupId) {
  const form = new FormData();
  form.set('_csrf', props.csrf);
  form.set('sharedGroupId', sharedGroupId);

  HttpRequestClient.submitPostRequest('/api/shared-group/participate', form)
    .then(() => {
      resultMessage.value.activate('INFO', `共有グループに参加しました。`);
      loadSharedGroup();
    })
    .catch((error) => {
      if (error instanceof HttpRequestFailedError) {
        if (error.status == 401) {
          // 認証エラーが発生した場合
          location.reload();
          return;
        } else if (error.status == 500) {
          resultMessage.value.activate(
            'ERROR',
            'システムエラーが発生しました。',
            'お手数ですが、再度お試しください。'
          );
          return;
        } else if (error.hasMessage()) {
          resultMessage.value.activate(
            'ERROR',
            'エラーが発生しました。',
            error.getMessage()
          );
          return;
        }
      }

      resultMessage.value.activate(
        'ERROR',
        'エラーが発生しました。',
        '通信状態をご確認のうえ、再度お試しください。'
      );
    });
}

function activateUserSearchModal() {
  userSearch.value.activateSearchModal();
}
</script>
