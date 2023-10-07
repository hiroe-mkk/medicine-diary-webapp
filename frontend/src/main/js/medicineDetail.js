import { createApp } from 'vue';

createApp({
  data() {
    return {
      isDeletionConfirmationModalActive: false,
      isEditModalActive: false,
    };
  },
}).mount('#medicineDetail');
