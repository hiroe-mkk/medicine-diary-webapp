import { createApp } from 'vue';
import Navbar from '@main/js/components/Navbar.vue';
import noProfileImage from '@main/images/no_profile_image.png';
import Authentication from '@main/js/components/Authentication.vue';
import MedicationRecords from '@main/js/components/medicationrecord/MedicationRecordsByDate.vue';
import Members from '@main/js/components/sharedgroup/Members.vue';
import ResultMessage from '@main/js/components/ResultMessage.vue';

createApp({
  data() {
    return {
      noProfileImage: noProfileImage,
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
