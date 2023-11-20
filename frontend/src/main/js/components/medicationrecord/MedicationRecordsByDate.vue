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

      <FilteredMedicationRecords
        ref="filteredDedicationRecords"
        :hasMembers="members.length !== 0"
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
  const date = new Date().toLocaleDateString().slice(0, 10);
  filter.start = date;
  filter.end = date;
  filteredDedicationRecords.value.loadMedicationRecords(filter);
});

function todayWithoutYear() {
  const today = new Date();
  return `${today.getMonth() + 1}月${today.getDate()}日`;
}
</script>
