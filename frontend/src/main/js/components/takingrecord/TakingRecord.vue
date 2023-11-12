<template>
  <div
    class="modal"
    :class="{ 'is-active': isSelectedTakingRecordModalActive }"
    v-if="takingRecord.value !== undefined"
  >
    <div
      class="modal-background"
      @click="isSelectedTakingRecordModalActive = false"
    ></div>
    <div class="modal-content">
      <div class="notification has-background-link-light py-3 px-5">
        <div class="has-text-right">
          <button
            class="delete"
            type="button"
            @click="isSelectedTakingRecordModalActive = false"
          ></button>
        </div>
        <p class="icon-text is-size-5 is-flex is-justify-content-center">
          <strong class="has-text-grey-dark">服用記録</strong>
          <span class="icon has-text-grey-dark mx-2">
            <i class="fa-solid fa-book-open"></i>
          </span>
        </p>
        <div class="block m-3">
          <p class="is-flex is-justify-content-space-between mb-2">
            <strong>お薬</strong>
            <span>
              <a
                class="is-underlined has-text-info has-text-weight-semibold"
                :href="`/medicines/${takingRecord.value.takenMedicine.medicineId}`"
              >
                {{ takingRecord.value.takenMedicine.medicineName }}
              </a>
              <span class="ml-2">{{
                takingRecord.value.takenMedicine.dose
              }}</span>
            </span>
          </p>
          <p class="is-flex is-justify-content-space-between mb-2">
            <strong>お薬を服用した時間</strong>
            <span>{{ takingRecord.value.takenAt }}</span>
          </p>
          <p class="is-flex is-justify-content-space-between mb-2">
            <strong>症状</strong>
            <span>
              <span>{{ takingRecord.value.followUp.symptom }}</span>
              (
              <small>{{ takingRecord.value.followUp.beforeTaking }}</small>
              <span
                v-html="
                  TakingRecordUtils.toIcon(
                    takingRecord.value.followUp.beforeTaking
                  )
                "
              ></span>
              <span
                class="icon is-small mx-2"
                v-if="takingRecord.value.followUp.afterTaking !== undefined"
              >
                <i class="fa-solid fa-angles-right"></i>
              </span>
              <small
                v-if="takingRecord.value.followUp.afterTaking !== undefined"
              >
                {{ takingRecord.value.followUp.afterTaking }}
              </small>
              <span
                v-html="
                  TakingRecordUtils.toIcon(
                    takingRecord.value.followUp.afterTaking
                  )
                "
                v-if="takingRecord.value.followUp.afterTaking !== undefined"
              >
              </span>
              )
            </span>
          </p>
          <p class="has-text-left" v-if="takingRecord.value.note !== ''">
            <strong>ノート</strong>
            <p class="notification has-background-white has-text-left py-2 px-3 my-2 mx-0">
              <span class="is-underlined p-1">{{ takingRecord.value.note }}</span>
            </p>
          </p>
          <p
            class="has-text-right mb-2"
            v-if="props.isParticipatingInSharedGroup"
          >
            <strong>recorded by  </strong>
            <span>{{ takingRecord.value.recorder.username }}</span>
          </p>
        </div>
        <div class="block" v-if="takingRecord.value.isOwned">
          <div class="field is-grouped is-grouped-centered">
            <p class="control">
              <a
                class="button is-small is-rounded is-outlined is-link"
                :href="`/takingrecords/${takingRecord.value.takingRecordId}/modify`"
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
                @click="deleteTakingRecord(takingRecord.value.takingRecordId)"
              >
                <span class="icon is-flex is-align-items-center mr-0">
                  <i class="fa-solid fa-trash-can"></i>
                </span>
                <span class="is-size-7 has-text-weight-bold">削除する</span>
              </button>
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>

  <ResultMessage ref="resultMessage"></ResultMessage>
</template>

<script setup>
import { ref, reactive, defineExpose, defineEmits } from 'vue';
import {
  HttpRequestClient,
  HttpRequestFailedError,
} from '@main/js/composables/HttpRequestClient.js';
import { TakingRecordUtils } from '@main/js/composables/model/TakingRecords.js';
import ResultMessage from '@main/js/components/ResultMessage.vue';

const props = defineProps({
  isParticipatingInSharedGroup: Boolean,
  csrf: String,
});
const emits = defineEmits(['deleted']);
defineExpose({ activateTakingRecordModal });

const takingRecord = reactive({ value: undefined });
const isSelectedTakingRecordModalActive = ref(false);

const resultMessage = ref(null);

function activateTakingRecordModal(selectedTakingRecord) {
  takingRecord.value = selectedTakingRecord;
  isSelectedTakingRecordModalActive.value = true;
}

function deleteTakingRecord(takingRecordId) {
  const form = new FormData();
  form.set('_csrf', props.csrf);

  HttpRequestClient.submitPostRequest(
    `/api/takingrecords/${takingRecordId}/delete`,
    form
  )
    .then(() => {
      resultMessage.value.activate('INFO', `服用記録の削除が完了しました。`);
      isSelectedTakingRecordModalActive.value = false;
      emits('deleted', takingRecordId);
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
</script>
