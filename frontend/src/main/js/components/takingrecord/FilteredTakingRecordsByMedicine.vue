<template>
  <div class="container is-max-desktop p-3" v-if="takingRecords.size !== 0">
    <div class="content has-text-centered">
      <p class="icon-text is-size-4 is-flex is-justify-content-center">
        <strong class="has-text-grey-dark">服用記録</strong>
        <span class="icon has-text-grey-dark mx-2">
          <i class="fa-solid fa-book-open"></i>
        </span>
      </p>
    </div>

    <div class="content m-2" v-if="takingRecords.size !== 0">
      <div
        class="media is-flex is-align-items-center is-clickable p-3 m-0"
        v-for="(takingRecord, takingRecordId) in takingRecords.values"
        @click="activateTakingRecordModal(takingRecordId)"
      >
        <div class="media-left">
          <figure class="image is-64x64 m-0">
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
              {{ takingRecord.followUp.symptom }}
            </span>
            <span
              v-html="
                TakingRecordUtils.toIcon(takingRecord.followUp.beforeTaking)
              "
            ></span>
            <span
              class="icon is-small"
              v-if="takingRecord.followUp.afterTaking !== undefined"
            >
              <i class="fa-solid fa-angles-right"></i>
            </span>
            <span
              v-if="takingRecord.followUp.afterTaking !== undefined"
              v-html="
                TakingRecordUtils.toIcon(takingRecord.followUp.afterTaking)
              "
            ></span>
          </p>
          <p class="has-text-right m-0">
            <span> {{ takingRecord.takenAt }} </span>
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
  </div>

  <TakingRecord
    ref="takingRecord"
    :csrf="props.csrf"
    @deleted="takingRecordDeleted"
  ></TakingRecord>
  <ResultMessage ref="resultMessage"></ResultMessage>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import {
  TakingRecords,
  Filter,
  TakingRecordUtils,
} from '@main/js/composables/model/TakingRecords.js';
import { HttpRequestClient } from '@main/js/composables/HttpRequestClient.js';
import ResultMessage from '@main/js/components/ResultMessage.vue';
import TakingRecord from '@main/js/components/takingrecord/TakingRecord.vue';
import noProfileImage from '@main/images/no_profile_image.png';

const props = defineProps({
  medicineId: String,
  csrf: String,
});

const takingRecords = reactive(new TakingRecords());
const takingRecord = ref(null);

const resultMessage = ref(null);

onMounted(async () => {
  await HttpRequestClient.submitGetRequest('/api/users?member')
    .then((data) => {
      const filter = new Filter();
      filter.initializeMembers(data.users.map((user) => user.accountId));
      filter.medicine = props.medicineId;
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
  takingRecord.value.activateTakingRecordModal(
    takingRecords.getTakingRecord(takingRecordId)
  );
}

function takingRecordDeleted(takingRecordId) {
  takingRecords.delete(takingRecordId);
}
</script>
