<template>
  <div
    class="content p-5 m-0"
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
    >
      <div class="media-left" v-if="props.displayRecorder">
        <a
          class="image is-48x48 m-0"
          :href="`/users/${medicationRecord.recorder.accountId}`"
        >
          <img
            class="is-rounded"
            :src="medicationRecord.recorder.profileImageURL || noProfileImage"
            alt=""
          />
        </a>
      </div>
      <div
        class="media-content columns has-text-grey-dark m-0"
        @click="activateMedicationRecordModal(medicationRecordId)"
      >
        <div class="column p-0">
          <p
            class="has-text-left m-0"
            v-if="props.elements.includes('medicine')"
          >
            <strong>
              {{ medicationRecord.takenMedicine.medicineName }}
            </strong>
            <strong class="pl-2">
              {{ medicationRecord.takenMedicine.dose }}
            </strong>
          </p>
          <p
            class="has-text-left m-0"
            v-if="props.elements.includes('symptom')"
          >
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
            <strong
              class="icon is-small"
              v-if="medicationRecord.followUp.afterMedication !== undefined"
            >
              <i class="fa-solid fa-angles-right"></i>
            </strong>
            <span
              v-if="medicationRecord.followUp.afterMedication !== undefined"
              v-html="
                MedicationRecordUtils.convertConditionLevelToIcon(
                  medicationRecord.followUp.afterMedication
                )
              "
            ></span>
          </p>
        </div>
        <div class="column p-0">
          <p class="has-text-right m-0">
            <span
              class="has-text-weight-semibold"
              v-if="props.elements.includes('dateTime')"
            >
              {{
                `${medicationRecord.takenMedicineOn} ${medicationRecord.takenMedicineAt}`
              }}
            </span>
            <span
              class="has-text-weight-semibold"
              v-if="props.elements.includes('time')"
            >
              {{ medicationRecord.takenMedicineAt }}
            </span>
          </p>
        </div>
      </div>
      <div
        class="media-right"
        @click="activateMedicationRecordModal(medicationRecordId)"
      >
        <p class="icon fas fa-lg has-text-link">
          <i class="fa-solid fa-angle-right"></i>
        </p>
      </div>
    </div>
    <!-- TODO: 自動的に読み込まれるように変更する -->
    <div
      class="has-text-centered mt-2"
      v-if="props.isAllowLoadMore && medicationRecords.canLoadMore"
    >
      <p
        class="is-size-7 has-text-link is-clickable"
        @click="medicationRecords.loadMore()"
      >
        さらに表示する
      </p>
    </div>
  </div>

  <div
    class="content has-text-centered p-2 m-0"
    v-if="props.isShowAppendButton"
  >
    <slot></slot>
  </div>

  <MedicationRecord
    ref="medicationRecord"
    :displayRecorder="props.displayRecorder"
    :csrf="props.csrf"
    @deleted="medicationRecordDeleted"
  ></MedicationRecord>
</template>

<script setup>
import noProfileImage from '@main/images/no_profile_image.png';
import MedicationRecord from '@main/js/components/medicationrecord/MedicationRecord.vue';
import {
  MedicationRecords,
  MedicationRecordUtils,
} from '@main/js/composables/model/MedicationRecords.js';
import { defineEmits, defineExpose, inject, reactive, ref } from 'vue';

const props = defineProps({
  isJoinedSharedGroup: Boolean,
  displayRecorder: Boolean,
  isAllowLoadMore: Boolean,
  isShowAppendButton: Boolean,
  elements: Array,
  csrf: String,
});
const emits = defineEmits(['deleted']);
defineExpose({ loadMedicationRecords });
const activateResultMessage = inject('activateResultMessage');

const medicationRecords = reactive(new MedicationRecords());
const medicationRecord = ref(null);

function loadMedicationRecords(filter) {
  medicationRecords.load(filter, activateResultMessage);
}

function activateMedicationRecordModal(medicationRecordId) {
  medicationRecord.value.activateMedicationRecordModal(
    medicationRecords.getMedicationRecord(medicationRecordId)
  );
}

function medicationRecordDeleted(medicationRecordId) {
  medicationRecords.delete(medicationRecordId);
  emits('deleted', medicationRecordId);
}
</script>
