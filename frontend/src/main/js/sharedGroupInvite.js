import { createApp } from 'vue';
import SharedGroupInvite from '@main/js/components/sharedgroup/SharedGroupInvite.vue';
import ResultMessage from '@main/js/components/ResultMessage.vue';

createApp({
  components: {
    'shared-group-invite': SharedGroupInvite,
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
}).mount('#sharedGroupInvite');
