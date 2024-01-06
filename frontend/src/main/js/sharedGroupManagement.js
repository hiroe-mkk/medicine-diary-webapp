import { createApp } from 'vue';
import SharedGroupManagement from '@main/js/components/sharedgroup/SharedGroupManagement.vue';
import ResultMessage from '@main/js/components/ResultMessage.vue';

createApp({
  components: {
    'shared-group-management': SharedGroupManagement,
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
}).mount('#sharedGroupManagement');