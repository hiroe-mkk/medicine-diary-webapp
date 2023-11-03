import { createApp } from 'vue';
import UserSearch from '@main/js/components/user/UserSearch.vue';
import noProfileImage from '@main/images/no_profile_image.png';

createApp({
  data() {
    return {
      selectedUser: undefined,
      noProfileImage: noProfileImage,
      isInvitationCancellationConfirmationModalActive: false,
      selectedUser: undefined,
    };
  },
  methods: {
    activateUserSearchModal() {
      this.$refs.userSearch.activateSearchModal();
    },
    activateInvitationCancellationConfirmationModal(sharedGroupId, accountId) {
      this.isInvitationCancellationConfirmationModalActive = true;
      this.selectedUser = { accountId, sharedGroupId };
    },
  },
  components: {
    'user-search': UserSearch,
  },
}).mount('#sharedGroupManagement');
