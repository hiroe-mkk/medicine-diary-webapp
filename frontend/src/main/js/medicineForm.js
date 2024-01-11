import { createApp } from 'vue';
import EffectsInputField from '@main/js/components/medicine/EffectsInputField.vue';
import MedicineOwnerInputField from '@main/js/components/medicine/MedicineOwnerInputField.vue';
import ResultMessage from '@main/js/components/ResultMessage.vue';

createApp({
  components: {
    effects: EffectsInputField,
    'medicine-owner': MedicineOwnerInputField,
    'result-message': ResultMessage,
  },
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
    activateResultMessage(type, message, details) {
      this.$refs.resultMessage.activate(type, message, details);
    },
  },
  provide() {
    return {
      activateResultMessage: this.activateResultMessage,
    };
  },
}).mount('#medicineForm');