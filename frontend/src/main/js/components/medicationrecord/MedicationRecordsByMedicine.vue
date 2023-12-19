<template>
  <div class="container has-text-centered is-max-desktop">
    <div class="content mb-3">
      <p class="is-size-4 has-text-weight-bold has-text-grey-dark">服用記録</p>
    </div>

    <div class="notification has-background-white py-3 px-5">
      <FilteredMedicationRecords
        ref="filteredMedicationRecords"
        :displayRecorder="props.isParticipatingInSharedGroup"
        :isAllowLoadMore="true"
        :isShowAppendButton="false"
        :elements="['symptom', 'dateTime']"
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
  medicineId: String,
  isParticipatingInSharedGroup: Boolean,
  csrf: String,
});
provide('activateResultMessage', activateResultMessage);

const filter = reactive(new Filter());
const filteredMedicationRecords = ref(null);

const resultMessage = ref(null);

onMounted(() => {
  filter.medicineId = props.medicineId;
  filteredMedicationRecords.value.loadMedicationRecords(filter);
});

function activateResultMessage(type, message, details) {
  resultMessage.value.activate(type, message, details);
}
</script>