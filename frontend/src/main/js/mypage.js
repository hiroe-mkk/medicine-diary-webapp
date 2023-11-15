import { createApp } from 'vue';
import noProfileImage from '@main/images/no_profile_image.png';
import TakingRecords from '@main/js/components/takingrecord/FilteredTakingRecordsByDate.vue';
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
    'taking-records': TakingRecords,
    'confirmation-message': ConfirmationMessage,
  },
}).mount('#mypage');
