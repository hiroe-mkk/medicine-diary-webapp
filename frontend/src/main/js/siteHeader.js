import { createApp } from 'vue';
import Navbar from '@main/js/components/Navbar.vue';
import ResultMessage from '@main/js/components/ResultMessage.vue';

createApp({
  components: {
    navbar: Navbar,
    resultmessage: ResultMessage,
  },
}).mount('#header');
