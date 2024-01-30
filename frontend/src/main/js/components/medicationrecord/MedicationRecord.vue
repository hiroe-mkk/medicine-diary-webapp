<template>
  <div
    class="modal"
    :class="{ 'is-active': isSelectedMedicationRecordModalActive }"
    v-if="medicationRecord.value !== undefined"
  >
    <div
      class="modal-background"
      @click="isSelectedMedicationRecordModalActive = false"
    ></div>
    <div class="modal-card">
      <div
        class="modal-card-head has-background-white is-flex is-justify-content-space-between p-3"
      >
        <span
          class="is-flex is-align-items-center"
          v-if="props.displayRecorder"
        >
          <a
            class="image is-32x32 is-inline-block m-0"
            :href="`/users/${medicationRecord.value.recorder.accountId}`"
          >
            <img
              :src="medicationRecord.value.recorder.profileImageURL"
              alt=""
              class="is-rounded"
              v-if="
                medicationRecord.value.recorder.profileImageURL !== undefined
              "
            />
            <img
              src="@main/images/no_profile_image.png"
              alt=""
              class="is-rounded"
              v-if="
                medicationRecord.value.recorder.profileImageURL === undefined
              "
            />
          </a>
          <span class="is-size-7 has-text-weight-bold ml-2">
            {{ medicationRecord.value.recorder.username }}
          </span>
        </span>
        <span
          class="is-flex is-align-items-center"
          v-if="!props.displayRecorder"
        >
          　
        </span>
        <button
          class="delete"
          type="button"
          @click="isSelectedMedicationRecordModalActive = false"
        ></button>
      </div>
      <div class="modal-card-body py-3 px-5">
        <p class="is-size-5 has-text-weight-bold has-text-centered mb-2">
          <span>{{ medicationRecord.value.takenMedicineOn }}</span>
          <span class="pl-2">{{ medicationRecord.value.takenMedicineAt }}</span>
        </p>
        <p class="is-flex is-justify-content-space-between mb-2">
          <span class="has-text-grey-dark has-text-weight-semibold">お薬</span>
          <span>
            <a
              class="is-underlined has-text-info has-text-weight-bold"
              :href="`/medicines/${medicationRecord.value.takenMedicine.medicineId}`"
            >
              {{ medicationRecord.value.takenMedicine.medicineName }}
            </a>
            <strong class="has-text-weight-bold ml-2">
              {{ medicationRecord.value.takenMedicine.dose }}
            </strong>
          </span>
        </p>
        <p class="is-flex is-justify-content-space-between mb-2">
          <span class="has-text-grey-dark has-text-weight-semibold">症状</span>
          <strong>{{ medicationRecord.value.followUp.symptom }}</strong>
        </p>
        <div class="columns m-0">
          <p
            class="column has-text-grey-dark has-text-weight-semibold has-text-left p-0"
          >
            経過
          </p>
          <p class="column has-text-right mb-2 p-0">
            <span
              class="has-text-weight-bold"
              v-if="medicationRecord.value.symptomOnsetAt !== undefined"
            >
              {{ medicationRecord.value.symptomOnsetAt }}
            </span>
            <span
              v-html="
                MedicationRecordUtils.convertConditionLevelToIcon(
                  medicationRecord.value.followUp.beforeMedication
                )
              "
            ></span>
            <span class="icon is-small mx-2">
              <i class="fa-solid fa-angles-right"></i>
            </span>

            <span class="has-text-weight-bold">
              {{ medicationRecord.value.takenMedicineAt }}
            </span>
            <span class="icon is-small mx-1">
              <i class="fa-solid fa-capsules"></i>
            </span>
            <span
              class="icon is-small mx-2"
              v-if="
                medicationRecord.value.onsetEffectAt !== undefined ||
                medicationRecord.value.followUp.afterMedication !== undefined
              "
            >
              <i class="fa-solid fa-angles-right"></i>
            </span>

            <span
              class="has-text-weight-bold"
              v-if="medicationRecord.value.onsetEffectAt !== undefined"
            >
              {{ medicationRecord.value.onsetEffectAt }}
            </span>
            <span
              v-html="
                MedicationRecordUtils.convertConditionLevelToIcon(
                  medicationRecord.value.followUp.afterMedication
                )
              "
              v-if="
                medicationRecord.value.followUp.afterMedication !== undefined
              "
            >
            </span>
          </p>
        </div>
        <p
          class="notification has-background-white-bis has-text-weight-semibold has-text-left is-size-7 p-3 m-2"
          v-if="medicationRecord.value.note !== ''"
        >
          {{ medicationRecord.value.note }}
        </p>
      </div>
      <div
        class="modal-card-foot has-background-white is-flex is-justify-content-center p-2"
      >
        <div
          class="field is-grouped is-grouped-centered p-2"
          v-if="medicationRecord.value.isRecordedBySelf"
        >
          <p class="control">
            <a
              class="button is-small is-rounded is-outlined is-link"
              :href="`/medication-records/${medicationRecord.value.medicationRecordId}/modify`"
            >
              <span class="icon is-flex is-align-items-center mr-0">
                <i class="fa-regular fa-pen-to-square"></i>
              </span>
              <span class="is-size-7 has-text-weight-bold">修正する</span>
            </a>
          </p>
          <p class="control">
            <button
              type="button"
              class="button is-small is-rounded is-outlined is-danger"
              @click="
                deleteMedicationRecord(
                  medicationRecord.value.medicationRecordId
                )
              "
            >
              <span class="icon is-flex is-align-items-center mr-0">
                <i class="fa-solid fa-trash-can"></i>
              </span>
              <span class="is-size-7 has-text-weight-bold">削除する</span>
            </button>
          </p>
        </div>
        <div
          class="block py-3"
          v-if="!medicationRecord.value.isRecordedBySelf"
        ></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, defineExpose, defineEmits, inject } from 'vue';
import {
  HttpRequestClient,
  HttpRequestFailedError,
} from '@main/js/composables/HttpRequestClient.js';
import { MedicationRecordUtils } from '@main/js/composables/model/MedicationRecords.js';

const props = defineProps({
  displayRecorder: Boolean,
  csrf: String,
});
const emits = defineEmits(['deleted']);
defineExpose({ activateMedicationRecordModal });
const activateResultMessage = inject('activateResultMessage');

const medicationRecord = reactive({ value: undefined });
const isSelectedMedicationRecordModalActive = ref(false);

function activateMedicationRecordModal(selectedMedicationRecord) {
  medicationRecord.value = selectedMedicationRecord;
  isSelectedMedicationRecordModalActive.value = true;
}

function deleteMedicationRecord(medicationRecordId) {
  const form = new FormData();
  form.set('_csrf', props.csrf);

  HttpRequestClient.submitPostRequest(
    `/api/medication-records/${medicationRecordId}/delete`,
    form
  )
    .then(() => {
      activateResultMessage('INFO', `服用記録の削除が完了しました。`);
      isSelectedMedicationRecordModalActive.value = false;
      emits('deleted', medicationRecordId);
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