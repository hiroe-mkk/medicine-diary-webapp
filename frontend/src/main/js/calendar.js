import { createApp } from 'vue';
import Calendar from '@main/js/components/medicationrecord/Calendar.vue';

createApp({
  components: {
    calendar: Calendar,
  },
}).mount('#calendar');