import { createApp } from 'vue';
import noMedicineImage from '@main/images/no_medicine_image.png';

createApp({
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
  },
}).mount('#medicineOverviews');
