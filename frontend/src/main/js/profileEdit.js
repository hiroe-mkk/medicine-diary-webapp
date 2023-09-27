import { createApp } from 'vue';
import ProfileEdit from '@main/js/components/profile/ProfileEdit.vue';

createApp({
  components: {
    edit: ProfileEdit,
  },
}).mount('#profileEdit');