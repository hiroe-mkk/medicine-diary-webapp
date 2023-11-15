import { createApp } from 'vue';
import TakenMedicineInputField from '@main/js/components/medicationrecord/TakenMedicineInputField.vue';

createApp({
  methods: {
    goBack() {
      window.history.back();
    },
  },
  components: {
    'taken-medicine': TakenMedicineInputField,
  },
}).mount('#medicationRecordForm');