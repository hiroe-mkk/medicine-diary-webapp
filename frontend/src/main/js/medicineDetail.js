import { createApp } from 'vue';
import ChangeableImage from '@main/js/components/ChangeableImage.vue';
import TakingRecords from '@main/js/components/takingrecord/TakingRecords.vue';
import noMedicineImage from '@main/images/no_medicine_image.png';

createApp({
  data() {
    return {
      isDeletionConfirmationModalActive: false,
      isEditModalActive: false,
      noMedicineImage: noMedicineImage,
    };
  },
  methods: {
    activateMedicineImageChangeModal() {
      this.isEditModalActive = false;
      this.$refs.changeableImage.activateChangeModal();
    },
  },
  components: {
    'changeable-image': ChangeableImage,
    'taking-records': TakingRecords,
  },
}).mount('#medicineDetail');