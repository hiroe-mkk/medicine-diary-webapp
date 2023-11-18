import { createApp } from 'vue';
import EffectsInputField from '@main/js/components/medicine/EffectsInputField.vue';
import MedicineOwnerInputField from '@main/js/components/medicine/MedicineOwnerInputField.vue';

createApp({
  data() {
    return {
      doseUnitOptions: [
        'カプセル',
        '錠',
        '包',
        'mg',
        'μg',
        'mL',
        'g',
        '%',
        'mL/h',
      ],
    };
  },
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
