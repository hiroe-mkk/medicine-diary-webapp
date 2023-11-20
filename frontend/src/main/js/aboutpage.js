import { createApp } from 'vue';
import smartphone_illustration from '@main/images/smartphone_illustration.png';
import medicine_illustration from '@main/images/medicine_illustration.png';
import share_illustration from '@main/images/share_illustration.png';
import shopping_illustration from '@main/images/shopping_illustration.png';

createApp({
  data() {
    return {
      smartphone: smartphone_illustration,
      medicine: medicine_illustration,
      share: share_illustration,
      shopping: shopping_illustration,
    };
  },
}).mount('#aboutpage');