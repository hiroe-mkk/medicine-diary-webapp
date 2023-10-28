import { createApp } from 'vue';
import UserSearch from '@main/js/components/user/UserSearch.vue';

createApp({
  data() {
    return {
      selectedUser: undefined,
    };
  },
  methods: {
    activateUserSearchModal() {
      this.$refs.userSearch.activateSearchModal();
    },
  },
  components: {
    'user-search': UserSearch,
  },
}).mount('#sharedGroupManagement');
