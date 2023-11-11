import { createApp } from 'vue';
import TakingRecords from '@main/js/components/takingrecord/FilteredTakingRecord.vue';

createApp({
  components: {
    'taking-records': TakingRecords,
  },
}).mount('#takingRecords');
