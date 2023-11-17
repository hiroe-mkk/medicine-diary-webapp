import { createApp } from 'vue';
import noMedicineImage from '@main/images/no_medicine_image.png';
import ChangeableImage from '@main/js/components/ChangeableImage.vue';
import Inventory from '@main/js/components/medicine/Inventory.vue';
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
    activateMedicineImageChangeModal() {
      this.isEditModalActive = false;
      this.$refs.changeableImage.activateChangeModal();
    },
    activateDeletionConfirmationModal() {
      this.isEditModalActive = false;
      this.$refs.confirmationMessage.activate();
    },
    activateInventoryAdjustmentModal() {
      this.isEditModalActive = false;
      this.$refs.inventory.activateInventoryAdjustmentModal();
    },
  },
  components: {
    'changeable-image': ChangeableImage,
    inventory: Inventory,
    'medication-records': MedicationRecords,
    'confirmation-message': ConfirmationMessage,
  },
}).mount('#medicineDetail');
