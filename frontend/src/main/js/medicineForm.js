import { createApp } from 'vue';
import EffectsInputField from '@main/js/components/medicine/EffectsInputField.vue';
import MedicineOwnerInputField from '@main/js/components/medicine/MedicineOwnerInputField.vue';

createApp({
  methods: {
    goBack() {
      window.history.back();
    },
  },
  components: {
    effects: EffectsInputField,
    'medicine-owner': MedicineOwnerInputField,
  },
}).mount('#medicineForm');
