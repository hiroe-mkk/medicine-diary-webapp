<template>
  <div class="content">
    <div
      class="media px-2 is-clickable"
      v-for="(
        takingRecordOverview, takingRecordId
      ) in takingRecordOverviews.values"
      @click="activateTakingRecordDetailModal(takingRecordId)"
    >
      <div class="media-content">
        <div
          class="content has-text-grey-dark is-flex is-justify-content-space-between"
        >
          <p class="mb-0">
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
          <p class="mb-0">
            <span> {{ takingRecordOverview.takenAt }} </span>
            <span class="icon is-small ml-2">
              <i class="fa-solid fa-greater-than"></i>
            </span>
          </p>
        </div>
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
      <div class="notification p-3 is-link is-light">
        <div class="has-text-right">
          <button
            class="delete"
            type="button"
            @click="isTakingRecordDetailModalActive = false"
          ></button>
        </div>
        <div
          class="content has-text-centered my-2 mx-5"
          v-if="takingRecordDetail.hasValue"
        >
          <div class="is-flex is-justify-content-space-between mb-2">
            <strong>服用したお薬</strong>
            <p>
              <a
                class="is-underlined has-text-info has-text-weight-semibold"
                :href="`/medicines/${takingRecordDetail.value.takenMedicine.medicineId}`"
              >
                {{ takingRecordDetail.value.takenMedicine.name }}
              </a>
              <span class="ml-2">{{
                takingRecordDetail.value.takenMedicine.dose
              }}</span>
            </p>
          </div>
          <div class="is-flex is-justify-content-space-between mb-2">
            <strong>お薬を服用した時間</strong>
            <p>{{ takingRecordDetail.value.takenAt }}</p>
          </div>
          <div class="is-flex is-justify-content-space-between mb-2">
            <strong>症状</strong>
            <p>
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
            </p>
          </div>
          <div
            class="has-text-left mb-2"
            v-if="takingRecordDetail.value.note !== ''"
          >
            <strong>ノート</strong>
            <p class="m-2">{{ takingRecordDetail.value.note }}</p>
          </div>
        </div>
        <div class="m-2">
          <div class="field is-grouped is-grouped-centered">
            <p class="control">
              <a
                class="button is-small is-link is-rounded is-outlined"
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
                class="button is-small is-danger is-rounded is-outlined"
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
import ResultMessage from '@main/js/components/ResultMessage.vue';

const props = defineProps({
  medicineId: String,
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
  takingRecordDetail.load(takingRecordId).catch(() => {
    resultMessage.value.activate(
      'ERROR',
      'エラーが発生しました。',
      '通信状態をご確認のうえ、再度お試しください。'
    );
  });
  isTakingRecordDetailModalActive.value = true;
}
</script>