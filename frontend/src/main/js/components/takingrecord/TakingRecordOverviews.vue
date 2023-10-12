<template>
  <div class="content">
    <div
      class="media px-2"
      v-for="(
        takingRecordOverview, takingRecordId
      ) in takingRecordOverviews.values"
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
            <span v-if="takingRecordOverview.afterTaking !== undefined">{{
              takingRecordOverview.afterTaking
            }}</span>
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
    <div class="has-text-centered mt-2" v-if="takingRecordOverviews.canLoadMore"> 
      <button
        class="button is-small is-ghost"
        type="button"
        @click="loadTakingRecordOverviews()"
      >
        さらに表示する
      </button>
    </div>
  </div>

  <ResultMessage ref="resultMessage"></ResultMessage>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { TakingRecordOverviews } from '@main/js/composables/model/TakingRecordOverviews.js';
import ResultMessage from '@main/js/components/ResultMessage.vue';

const props = defineProps({
  medicineId: String,
});

const takingRecordOverviews = reactive(
  new TakingRecordOverviews({
    medicineid: props.medicineId,
  })
);

const resultMessage = ref(null);

onMounted(() => {
  loadTakingRecordOverviews();
});

function loadTakingRecordOverviews() {
  takingRecordOverviews.load().catch(() => {
    resultMessage.value.activate('ERROR', '服用記録の読み込みに失敗しました。');
  });
}
</script>
