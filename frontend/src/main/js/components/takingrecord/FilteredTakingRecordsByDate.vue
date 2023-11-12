<template>
  <div class="container has-text-centered is-max-desktop p-3">
    <div class="notification has-background-white p-3">
      <div class="content">
        <p class="icon-text is-size-4 is-flex is-justify-content-center m-3">
          <strong class="has-text-grey-dark">本日の服用記録</strong>
          <span class="icon has-text-grey-dark mx-2">
            <i class="fa-solid fa-book-open"></i>
          </span>
        </p>
      </div>

      <div
        class="content is-inline-block m-0"
        v-if="props.isParticipatingInSharedGroup"
      >
        <div class="is-flex is-align-items-center">
          <div
            class="is-clickable mx-2"
            :class="{ opacity: !filter.isUserActive(user.accountId) }"
            @click="toggleUserActive(user.accountId)"
            v-for="user in users"
          >
            <div class="is-flex is-justify-content-center">
              <figure class="image is-48x48 m-0">
                <img
                  class="is-rounded"
                  :src="user.profileImageURL"
                  v-if="user.profileImageURL !== undefined"
                />
                <img
                  class="is-rounded"
                  :src="noProfileImage"
                  v-if="user.profileImageURL === undefined"
                />
              </figure>
            </div>
            <p
              class="is-size-7 has-text-weight-bold has-text-grey-dark m-0 p-0"
            >
              {{ user.username }}
            </p>
          </div>
        </div>
      </div>

      <div
        class="content m-5"
        v-if="takingRecords.isLoaded && takingRecords.size === 0"
        v-clock
      >
        <p class="has-text-weight-bold has-text-info">
          お薬を服用していません。
        </p>
      </div>

      <div
        class="content m-2"
        v-if="takingRecords.isLoaded && takingRecords.size !== 0"
        v-clock
      >
        <div
          class="media is-flex is-align-items-center is-clickable p-3 m-0"
          v-for="(takingRecord, takingRecordId) in takingRecords.values"
          @click="activateTakingRecordModal(takingRecordId)"
        >
          <div class="media-left">
            <figure class="image is-64x64 m-0">
              <img
                :src="takingRecord.recorder.profileImageURL"
                class="is-rounded"
                v-if="takingRecord.recorder.profileImageURL !== undefined"
              />
              <img
                :src="noProfileImage"
                class="is-rounded"
                v-if="takingRecord.recorder.profileImageURL === undefined"
              />
            </figure>
          </div>
          <div class="media-content has-text-left has-text-grey-dark">
            <p class="m-0">
              <span class="has-text-weight-bold">
                {{ takingRecord.takenMedicine.medicineName }}
              </span>
            </p>
            <p class="m-0">
              <span>
                {{ TakingRecordUtils.toTime(takingRecord.takenAt) }}
              </span>
            </p>
          </div>
          <div class="media-right">
            <p class="icon fas fa-lg ml-2 has-text-link">
              <i class="fa-solid fa-angle-right"></i>
            </p>
          </div>
        </div>
        <!-- TODO: 自動的に読み込まれるように変更する -->
        <div class="has-text-centered mt-2" v-if="takingRecords.canLoadMore">
          <button
            class="button is-small is-ghost"
            type="button"
            @click="loadMoreTakingRecords()"
          >
            さらに表示する
          </button>
        </div>
      </div>
    </div>
  </div>

  <TakingRecord
    ref="takingRecord"
    :isParticipatingInSharedGroup="props.isParticipatingInSharedGroup"
    :csrf="props.csrf"
    @deleted="takingRecordDeleted"
  ></TakingRecord>
  <ResultMessage ref="resultMessage"></ResultMessage>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import {
  TakingRecords,
  Filter,
  TakingRecordUtils,
} from '@main/js/composables/model/TakingRecords.js';
import { HttpRequestClient } from '@main/js/composables/HttpRequestClient.js';
import ResultMessage from '@main/js/components/ResultMessage.vue';
import TakingRecord from '@main/js/components/takingrecord/TakingRecord.vue';
import noProfileImage from '@main/images/no_profile_image.png';

const props = defineProps({
  isParticipatingInSharedGroup: Boolean,
  csrf: String,
});

const users = reactive([]);
const filter = reactive(new Filter());
const takingRecords = reactive(new TakingRecords());
const takingRecord = ref(null);

const resultMessage = ref(null);

onMounted(async () => {
  await HttpRequestClient.submitGetRequest('/api/users?own')
    .then((data) => {
      users.push(data);
      filter.addAccountId(users.map((user) => user.accountId));
    })
    .catch(() => {
      resultMessage.value.activate(
        'ERROR',
        '服用記録の読み込みに失敗しました。'
      );
    });

  if (props.isParticipatingInSharedGroup) {
    await HttpRequestClient.submitGetRequest('/api/users?members')
      .then((data) => {
        users.push(...data.users);
        filter.addAllAccountIds(users.map((user) => user.accountId));
      })
      .catch(() => {
        resultMessage.value.activate(
          'ERROR',
          '服用記録の読み込みに失敗しました。'
        );
      });
  }

  const date = new Date().toLocaleDateString().slice(0, 10);
  filter.start = date;
  filter.end = date;
  takingRecords.load(filter);
});

function toggleUserActive(accountId) {
  filter.toggleUserActive(accountId);
  takingRecords.load(filter);
}

function loadMoreTakingRecords() {
  takingRecords.loadMore().catch(() => {
    resultMessage.value.activate('ERROR', '服用記録の読み込みに失敗しました。');
  });
}

function activateTakingRecordModal(takingRecordId) {
  takingRecord.value.activateTakingRecordModal(
    takingRecords.getTakingRecord(takingRecordId)
  );
}

function takingRecordDeleted(takingRecordId) {
  takingRecords.delete(takingRecordId);
}
</script>
