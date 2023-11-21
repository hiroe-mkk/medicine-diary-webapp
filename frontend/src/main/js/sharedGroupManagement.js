import { createApp } from 'vue';
import UserSearch from '@main/js/components/sharedgroup/UserSearch.vue';
import noProfileImage from '@main/images/no_profile_image.png';
import ConfirmationMessage from '@main/js/components/ConfirmationMessage.vue';

createApp({
  data() {
    return {
      noProfileImage: noProfileImage,
    };
  },
  methods: {
    activateUserSearchModal() {
      this.$refs.userSearch.activateSearchModal();
    },
    activateInvitationCancellationConfirmationModal(sharedGroupId, accountId) {
      this.$refs.invitationCancellationConfirmationMessage.activate({
        sharedGroupId,
        accountId,
      });
    },
    activateUnshareConfirmationModal(sharedGroupId) {
      this.$refs.unshareConfirmationMessage.activate({
        sharedGroupId,
      });
    },
  },
  components: {
    'user-search': UserSearch,
    'confirmation-message': ConfirmationMessage,
  },
}).mount('#sharedGroupManagement');