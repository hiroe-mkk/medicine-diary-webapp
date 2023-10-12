import { createApp } from 'vue';
import ChangeableImage from '@main/js/components/ChangeableImage.vue';
import TakingRecordOverviews from '@main/js/components/takingrecord/TakingRecordOverviews.vue';

createApp({
  data() {
    return {
      isDeletionConfirmationModalActive: false,
      isEditModalActive: false,
    };
  },
  methods: {
    activateMedicineImageChangeModal() {
      this.isEditModalActive = false;
      this.$refs.changeableImage.activate();
    },
  },
  components: {
    'changeable-image': ChangeableImage,
    'taking-records': TakingRecordOverviews,
  },
}).mount('#medicineDetail');
