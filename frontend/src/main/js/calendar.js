import { createApp } from 'vue';
import Calendar from '@main/js/components/medicationrecord/Calendar.vue';
import ResultMessage from '@main/js/components/ResultMessage.vue';

createApp({
  components: {
    calendar: Calendar,
    'result-message': ResultMessage,
  },
  methods: {
    activateResultMessage(type, message, details) {
      this.$refs.resultMessage.activate(type, message, details);
    },
  },
  provide() {
    return {
      activateResultMessage: this.activateResultMessage,
    };
  },
}).mount('#calendar');