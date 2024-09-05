<template>
  <div class="container has-text-centered is-max-desktop">
    <div class="content mb-3">
      <p class="is-size-4 has-text-weight-bold has-text-grey-dark">服用記録</p>
    </div>

    <div class="notification has-background-white py-3 px-5">
      <FilteredMedicationRecords
        ref="filteredMedicationRecords"
        :displayRecorder="props.isJoinedSharedGroup"
        :isAllowLoadMore="true"
        :isShowAppendButton="false"
        :elements="['symptom', 'dateTime']"
        :csrf="props.csrf"
      ></FilteredMedicationRecords>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, inject } from 'vue';
import { Filter } from '@main/js/composables/model/MedicationRecords.js';
import FilteredMedicationRecords from '@main/js/components/medicationrecord/FilteredMedicationRecords.vue';

const props = defineProps({
  medicineId: String,
  isJoinedSharedGroup: Boolean,
  csrf: String,
});
const activateResultMessage = inject('activateResultMessage');

const filter = reactive(new Filter());
const filteredMedicationRecords = ref(null);

onMounted(() => {
  filter.medicineId = props.medicineId;
  filteredMedicationRecords.value.loadMedicationRecords(filter);
});
</script>
