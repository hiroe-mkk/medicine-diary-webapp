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
        :displayRecorder="props.isJoinedSharedGroup"
        :isAllowLoadMore="false"
        :isShowAppendButton="false"
        :elements="['medicine', 'dateTime']"
        :csrf="props.csrf"
      ></FilteredMedicationRecords>
    </div>
  </div>
</template>

<script setup>
import FilteredMedicationRecords from '@main/js/components/medicationrecord/FilteredMedicationRecords.vue';
import { Filter } from '@main/js/composables/model/MedicationRecords.js';
import { onMounted, reactive, ref } from 'vue';

const props = defineProps({
  isJoinedSharedGroup: Boolean,
  csrf: String,
});

const filter = reactive(new Filter());
const filteredMedicationRecords = ref(null);

onMounted(() => {
  filter.sizePerPage = 5;
  filteredMedicationRecords.value.loadMedicationRecords(filter);
});
</script>
