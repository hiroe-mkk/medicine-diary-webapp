import { createApp } from 'vue';
import Navbar from '@main/js/components/Navbar.vue';

createApp({
  components: {
    navbar: Navbar,
  },
}).mount('#header');
