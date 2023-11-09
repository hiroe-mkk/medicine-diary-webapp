import { createApp } from 'vue';
import TakenMedicineInputField from '@main/js/components/takingrecord/TakenMedicineInputField.vue';

createApp({
  components: {
    'taken-medicine': TakenMedicineInputField,
  },
}).mount('#takingRecordForm');
