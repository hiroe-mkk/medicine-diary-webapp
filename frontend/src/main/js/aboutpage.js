import { createApp } from 'vue';
import medicines from '@main/images/medicines.png';
import writing from '@main/images/writing.png';
import womans from '@main/images/womans.png';
import calendar from '@main/images/calendar.png';
import shopping from '@main/images/shopping.png';

createApp({
  data() {
    return {
      medicines: medicines,
      shopping: shopping,
      writing: writing,
      calendar: calendar,
      womans: womans,
    };
  },
}).mount('#aboutpage');
