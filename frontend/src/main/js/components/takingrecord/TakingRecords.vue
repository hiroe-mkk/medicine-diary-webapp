<template>
  <div class="content has-text-centered">
    <p class="icon-text is-size-4 is-flex is-justify-content-center">
      <strong class="has-text-grey-dark">服用記録</strong>
      <span class="icon has-text-grey-dark mx-2">
        <i class="fa-solid fa-book-open"></i>
      </span>
    </p>
  </div>
  <div class="content m-2">
    <div
      class="media mx-2 is-clickable"
      v-for="(
        takingRecordOverview, takingRecordId
      ) in takingRecordOverviews.values"
      @click="activateTakingRecordDetailModal(takingRecordId)"
    >
      <div
        class="media-content has-text-grey-dark is-flex is-justify-content-space-between"
      >
        <p class="m-0">
          <span>{{ takingRecordOverview.beforeTaking }}</span>
          <span
            class="icon is-small mx-3"
            v-if="takingRecordOverview.afterTaking !== undefined"
          >
            <i class="fa-solid fa-angles-right"></i>
          </span>
          <span v-if="takingRecordOverview.afterTaking !== undefined">
            {{ takingRecordOverview.afterTaking }}
          </span>
        </p>
        <p class="m-0">
          <span> {{ takingRecordOverview.takenAt }} </span>
          <span class="icon is-small ml-2 has-text-link">
            <i class="fa-solid fa-greater-than"></i>
          </span>
        </p>
      </div>
    </div>
    <!-- TODO: 自動的に読み込まれるように変更する -->
    <div
      class="has-text-centered mt-2"
      v-if="takingRecordOverviews.canLoadMore"
    >
      <button
        class="button is-small is-ghost"
        type="button"
        @click="loadTakingRecordOverviews()"
      >
        さらに表示する
      </button>
    </div>
  </div>

  <div class="modal" :class="{ 'is-active': isTakingRecordDetailModalActive }">
    <div
      class="modal-background"
      @click="isTakingRecordDetailModalActive = false"
    ></div>
    <div class="modal-content">
      <div class="notification has-background-link-light py-3 px-5">
        <div class="has-text-right">
          <button
            class="delete"
            type="button"
            @click="isTakingRecordDetailModalActive = false"
          ></button>
        </div>
        <p class="icon-text is-size-5 is-flex is-justify-content-center">
          <strong class="has-text-grey-dark">服用記録</strong>
          <span class="icon has-text-grey-dark mx-2">
            <i class="fa-solid fa-book-open"></i>
          </span>
        </p>
        <div class="block m-3" v-if="takingRecordDetail.hasValue">
          <p class="is-flex is-justify-content-space-between mb-2">
            <strong>お薬</strong>
            <span>
              <a
                class="is-underlined has-text-info has-text-weight-semibold"
                :href="`/medicines/${takingRecordDetail.value.takenMedicine.medicineId}`"
              >
                {{ takingRecordDetail.value.takenMedicine.medicineName }}
              </a>
              <span class="ml-2">{{
                takingRecordDetail.value.takenMedicine.dose
              }}</span>
            </span>
          </p>
          <p class="is-flex is-justify-content-space-between mb-2">
            <strong>お薬を服用した時間</strong>
            <span>{{ takingRecordDetail.value.takenAt }}</span>
          </p>
          <p class="is-flex is-justify-content-space-between mb-2">
            <strong>症状</strong>
            <span>
              <span>{{ takingRecordDetail.value.symptom }}</span>
              (
              <span>{{ takingRecordDetail.value.beforeTaking }}</span>
              <span
                class="icon is-small mx-3"
                v-if="takingRecordDetail.value.afterTaking !== undefined"
              >
                <i class="fa-solid fa-angles-right"></i>
              </span>
              <span v-if="takingRecordDetail.value.afterTaking !== undefined">
                {{ takingRecordDetail.value.afterTaking }}
              </span>
              )
            </span>
          </p>
          <p
            class="has-text-left mb-2"
            v-if="takingRecordDetail.value.note !== ''"
          >
            <strong>ノート</strong>
            <span class="m-2">{{ takingRecordDetail.value.note }}</span>
          </p>
        </div>
        <div class="block">
          <div class="field is-grouped is-grouped-centered">
            <p class="control">
              <a
                class="button is-small is-rounded is-outlined is-link"
                :href="`/takingrecords/${takingRecordDetail.value.takingRecordId}/modify`"
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
                  deleteTakingRecord(takingRecordDetail.value.takingRecordId)
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
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { TakingRecordOverviews } from '@main/js/composables/model/TakingRecordOverviews.js';
import { TakingRecordDetail } from '@main/js/composables/model/TakingRecordDetail.js';
import {
  HttpRequestClient,
  HttpRequestFailedError,
} from '@main/js/composables/HttpRequestClient.js';
import ResultMessage from '@main/js/components/ResultMessage.vue';

const props = defineProps({
  medicineId: String,
  csrf: String,
});

const takingRecordOverviews = reactive(
  new TakingRecordOverviews({
    medicineid: props.medicineId,
  })
);

const isTakingRecordDetailModalActive = ref(false);
const takingRecordDetail = reactive(new TakingRecordDetail());

const resultMessage = ref(null);

onMounted(() => {
  loadTakingRecordOverviews();
});

function loadTakingRecordOverviews() {
  takingRecordOverviews.load().catch(() => {
    resultMessage.value.activate('ERROR', '服用記録の読み込みに失敗しました。');
  });
}

function activateTakingRecordDetailModal(takingRecordId) {
  takingRecordDetail
    .load(takingRecordId)
    .then(() => {
      isTakingRecordDetailModalActive.value = true;
    })
    .catch(() => {
      resultMessage.value.activate(
        'ERROR',
        'エラーが発生しました。',
        '通信状態をご確認のうえ、再度お試しください。'
      );
    });
}

function deleteTakingRecord(takingRecordId) {
  const form = new FormData();
  form.set('_csrf', props.csrf);

  HttpRequestClient.submitPostRequest(
    `/api/takingrecords/${takingRecordId}/delete`,
    form
  )
    .then(() => {
      takingRecordOverviews.delete(takingRecordId);
      isTakingRecordDetailModalActive.value = false;
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