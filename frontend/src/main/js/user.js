import { createApp } from 'vue';
import Calendar from '@main/js/components/medicationrecord/Calendar.vue';
import noProfileImage from '@main/images/no_profile_image.png';

createApp({
  data() {
    return {
      noProfileImage: noProfileImage,
    };
  },
  components: {
    calendar: Calendar,
  },
}).mount('#user');
