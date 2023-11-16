<template>
  <div class="container is-max-desktop p-3" v-if="medicationRecords.size !== 0">
    <div class="content has-text-centered mb-3">
      <p class="icon-text is-size-4 is-flex is-justify-content-center">
        <strong class="has-text-grey-dark">服用記録</strong>
        <span class="icon has-text-grey-dark mx-2">
          <i class="fa-solid fa-book-open"></i>
        </span>
      </p>
    </div>

    <div class="notification has-background-white p-3">
      <div class="content m-2" v-if="medicationRecords.size !== 0">
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
            <p class="has-text-left m-0">
              <strong>
                {{ medicationRecord.followUp.symptom }}
              </strong>
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
            </p>
            <p class="has-text-right m-0">
              <strong> {{ medicationRecord.takenAt }} </strong>
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
  medicineId: String,
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

  filter.medicineId = props.medicineId;
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

function activateMedicationRecordModal(medicationRecordId) {
  medicationRecord.value.activateMedicationRecordModal(
    medicationRecords.getMedicationRecord(medicationRecordId)
  );
}

function medicationRecordDeleted(medicationRecordId) {
  medicationRecords.delete(medicationRecordId);
}
</script>
