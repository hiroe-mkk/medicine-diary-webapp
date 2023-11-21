import { createApp } from 'vue';
import noProfileImage from '@main/images/no_profile_image.png';
import medicines from '@main/images/medicines_small.png';
import calendar from '@main/images/calendar_small.png';
import writing from '@main/images/writing_small.png';
import MedicationRecords from '@main/js/components/medicationrecord/MedicationRecordsByDate.vue';
import Members from '@main/js/components/sharedgroup/Members.vue';

createApp({
  data() {
    return {
      noProfileImage: noProfileImage,
      medicines: medicines,
      writing: writing,
      calendar: calendar,
    };
  },
  components: {
    'medication-records': MedicationRecords,
    members: Members,
  },
}).mount('#mypage');
