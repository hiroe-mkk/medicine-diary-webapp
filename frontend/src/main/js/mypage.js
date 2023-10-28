import { createApp } from 'vue';
import EditableProfile from '@main/js/components/profile/EditableProfile.vue';
import noProfileImage from '@main/images/no_profile_image.png';

createApp({
  data() {
    return {
      noProfileImage: noProfileImage,
    };
  },
  components: {
    'editable-profile': EditableProfile,
  },
}).mount('#mypage');
