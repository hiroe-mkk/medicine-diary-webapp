import { createApp } from 'vue';
import Setting from '@main/js/components/Setting.vue';
import ResultMessage from '@main/js/components/ResultMessage.vue';

createApp({
  components: {
    'setting': Setting,
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
}).mount('#setting');