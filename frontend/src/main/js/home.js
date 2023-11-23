import { createApp } from 'vue';
import Navbar from '@main/js/components/Navbar.vue';
import ResultMessage from '@main/js/components/ResultMessage.vue';
import logo_home from '@main/images/logo_home.png';

import medicines from '@main/images/medicines.png';
import writing from '@main/images/writing.png';
import womans from '@main/images/womans.png';
import calendar from '@main/images/calendar.png';
import shopping from '@main/images/shopping.png';

import Authentication from '@main/js/components/Authentication.vue';
import noProfileImage from '@main/images/no_profile_image.png';
import medicines_small from '@main/images/medicines_small.png';
import calendar_small from '@main/images/calendar_small.png';
import writing_small from '@main/images/writing_small.png';
import MedicationRecords from '@main/js/components/medicationrecord/MedicationRecordsByDate.vue';
import Members from '@main/js/components/sharedgroup/Members.vue';

createApp({
  data() {
    return {
      logo: logo_home,
      medicines: medicines,
      shopping: shopping,
      writing: writing,
      calendar: calendar,
      womans: womans,
      noProfileImage: noProfileImage,
      medicines_small: medicines_small,
      writing_small: writing_small,
      calendar_small: calendar_small,
    };
  },
  methods: {
    activateAccountRegistrationModal() {
      this.$refs.authentication.activateAccountRegistrationModal();
    },
  },
  components: {
    navbar: Navbar,
    resultmessage: ResultMessage,
    authentication: Authentication,
    'medication-records': MedicationRecords,
    members: Members,
  },
}).mount('#home');
