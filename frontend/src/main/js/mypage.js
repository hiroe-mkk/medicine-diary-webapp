import { createApp } from 'vue';
import noProfileImage from '@main/images/no_profile_image.png';
import TakingRecords from '@main/js/components/takingrecord/FilteredTakingRecordsByDate.vue';

createApp({
  data() {
    return {
      isMenuModalActive: false,
      noProfileImage: noProfileImage,
    };
  },
  components: {
    'taking-records': TakingRecords,
  },
}).mount('#mypage');
