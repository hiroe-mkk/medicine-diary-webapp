<template>
  <div class="container has-text-centered is-max-desktop p-3">
    <div class="content mb-3">
      <p class="icon-text is-size-4 is-flex is-justify-content-center">
        <strong class="has-text-grey-dark">服用記録一覧</strong>
        <span class="icon has-text-grey-dark mx-2">
          <i class="fa-solid fa-book-open"></i>
        </span>
      </p>
    </div>

    <div class="notification has-background-white p-3">
      <div
        class="content is-inline-block pt-2 m-0"
        v-if="props.isParticipatingInSharedGroup"
      >
        <div class="is-flex is-align-items-center">
          <div
            class="is-clickable mx-2"
            :class="{ opacity: !filter.isUserActive(user.accountId) }"
            @click="toggleUserActive(user.accountId)"
            v-for="user in users"
          >
            <div
              class="is-flex is-justify-content-center"
              v-if="props.isParticipatingInSharedGroup"
            >
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
        class="content p-5"
        v-if="medicationRecords.isLoaded && medicationRecords.size === 0"
        v-clock
      >
        <p class="has-text-weight-bold has-text-grey">服用記録がありません。</p>
      </div>

      <div
        class="content m-0"
        v-if="medicationRecords.isLoaded && medicationRecords.size !== 0"
        v-clock
      >
        <div
          class="media is-flex is-align-items-center is-clickable p-3 m-0"
          v-for="(medicationRecord, medicationRecordId) in medicationRecords.values"
          @click="activateMedicationRecordModal(medicationRecordId)"
        >
          <div class="media-left">
            <figure class="image is-48x48 m-0">
              <img
                :src="medicationRecord.recorder.profileImageURL"
                class="is-rounded"
                v-if="medicationRecord.recorder.profileImageURL !== undefined"
              />
              <img
                :src="noProfileImage"
                class="is-rounded"
                v-if="medicationRecord.recorder.profileImageURL === undefined"
              />
            </figure>
          </div>
          <div class="media-content has-text-grey-dark">
            <p class="m-0 has-text-left">
              <span class="has-text-weight-bold">
                {{ medicationRecord.takenMedicine.medicineName }}
              </span>
              (
              <span>
                {{ medicationRecord.followUp.symptom }}
              </span>
              <span
                v-html="
                  MedicationRecordUtils.convertConditionLevelToIcon(
                    medicationRecord.followUp.beforeTaking
                  )
                "
              ></span>
              <span
                class="icon is-small"
                v-if="medicationRecord.followUp.afterTaking !== undefined"
              >
                <i class="fa-solid fa-angles-right"></i>
              </span>
              <span
                v-if="medicationRecord.followUp.afterTaking !== undefined"
                v-html="
                  MedicationRecordUtils.convertConditionLevelToIcon(
                    medicationRecord.followUp.afterTaking
                  )
                "
              ></span>
              )
            </p>
            <p class="has-text-right m-0">
              <strong>
                {{ medicationRecord.takenAt }}
              </strong>
            </p>
          </div>
          <div class="media-right">
            <p class="icon fas fa-lg has-text-link">
              <i class="fa-solid fa-angle-right"></i>
            </p>
          </div>
        </div>
        <!-- TODO: 自動的に読み込まれるように変更する -->
        <div class="has-text-centered mt-2" v-if="medicationRecords.canLoadMore">
          <button
            class="button is-small is-ghost"
            type="button"
            @click="loadMoreMedicationRecords()"
          >
            さらに表示する
          </button>
        </div>
      </div>
    </div>
  </div>

  <MedicationRecord
    ref="medicationRecord"
    :isParticipatingInSharedGroup="props.isParticipatingInSharedGroup"
    :csrf="props.csrf"
    @deleted="medicationRecordDeleted"
  ></MedicationRecord>
  <ResultMessage ref="resultMessage"></ResultMessage>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import {
  MedicationRecords,
  Filter,
  MedicationRecordUtils,
} from '@main/js/composables/model/MedicationRecords.js';
import { HttpRequestClient } from '@main/js/composables/HttpRequestClient.js';
import ResultMessage from '@main/js/components/ResultMessage.vue';
import MedicationRecord from '@main/js/components/medicationrecord/MedicationRecord.vue';
import noProfileImage from '@main/images/no_profile_image.png';

const props = defineProps({
  isParticipatingInSharedGroup: Boolean,
  csrf: String,
});

const users = reactive([]);
const filter = reactive(new Filter());
const medicationRecords = reactive(new MedicationRecords());
const medicationRecord = ref(null);

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
        'エラーが発生しました。',
        '通信状態をご確認のうえ、再度お試しください。'
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
          'エラーが発生しました。',
          '通信状態をご確認のうえ、再度お試しください。'
        );
      });
  }

  medicationRecords.load(filter);
});

function loadMoreMedicationRecords() {
  medicationRecords.loadMore().catch(() => {
    resultMessage.value.activate(
      'ERROR',
      'エラーが発生しました。',
      '通信状態をご確認のうえ、再度お試しください。'
    );
  });
}

function toggleUserActive(accountId) {
  filter.toggleUserActive(accountId);
  medicationRecords.load(filter);
}

function activateMedicationRecordModal(medicationRecordId) {
  medicationRecord.value.activateMedicationRecordModal(
    medicationRecords.getMedicationRecord(medicationRecordId)
  );
}

function medicationRecordDeleted(medicationRecordId) {
  medicationRecords.delete(medicationRecordId);
}
</script>