import { createApp } from 'vue';
import noMedicineImage from '@main/images/no_medicine_image.png';
import ChangeableImage from '@main/js/components/ChangeableImage.vue';
import TakingRecords from '@main/js/components/takingrecord/FilteredTakingRecordsByMedicine.vue';
import ConfirmationMessage from '@main/js/components/ConfirmationMessage.vue';

createApp({
  data() {
    return {
      isEditModalActive: false,
      noMedicineImage: noMedicineImage,
    };
  },
  methods: {
    activateMedicineImageChangeModal() {
      this.isEditModalActive = false;
      this.$refs.changeableImage.activateChangeModal();
    },
    activateDeletionConfirmationModal() {
      this.$refs.confirmationMessage.activate();
    },
  },
  components: {
    'changeable-image': ChangeableImage,
    'taking-records': TakingRecords,
    'confirmation-message': ConfirmationMessage,
  },
}).mount('#medicineDetail');