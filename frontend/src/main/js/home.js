import { createApp } from 'vue';
import Navbar from '@main/js/components/Navbar.vue';
import ResultMessage from '@main/js/components/ResultMessage.vue';
import noProfileImage from '@main/images/no_profile_image.png';
import Authentication from '@main/js/components/Authentication.vue';
import MedicationRecords from '@main/js/components/medicationrecord/MedicationRecordsByDate.vue';
import Members from '@main/js/components/sharedgroup/Members.vue';

createApp({
  components: {
    navbar: Navbar,
    authentication: Authentication,
    'medication-records': MedicationRecords,
    members: Members,
    'result-message': ResultMessage,
  },
  data() {
    return {
      noProfileImage: noProfileImage,
    };
  },
  methods: {
    activateAccountRegistrationModal() {
      this.$refs.authentication.activateAccountRegistrationModal();
    },
    activateResultMessage(type, message, details) {
      this.$refs.resultMessage.activate(type, message, details);
    },
  },
  provide() {
    return {
      activateResultMessage: this.activateResultMessage,
    };
  },
}).mount('#home');