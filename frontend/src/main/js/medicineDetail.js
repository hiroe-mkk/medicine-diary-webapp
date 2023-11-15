import { createApp } from 'vue';
import noMedicineImage from '@main/images/no_medicine_image.png';
import ChangeableImage from '@main/js/components/ChangeableImage.vue';
import MedicationRecords from '@main/js/components/medicationrecord/FilteredMedicationRecordsByMedicine.vue';
import ConfirmationMessage from '@main/js/components/ConfirmationMessage.vue';

createApp({
  data() {
    return {
      isEditModalActive: false,
      noMedicineImage: noMedicineImage,
    };
  },
  methods: {
    goBack() {
      window.history.back();
    },
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
    'medication-records': MedicationRecords,
    'confirmation-message': ConfirmationMessage,
  },
}).mount('#medicineDetail');