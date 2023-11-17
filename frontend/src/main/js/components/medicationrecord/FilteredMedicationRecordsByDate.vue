<template>
  <div class="container has-text-centered is-max-desktop p-3">
    <div class="notification has-background-white p-3">
      <div class="content mb-3">
        <p class="icon-text is-size-4 is-flex is-justify-content-center m-3">
          <strong class="has-text-grey-dark">
            {{ todayWithoutYear() }}
          </strong>
          <strong class="has-text-grey-dark mx-2">の服用記録</strong>
          <span class="icon has-text-grey-dark mx-1">
            <i class="fa-solid fa-book-open"></i>
          </span>
        </p>
      </div>

      <div class="content is-inline-block pt-2 m-0" v-if="members.size === 0">
        <div class="is-flex is-align-items-center">
          <div
            class="is-clickable mx-2"
            :class="{ opacity: !filter.isUserActive(self.value.accountId) }"
            @click="toggleUserActive(self.value.accountId)"
          >
            <div class="is-flex is-justify-content-center">
              <figure class="image is-48x48 m-0">
                <img
                  class="is-rounded"
                  :src="self.value.profileImageURL"
                  v-if="self.value.profileImageURL !== undefined"
                />
                <img
                  class="is-rounded"
                  :src="noProfileImage"
                  v-if="self.value.profileImageURL === undefined"
                />
              </figure>
            </div>
            <p
              class="is-size-7 has-text-weight-bold has-text-grey-dark m-0 p-0"
            >
              {{ self.value.username }}
            </p>
          </div>
          <div
            class="is-clickable mx-2"
            :class="{ opacity: !filter.isUserActive(member.accountId) }"
            @click="toggleUserActive(member.accountId)"
            v-for="member in members"
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
            <p
              class="is-size-7 has-text-weight-bold has-text-grey-dark m-0 p-0"
            >
              {{ member.username }}
            </p>
          </div>
        </div>
      </div>

      <div
        class="content p-5"
        v-if="medicationRecords.isLoaded && medicationRecords.size === 0"
        v-clock
      >
        <p class="has-text-weight-bold has-text-grey">
          お薬を服用していません。
        </p>
      </div>

      <div
        class="content p-2"
        v-if="medicationRecords.isLoaded && medicationRecords.size !== 0"
        v-clock
      >
        <div
          class="media is-flex is-align-items-center is-clickable p-3 m-0"
          v-for="(
            medicationRecord, medicationRecordId
          ) in medicationRecords.values"
          @click="activateMedicationRecordModal(medicationRecordId)"
        >
          <div class="media-left" v-if="members.size === 0">
            <figure class="image is-64x64 m-0">
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
              <strong>
                {{ medicationRecord.takenMedicine.medicineName }}
              </strong>
              <span class="has-text-weight-semibold">
                (
                <span>
                  {{ medicationRecord.followUp.symptom }}
                </span>
                <span
                  v-html="
                    MedicationRecordUtils.convertConditionLevelToIcon(
                      medicationRecord.followUp.beforeMedication
                    )
                  "
                ></span>
                <span
                  class="icon is-small"
                  v-if="medicationRecord.followUp.afterMedication !== undefined"
                >
                  <i class="fa-solid fa-angles-right"></i>
                </span>
                <span
                  v-if="medicationRecord.followUp.afterMedication !== undefined"
                  v-html="
                    MedicationRecordUtils.convertConditionLevelToIcon(
                      medicationRecord.followUp.afterMedication
                    )
                  "
                ></span>
                )
              </span>
            </p>
            <p class="has-text-right m-0">
              <strong>
                {{ MedicationRecordUtils.toTime(medicationRecord.takenAt) }}
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
        <div
          class="has-text-centered mt-2"
          v-if="medicationRecords.canLoadMore"
        >
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
    :hasMembers="members.size === 0"
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

const self = reactive({ value: undefined });
const members = reactive([]);
const filter = reactive(new Filter());
const medicationRecords = reactive(new MedicationRecords());
const medicationRecord = ref(null);

const resultMessage = ref(null);

onMounted(async () => {
  await HttpRequestClient.submitGetRequest('/api/users?own')
    .then((data) => {
      self.value = data;
      filter.addAccountId(data.accountId);
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
        members.push(...data.users);
        filter.addAllAccountIds(members.map((member) => member.accountId));
      })
      .catch(() => {
        resultMessage.value.activate(
          'ERROR',
          'エラーが発生しました。',
          '通信状態をご確認のうえ、再度お試しください。'
        );
      });
  }

  filter.start = today();
  filter.end = today();
  medicationRecords.load(filter);
});

function today() {
  return new Date().toLocaleDateString().slice(0, 10);
}

function todayWithoutYear() {
  const today = new Date();
  return `${today.getMonth() + 1}月${today.getDate()}日`;
}

function toggleUserActive(accountId) {
  filter.toggleUserActive(accountId);
  medicationRecords.load(filter);
}

function loadMoreMedicationRecords() {
  medicationRecords.loadMore().catch(() => {
    resultMessage.value.activate(
      'ERROR',
      'エラーが発生しました。',
      '通信状態をご確認のうえ、再度お試しください。'
    );
  });
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
