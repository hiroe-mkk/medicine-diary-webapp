import { createApp } from 'vue';
import noProfileImage from '@main/images/no_profile_image.png';

createApp({
  data() {
    return {
      isMenuModalActive: false,
      noProfileImage: noProfileImage,
    };
  },
}).mount('#mypage');
