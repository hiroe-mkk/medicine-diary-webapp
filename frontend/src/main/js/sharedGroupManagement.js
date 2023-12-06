import { createApp } from 'vue';
import SharedGroupManagement from '@main/js/components/sharedgroup/SharedGroupManagement.vue';

createApp({
  components: {
    'shared-group-management': SharedGroupManagement,
  },
}).mount('#sharedGroupManagement');
