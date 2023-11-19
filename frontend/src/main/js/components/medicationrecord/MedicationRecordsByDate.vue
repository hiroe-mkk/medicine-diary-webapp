<template>
  <div class="container has-text-centered is-max-desktop p-3">
    <div class="notification has-background-white p-3">
      <div class="content mb-3">
        <p class="is-size-4 m-3">
          <strong class="has-text-grey-dark">
            {{ todayWithoutYear() }}
          </strong>
          <strong class="has-text-grey-dark mx-2">の服用記録</strong>
        </p>
      </div>

      <div class="content is-inline-block pt-2 m-0" v-if="members.size !== 0">
        <div class="is-flex is-align-items-center">
          <div
            class="is-clickable mx-2"
            :class="{ opacity: !filter.isUserActive(self.value.accountId) }"
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
                  :src="noProfileImage"
                  v-if="self.value.profileImageURL === undefined"
                />
              </figure>
            </div>
            <p
              class="is-size-7 has-text-weight-bold has-text-grey-dark m-0 p-0"
            >
              {{ self.value.username }}
            </p>
          </div>
          <div
            class="is-clickable mx-2"
            :class="{ opacity: !filter.isUserActive(member.accountId) }"
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
                  :src="noProfileImage"
                  v-if="member.profileImageURL === undefined"
                />
              </figure>
            </div>
            <p
              class="is-size-7 has-text-weight-bold has-text-grey-dark m-0 p-0"
            >
              {{ member.username }}
            </p>
          </div>
        </div>
      </div>

      <FilteredMedicationRecords
        ref="filteredDedicationRecords"
        :hasMembers="members.size !== 0"
      ></FilteredMedicationRecords>
    </div>
  </div>
  <ResultMessage ref="resultMessage"></ResultMessage>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { Filter } from '@main/js/composables/model/MedicationRecords.js';
import { HttpRequestClient } from '@main/js/composables/HttpRequestClient.js';
import FilteredMedicationRecords from '@main/js/components/medicationrecord/FilteredMedicationRecords.vue';
import noProfileImage from '@main/images/no_profile_image.png';
import ResultMessage from '@main/js/components/ResultMessage.vue';

const props = defineProps({
  isParticipatingInSharedGroup: Boolean,
  csrf: String,
});

const self = reactive({ value: undefined });
const members = reactive([]);
const filter = reactive(new Filter());
const filteredDedicationRecords = ref(null);
const resultMessage = ref(null);

onMounted(async () => {
  await HttpRequestClient.submitGetRequest('/api/users?own')
    .then((data) => {
      self.value = data;
      filter.addAccountId(data.accountId);
    })
    .catch(() => {
      resultMessage.value.activate(
        'ERROR',
        'エラーが発生しました。',
        '通信状態をご確認のうえ、再度お試しください。'
      );
    });

  if (props.isParticipatingInSharedGroup) {
    await HttpRequestClient.submitGetRequest('/api/users?members')
      .then((data) => {
        members.push(...data.users);
        filter.addAllAccountIds(members.map((member) => member.accountId));
      })
      .catch(() => {
        resultMessage.value.activate(
          'ERROR',
          'エラーが発生しました。',
          '通信状態をご確認のうえ、再度お試しください。'
        );
      });
  }

  filter.start = today();
  filter.end = today();
  filteredDedicationRecords.value.loadMedicationRecords(filter);
});

function today() {
  return new Date().toLocaleDateString().slice(0, 10);
}

function todayWithoutYear() {
  const today = new Date();
  return `${today.getMonth() + 1}月${today.getDate()}日`;
}

function toggleUserActive(accountId) {
  filter.toggleUserActive(accountId);
  filteredDedicationRecords.value.loadMedicationRecords(filter);
}
</script>
