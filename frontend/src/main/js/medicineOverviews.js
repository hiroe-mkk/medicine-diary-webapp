import { createApp } from 'vue';
import OwnerBaseMedicineOverviews from '@main/js/components/medicine/OwnerBaseMedicineOverviews.vue';
import ResultMessage from '@main/js/components/ResultMessage.vue';

createApp({
  components: {
    'result-message': ResultMessage,
    'medicine-overviews': OwnerBaseMedicineOverviews,
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
}).mount('#medicineOverviews');
