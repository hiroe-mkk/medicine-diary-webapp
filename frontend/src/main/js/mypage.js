import { createApp } from 'vue';
import noProfileImage from '@main/images/no_profile_image.png';
import MedicationRecords from '@main/js/components/medicationrecord/MedicationRecordsByDate.vue';

createApp({
  data() {
    return {
      isMenuModalActive: false,
      noProfileImage: noProfileImage,
    };
  },
  components: {
    'medication-records': MedicationRecords,
  },
}).mount('#mypage');