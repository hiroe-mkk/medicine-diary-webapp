<template>
  <div class="is-flex is-align-items-center is-justify-content-center">
    <div
      class="has-text-centered mx-2"
      v-for="member in props.sharedGroup.members"
    >
      <div class="is-flex is-justify-content-center">
        <a
          class="image is-64x64 m-0"
          :href="`/users/${member.accountId}`"
          v-if="props.isJoined"
        >
          <img
            class="is-rounded"
            :src="member.profileImageURL"
            alt=""
            v-if="member.profileImageURL !== undefined"
          />
          <img
            class="is-rounded"
            src="@main/images/no_profile_image.png"
            alt=""
            v-if="member.profileImageURL === undefined"
          />
        </a>
        <figure class="image is-64x64 m-0" v-if="!props.isJoined">
          <img
            class="is-rounded"
            :src="member.profileImageURL"
            alt=""
            v-if="member.profileImageURL !== undefined"
          />
          <img
            class="is-rounded"
            src="@main/images/no_profile_image.png"
            alt=""
            v-if="member.profileImageURL === undefined"
          />
        </figure>
      </div>
      <p class="has-text-weight-bold has-text-grey-dark m-0 p-0">
        {{ member.username }}
      </p>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  sharedGroup: Object,
  isJoined: Boolean,
  csrf: String,
});
</script>
