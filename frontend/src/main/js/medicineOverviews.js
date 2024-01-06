import { createApp } from 'vue';
import noMedicineImage from '@main/images/no_medicine_image.png';
import ResultMessage from '@main/js/components/ResultMessage.vue';

createApp({
  components: {
    'result-message': ResultMessage,
  },
  data() {
    return {
      noMedicineImage: noMedicineImage,
      isMedicineImageModalActive: false,
      selectedMedicineImageURL: '',
      type: 'OWNED',
    };
  },
  methods: {
    activateMedicineImageModal(url) {
      this.selectedMedicineImageURL = url;
      this.isMedicineImageModalActive = true;
    },
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