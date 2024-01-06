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
          :class="{ opacity: !isUserEnabled(self.value.accountId) }"
          @click="toggleUserActive(self.value.accountId)"
          v-if="self.value !== undefined"
        >
          <div class="is-flex is-justify-content-center">
            <figure class="image is-48x48 m-0">
              <img
                class="is-rounded"
                :src="self.value.profileImageURL"
                v-if="self.value.profileImageURL !== undefined"
              />
              <img
                class="is-rounded"
                src="@main/images/no_profile_image.png"
                v-if="self.value.profileImageURL === undefined"
              />
            </figure>
          </div>
          <p class="is-size-7 has-text-weight-bold has-text-grey-dark m-0 p-0">
            {{ self.value.username }}
          </p>
        </div>
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
          <p class="is-size-7 has-text-weight-bold has-text-grey-dark m-0 p-0">
            {{ member.username }}
          </p>
        </div>
      </div>
    </div>

    <div class="notification has-background-white py-3 px-5">
      <FilteredMedicationRecords
        ref="filteredMedicationRecords"
        :displayRecorder="props.isParticipatingInSharedGroup"
        :isAllowLoadMore="true"
        :isShowAppendButton="false"
        :elements="['medicine', 'dateTime']"
        :csrf="props.csrf"
      ></FilteredMedicationRecords>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, inject } from 'vue';
import { Filter } from '@main/js/composables/model/MedicationRecords.js';
import { HttpRequestClient } from '@main/js/composables/HttpRequestClient.js';
import FilteredMedicationRecords from '@main/js/components/medicationrecord/FilteredMedicationRecords.vue';

const props = defineProps({
  isParticipatingInSharedGroup: Boolean,
  csrf: String,
});
const activateResultMessage = inject('activateResultMessage');

const self = reactive({ value: undefined });
const members = reactive([]);
const filter = reactive(new Filter());
const filteredMedicationRecords = ref(null);

onMounted(() => {
  HttpRequestClient.submitGetRequest('/api/users?self')
    .then((data) => {
      self.value = data;
    })
    .catch(() => {
      activateResultMessage(
        'ERROR',
        'エラーが発生しました。',
        '通信状態をご確認のうえ、再度お試しください。'
      );
    });

  if (props.isParticipatingInSharedGroup) {
    HttpRequestClient.submitGetRequest('/api/users?members')
      .then((data) => {
        members.push(...data.users);
      })
      .catch(() => {
        activateResultMessage(
          'ERROR',
          'エラーが発生しました。',
          '通信状態をご確認のうえ、再度お試しください。'
        );
      });
  }

  filteredMedicationRecords.value.loadMedicationRecords(filter);
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