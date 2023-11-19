<template>
  <div
    class="content p-5"
    v-if="medicationRecords.isLoaded && medicationRecords.size === 0"
    v-clock
  >
    <p class="has-text-weight-bold has-text-grey">服用記録がありません。</p>
  </div>

  <div
    class="content p-2"
    v-if="medicationRecords.isLoaded && medicationRecords.size !== 0"
    v-clock
  >
    <div
      class="media is-flex is-align-items-center is-clickable p-3 m-0"
      v-for="(medicationRecord, medicationRecordId) in medicationRecords.values"
      @click="activateMedicationRecordModal(medicationRecordId)"
    >
      <div class="media-left" v-if="props.hasMembers">
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

  <MedicationRecord
    ref="medicationRecord"
    :hasMembers="props.hasMembers"
    :csrf="props.csrf"
    @deleted="medicationRecordDeleted"
  ></MedicationRecord>
  <ResultMessage ref="resultMessage"></ResultMessage>
</template>

<script setup>
import { ref, reactive, defineExpose } from 'vue';
import {
  MedicationRecords,
  MedicationRecordUtils,
} from '@main/js/composables/model/MedicationRecords.js';
import ResultMessage from '@main/js/components/ResultMessage.vue';
import MedicationRecord from '@main/js/components/medicationrecord/MedicationRecord.vue';
import noProfileImage from '@main/images/no_profile_image.png';

const props = defineProps({
  isParticipatingInSharedGroup: Boolean,
  hasMembers: Boolean,
  csrf: String,
});
defineExpose({ loadMedicationRecords });

const medicationRecords = reactive(new MedicationRecords());
const medicationRecord = ref(null);

const resultMessage = ref(null);

function loadMedicationRecords(filter) {
  medicationRecords.load(filter).catch(() => {
    resultMessage.value.activate(
      'ERROR',
      'エラーが発生しました。',
      '通信状態をご確認のうえ、再度お試しください。'
    );
  });
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
