<template>
  <div class="container has-text-centered is-max-desktop p-3">
    <div class="notification has-background-white p-3">
      <div class="content mb-3">
        <p class="is-size-5 has-text-weight-bold has-text-grey-dark">
          最近の服用記録
        </p>
      </div>

      <FilteredMedicationRecords
        ref="filteredDedicationRecords"
        :displayRecorder="props.displayRecorder"
        :allowLoadMore="false"
        :elements="['medicine', 'dateTime']"
      ></FilteredMedicationRecords>
    </div>
  </div>
  <ResultMessage ref="resultMessage"></ResultMessage>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { Filter } from '@main/js/composables/model/MedicationRecords.js';
import FilteredMedicationRecords from '@main/js/components/medicationrecord/FilteredMedicationRecords.vue';
import ResultMessage from '@main/js/components/ResultMessage.vue';

const props = defineProps({ displayRecorder: Boolean, csrf: String });

const filter = reactive(new Filter());
const filteredDedicationRecords = ref(null);
const resultMessage = ref(null);

onMounted(async () => {
  filter.sizePerPage = 10;
  filteredDedicationRecords.value.loadMedicationRecords(filter);
});
</script>
