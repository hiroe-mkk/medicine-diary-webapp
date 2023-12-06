<template>
  <div class="is-flex is-align-items-center is-justify-content-center">
    <div
      class="has-text-centered mx-2"
      v-for="member in props.sharedGroup.members"
    >
      <p class="is-size-7 m-0" v-if="props.sharedGroup.invitees.length !== 0">
        　
      </p>
      <div class="is-flex is-justify-content-center">
        <a
          class="image is-64x64 m-0"
          :href="`/users/${member.accountId}`"
          v-if="props.isParticipating"
        >
          <img
            class="is-rounded"
            :src="member.profileImageURL"
            v-if="member.profileImageURL !== undefined"
          />
          <img
            class="is-rounded"
            src="@main/images/no_profile_image.png"
            v-if="member.profileImageURL === undefined"
          />
        </a>
        <figure class="image is-64x64 m-0" v-if="!props.isParticipating">
          <img
            class="is-rounded"
            :src="member.profileImageURL"
            v-if="member.profileImageURL !== undefined"
          />
          <img
            class="is-rounded"
            src="@main/images/no_profile_image.png"
            v-if="member.profileImageURL === undefined"
          />
        </figure>
      </div>
      <p class="has-text-weight-bold has-text-grey-dark m-0 p-0">
        {{ member.username }}
      </p>
    </div>

    <div
      class="has-text-centered mx-2"
      v-for="invitee in props.sharedGroup.invitees"
    >
      <p class="is-size-7 has-text-grey m-0">招待中</p>
      <div class="is-flex is-justify-content-center">
        <figure
          class="image is-64x64 is-clickable m-0"
          v-if="props.isParticipating"
          @click="
            selectedUser = invitee.accountId;
            isInvitationCancellationConfirmationModalActive = true;
          "
        >
          <img
            class="is-rounded"
            :src="invitee.profileImageURL"
            v-if="invitee.profileImageURL !== undefined"
          />
          <img
            class="is-rounded"
            src="@main/images/no_profile_image.png"
            v-if="invitee.profileImageURL === undefined"
          />
        </figure>
        <figure class="image is-64x64 m-0" v-if="!props.isParticipating">
          <img
            class="is-rounded"
            :src="invitee.profileImageURL"
            v-if="invitee.profileImageURL !== undefined"
          />
          <img
            class="is-rounded"
            src="@main/images/no_profile_image.png"
            v-if="invitee.profileImageURL === undefined"
          />
        </figure>
      </div>
      <p class="has-text-weight-bold has-text-grey-dark m-0 p-0">
        {{ invitee.username }}
      </p>
    </div>

    <div
      class="modal"
      :class="{ 'is-active': isInvitationCancellationConfirmationModalActive }"
    >
      <div
        class="modal-background"
        @click="isInvitationCancellationConfirmationModalActive = false"
      ></div>
      <div class="modal-content is-flex is-justify-content-center">
        <div class="message is-inline-block is-info">
          <div class="message-body">
            <div class="content">
              <p class="has-text-centered mb-2">
                <strong class="is-size-5 mb-1">
                  このユーザーの招待を取り消しますか？
                </strong>
                <br />
              </p>
              <form class="form" method="post" action="/shared-group/cancel">
                <input name="_csrf" :value="props.csrf" hidden />
                <div class="field is-grouped is-grouped-centered p-2">
                  <p class="control">
                    <button class="button is-small is-rounded is-link">
                      取り消す
                    </button>
                  </p>
                  <p class="control">
                    <button
                      type="button"
                      class="button is-small is-rounded is-outlined is-danger"
                      @click="
                        isInvitationCancellationConfirmationModalActive = false
                      "
                    >
                      キャンセル
                    </button>
                  </p>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const props = defineProps({
  sharedGroup: Object,
  isParticipating: Boolean,
  csrf: String,
});

const isInvitationCancellationConfirmationModalActive = ref(false);
const selectedUser = ref('');
</script>
