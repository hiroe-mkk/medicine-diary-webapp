import { createApp } from 'vue';
import UserSearch from '@main/js/components/user/UserSearch.vue';
import noProfileImage from '@main/images/no_profile_image.png';

createApp({
  data() {
    return {
      selectedUser: undefined,
      noProfileImage: noProfileImage,
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
