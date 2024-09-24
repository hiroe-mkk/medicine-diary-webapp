import ResultMessage from '@main/js/components/ResultMessage.vue';
import Members from '@main/js/components/sharedgroup/Members.vue';
import { createApp } from 'vue';

createApp({
  components: {
    members: Members,
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
}).mount('#sharedGroupJoin');
