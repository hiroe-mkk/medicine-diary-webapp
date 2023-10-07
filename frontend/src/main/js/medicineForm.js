import { createApp } from 'vue';
import EffectsInputField from '@main/js/components/medicine/EffectsInputField.vue';

createApp({
  components: {
    effects: EffectsInputField,
  },
}).mount('#medicineForm');