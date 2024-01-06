import { createApp } from 'vue';
import MedicationRecords from '@main/js/components/medicationrecord/AllMedicationRecords.vue';
import ResultMessage from '@main/js/components/ResultMessage.vue';

createApp({
  components: {
    'medication-records': MedicationRecords,
    'result-message': ResultMessage,
  },
  methods: {
    activateResultMessage(type, message, details) {
      this.$refs.resultMessage.activate(type, message, details);
    },
  },
  provide() {
    return {
      activateResultMessage: this.activateResultMessage,
    };
  },
}).mount('#medicationRecords');