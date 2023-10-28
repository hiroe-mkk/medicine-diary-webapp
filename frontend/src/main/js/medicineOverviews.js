import { createApp } from 'vue';
import noMedicineImage from '@main/images/no_medicine_image.png';

createApp({
  data() {
    return {
      noMedicineImage: noMedicineImage,
    };
  },
}).mount('#medicineOverviews');