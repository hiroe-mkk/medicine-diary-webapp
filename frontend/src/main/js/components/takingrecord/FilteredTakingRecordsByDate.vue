<template>
  <div class="container is-max-desktop p-3" v-if="takingRecords.size !== 0">
    <div class="content has-text-centered">
      <p class="icon-text is-size-4 is-flex is-justify-content-center">
        <strong class="has-text-grey-dark">きょうの服用記録</strong>
        <span class="icon has-text-grey-dark mx-2">
          <i class="fa-solid fa-book-open"></i>
        </span>
      </p>
    </div>
    <div class="content m-2">
      <div
        class="media is-flex is-align-items-center is-clickable mx-2"
        v-for="(takingRecord, takingRecordId) in takingRecords.values"
        @click="activateTakingRecordModal(takingRecordId)"
      >
        <div class="media-left">
          <figure class="image is-64x64 mx-3">
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
        <div class="media-content has-text-grey-dark">
          <p class="m-0">
            <span class="has-text-weight-bold">
              {{ takingRecord.takenMedicine.medicineName }}
            </span>
          </p>
          <p class="has-text-right m-0">
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

    <div
      class="modal"
      :class="{ 'is-active': isSelectedTakingRecordModalActive }"
      v-if="selectedTakingRecord.value !== undefined"
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
                  :href="`/medicines/${selectedTakingRecord.value.takenMedicine.medicineId}`"
                >
                  {{ selectedTakingRecord.value.takenMedicine.medicineName }}
                </a>
                <span class="ml-2">{{
                  selectedTakingRecord.value.takenMedicine.dose
                }}</span>
              </span>
            </p>
            <p class="is-flex is-justify-content-space-between mb-2">
              <strong>お薬を服用した時間</strong>
              <span>{{ selectedTakingRecord.value.takenAt }}</span>
            </p>
            <p class="is-flex is-justify-content-space-between mb-2">
              <strong>症状</strong>
              <span>
                <span>{{ selectedTakingRecord.value.followUp.symptom }}</span>
                (
                <small>{{
                  selectedTakingRecord.value.followUp.beforeTaking
                }}</small>
                <span
                  v-html="
                    TakingRecordUtils.toIcon(
                      selectedTakingRecord.value.followUp.beforeTaking
                    )
                  "
                ></span>
                <span
                  class="icon is-small mx-2"
                  v-if="
                    selectedTakingRecord.value.followUp.afterTaking !==
                    undefined
                  "
                >
                  <i class="fa-solid fa-angles-right"></i>
                </span>
                <small
                  v-if="
                    selectedTakingRecord.value.followUp.afterTaking !==
                    undefined
                  "
                >
                  {{ selectedTakingRecord.value.followUp.afterTaking }}
                </small>
                <span
                  v-html="
                    TakingRecordUtils.toIcon(
                      selectedTakingRecord.value.followUp.afterTaking
                    )
                  "
                  v-if="
                    selectedTakingRecord.value.followUp.afterTaking !==
                    undefined
                  "
                >
                </span>
                )
              </span>
            </p>
            <p
              class="has-text-left mb-2"
              v-if="selectedTakingRecord.value.note !== ''"
            >
              <strong>ノート</strong>
              <span class="m-2">{{ selectedTakingRecord.value.note }}</span>
            </p>
          </div>
          <div class="block">
            <div class="field is-grouped is-grouped-centered">
              <p class="control">
                <a
                  class="button is-small is-rounded is-outlined is-link"
                  :href="`/takingrecords/${selectedTakingRecord.value.takingRecordId}/modify`"
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
                    deleteTakingRecord(
                      selectedTakingRecord.value.takingRecordId
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
          </div>
        </div>
      </div>
    </div>

    <ResultMessage ref="resultMessage"></ResultMessage>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import {
  TakingRecords,
  Filter,
  TakingRecordUtils,
} from '@main/js/composables/model/TakingRecords.js';
import {
  HttpRequestClient,
  HttpRequestFailedError,
} from '@main/js/composables/HttpRequestClient.js';
import ResultMessage from '@main/js/components/ResultMessage.vue';
import noProfileImage from '@main/images/no_profile_image.png';

const props = defineProps({ csrf: String });

const takingRecords = reactive(new TakingRecords());
const selectedTakingRecord = reactive({ value: undefined });
const isSelectedTakingRecordModalActive = ref(false);

const resultMessage = ref(null);

onMounted(async () => {
  await HttpRequestClient.submitGetRequest('/api/users?member')
    .then((data) => {
      const members = data.users.map((user) => user.accountId);
      const date = new Date().toLocaleDateString().slice(0, 10);
      const filter = new Filter(props.medicineId, members, date, date);
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

function activateTakingRecordModal(takingRecordId) {
  selectedTakingRecord.value = takingRecords.getTakingRecord(takingRecordId);
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
      takingRecords.delete(takingRecordId);
      isSelectedTakingRecordModalActive.value = false;
      resultMessage.value.activate('INFO', `服用記録の削除が完了しました。`);
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
