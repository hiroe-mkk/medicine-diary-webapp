import { createApp } from 'vue';
import noMedicineImage from '@main/images/no_medicine_image.png';
import ChangeableImage from '@main/js/components/ChangeableImage.vue';
import Inventory from '@main/js/components/medicine/Inventory.vue';
import MedicationRecords from '@main/js/components/medicationrecord/MedicationRecordsByMedicine.vue';
import ResultMessage from '@main/js/components/ResultMessage.vue';

createApp({
  components: {
    'changeable-image': ChangeableImage,
    inventory: Inventory,
    'medication-records': MedicationRecords,
    'result-message': ResultMessage,
  },
  data() {
    return {
      isMenuModalActive: false,
      isInventoryManagementEnabled: false,
      noMedicineImage: noMedicineImage,
      isMedicineDeletionConfirmationModalActive: false,
    };
  },
  methods: {
    activateMedicineImageMenuModal() {
      this.isMenuModalActive = false;
      this.$refs.changeableImage.activateMenuModal();
    },
    activateInventoryMenuModal() {
      this.isMenuModalActive = false;
      this.$refs.inventory.activateMenuModal();
    },
    activateInventoryAdjustmentModal() {
      this.isMenuModalActive = false;
      this.$refs.inventory.activateAdjustmentModal();
    },
    activateInventoryManagementStoppageConfirmationModal() {
      this.isMenuModalActive = false;
      this.$refs.inventory.activateStoppageConfirmationModal();
    },
    activateDeletionConfirmationModal() {
      this.isMenuModalActive = false;
      this.isMedicineDeletionConfirmationModalActive = true;
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
}).mount('#medicineDetail');
