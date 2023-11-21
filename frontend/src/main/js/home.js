import { createApp } from 'vue';
import Navbar from '@main/js/components/Navbar.vue';
import ResultMessage from '@main/js/components/ResultMessage.vue';
import logo_home from '@main/images/logo_home.png';
import Authentication from '@main/js/components/Authentication.vue';

createApp({
  data() {
    return {
      logo: logo_home,

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
  },
}).mount('#home');
