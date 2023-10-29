import { createApp } from 'vue';
import EditableProfile from '@main/js/components/profile/EditableProfile.vue';

createApp({
  components: {
    'editable-profile': EditableProfile,
  },
}).mount('#profileEdit');
