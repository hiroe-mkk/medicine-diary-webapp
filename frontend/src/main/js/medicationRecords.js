import { createApp } from 'vue';
import MedicationRecords from '@main/js/components/medicationrecord/FilteredMedicationRecords.vue';

createApp({
  components: {
    'medication-records': MedicationRecords,
  },
}).mount('#medicationRecords');