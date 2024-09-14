<template>
  <div class="container has-text-centered is-max-desktop">
    <div class="content mb-3">
      <p class="is-size-4 has-text-weight-bold has-text-grey-dark">
        服用記録一覧
      </p>
    </div>

    <div class="content is-inline-block m-2" v-if="members.length !== 0">
      <div class="is-flex is-align-items-center">
        <div
          class="is-clickable mx-2"
          :class="{ opacity: !isUserEnabled(member.accountId) }"
          @click="toggleUserActive(member.accountId)"
          v-for="member in members"
        >
          <div class="is-flex is-justify-content-center">
            <figure class="image is-48x48 m-0">
              <img
                class="is-rounded"
                :src="member.profileImageURL || noProfileImage"
                alt=""
              />
            </figure>
          </div>
          <p
            class="is-size-7 has-text-weight-bold has-text-grey-dark"
            v-if="member.username !== ''"
          >
            {{ member.username }}
          </p>
          <p class="is-size-7 has-text-weight-bold has-text-grey" v-else>
            ( unknown )
          </p>
        </div>
      </div>
    </div>

    <div class="notification has-background-white py-3 px-5">
      <FilteredMedicationRecords
        ref="filteredMedicationRecords"
        :displayRecorder="props.joinedSharedGroupId !== undefined"
        :isAllowLoadMore="true"
        :isShowAppendButton="false"
        :elements="['medicine', 'dateTime']"
        :csrf="props.csrf"
      ></FilteredMedicationRecords>
    </div>
  </div>
</template>

<script setup>
import noProfileImage from '@main/images/no_profile_image.png';
import FilteredMedicationRecords from '@main/js/components/medicationrecord/FilteredMedicationRecords.vue';
import { HttpRequestClient } from '@main/js/composables/HttpRequestClient.js';
import { Filter } from '@main/js/composables/model/MedicationRecords.js';
import { inject, onMounted, reactive, ref } from 'vue';

const props = defineProps({
  joinedSharedGroupId: { type: String, default: undefined },
  csrf: String,
});
const activateResultMessage = inject('activateResultMessage');

const members = reactive([]);
const filter = reactive(new Filter());
const filteredMedicationRecords = ref(null);

onMounted(() => {
  if (props.joinedSharedGroupId !== undefined) {
    HttpRequestClient.submitGetRequest(
      `/api/users?sharedGroupId=${props.joinedSharedGroupId}`,
      activateResultMessage
    ).then((data) => {
      members.push(...data.users);
      filteredMedicationRecords.value.loadMedicationRecords(filter);
    });
  }
});

function toggleUserActive(accountId) {
  if (filter.accountId === accountId) {
    filter.accountId = undefined;
  } else {
    filter.accountId = accountId;
  }
  filteredMedicationRecords.value.loadMedicationRecords(filter);
}

function isUserEnabled(accountId) {
  return filter.accountId === undefined || filter.accountId === accountId;
}
</script>

<style scoped>
.opacity {
  opacity: 0.3;
}
</style>
