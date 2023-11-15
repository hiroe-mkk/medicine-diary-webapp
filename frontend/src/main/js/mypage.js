import { createApp } from 'vue';
import noProfileImage from '@main/images/no_profile_image.png';
import MedicationRecords from '@main/js/components/medicationrecord/FilteredMedicationRecordsByDate.vue';
import ConfirmationMessage from '@main/js/components/ConfirmationMessage.vue';

createApp({
  data() {
    return {
      isMenuModalActive: false,
      noProfileImage: noProfileImage,
    };
  },
  methods: {
    activateAccountDeletionConfirmationModal() {
      this.$refs.accountDeletionConfirmationMessage.activate();
    },
  },
  components: {
    'medication-records': MedicationRecords,
    'confirmation-message': ConfirmationMessage,
  },
}).mount('#mypage');