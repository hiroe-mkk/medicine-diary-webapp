<template>
  <div class="container has-text-centered is-max-desktop">
    <div class="notification has-background-white py-3 px-5">
      <div class="content m-0">
        <p class="is-size-4 has-text-weight-bold has-text-grey-dark mb-0">
          最近の服用記録
        </p>
        <p class="has-text-right">
          <a
            class="icon-text is-size-7"
            style="text-decoration: none"
            href="/medication-records"
          >
            <strong class="has-text-link">もっと見る</strong>
            <span class="icon has-text-link m-0">
              <i class="fa-solid fa-angle-right"></i>
            </span>
          </a>
        </p>
      </div>

      <FilteredMedicationRecords
        ref="filteredMedicationRecords"
        :displayRecorder="props.isParticipatingInSharedGroup"
        :isAllowLoadMore="false"
        :isShowAppendButton="false"
        :elements="['medicine', 'dateTime']"
        :csrf="props.csrf"
      ></FilteredMedicationRecords>
    </div>
  </div>
  <ResultMessage ref="resultMessage"></ResultMessage>
</template>

<script setup>
import { ref, reactive, onMounted, provide } from 'vue';
import { Filter } from '@main/js/composables/model/MedicationRecords.js';
import FilteredMedicationRecords from '@main/js/components/medicationrecord/FilteredMedicationRecords.vue';
import ResultMessage from '@main/js/components/ResultMessage.vue';

const props = defineProps({
  isParticipatingInSharedGroup: Boolean,
  csrf: String,
});
provide('activateResultMessage', activateResultMessage);

const filter = reactive(new Filter());
const filteredMedicationRecords = ref(null);
const resultMessage = ref(null);

onMounted(() => {
  filter.sizePerPage = 5;
  filteredMedicationRecords.value.loadMedicationRecords(filter);
});

function activateResultMessage(type, message, details) {
  resultMessage.value.activate(type, message, details);
}
</script>