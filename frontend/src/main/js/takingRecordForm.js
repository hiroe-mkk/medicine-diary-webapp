import { createApp } from 'vue';
import TakenMedicineInputField from '@main/js/components/takingrecord/TakenMedicineInputField.vue';

createApp({
  methods: {
    goBack() {
      window.history.back();
    },
  },
  components: {
    'taken-medicine': TakenMedicineInputField,
  },
}).mount('#takingRecordForm');
