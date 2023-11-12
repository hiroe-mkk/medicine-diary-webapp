<template>
  <div class="container has-text-centered is-max-desktop p-3">
    <div class="content">
      <p class="icon-text is-size-4 is-flex is-justify-content-center">
        <strong class="has-text-grey-dark">服用記録一覧</strong>
        <span class="icon has-text-grey-dark mx-2">
          <i class="fa-solid fa-book-open"></i>
        </span>
      </p>
    </div>

    <div class="content is-inline-block m-0">
      <div class="is-flex is-align-items-center">
        <div
          class="is-clickable mx-2"
          :class="{ opacity: !filter.isMemberActive(member.accountId) }"
          @click="toggleMemberActive(member.accountId)"
          v-for="member in members"
          v-if="members.length !== 1"
        >
          <div class="is-flex is-justify-content-center">
            <figure class="image is-48x48 m-0">
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

    <div class="content m-0" v-if="takingRecords.size !== 0">
      <div
        class="media is-flex is-align-items-center is-clickable p-3 m-0"
        v-for="(takingRecord, takingRecordId) in takingRecords.values"
        @click="activateTakingRecordModal(takingRecordId)"
      >
        <div class="media-left">
          <figure class="image is-48x48 m-0">
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
          <p class="has-text-right m-0">
            <span>
              {{ takingRecord.takenAt }}
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

  <TakingRecord
    ref="takingRecord"
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

const props = defineProps({ csrf: String });

const members = reactive([]);
const filter = reactive(new Filter());
const takingRecords = reactive(new TakingRecords());
const takingRecord = ref(null);

const resultMessage = ref(null);

onMounted(async () => {
  await HttpRequestClient.submitGetRequest('/api/users?member')
    .then((data) => {
      members.push(...data.users);
      filter.initializeMembers(members.map((user) => user.accountId));

      takingRecords.load(filter);
    })
    .catch(() => {
      resultMessage.value.activate(
        'ERROR',
        '服用記録の読み込みに失敗しました。'
      );
    });
});

function loadMoreTakingRecords() {
  takingRecords.loadMore().catch(() => {
    resultMessage.value.activate('ERROR', '服用記録の読み込みに失敗しました。');
  });
}

function toggleMemberActive(accountId) {
  filter.toggleMemberActive(accountId);
  takingRecords.load(filter);
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
