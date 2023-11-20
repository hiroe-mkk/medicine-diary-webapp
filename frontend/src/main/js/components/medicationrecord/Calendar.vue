<template>
  <div class="container has-text-centered is-max-desktop p-3">
    <div class="content is-inline-block pt-2 m-0" v-if="members.length !== 0">
      <div class="is-flex is-align-items-center">
        <div
          class="is-clickable mx-2"
          :class="{ opacity: !filter.isUserActive(self.value.accountId) }"
          @click="activeUserOnly(self.value.accountId)"
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
          <p class="is-size-7 has-text-weight-bold has-text-grey-dark m-0 p-0">
            {{ self.value.username }}
          </p>
        </div>
        <div
          class="is-clickable mx-2"
          :class="{ opacity: !filter.isUserActive(member.accountId) }"
          @click="activeUserOnly(member.accountId)"
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
          <p class="is-size-7 has-text-weight-bold has-text-grey-dark m-0 p-0">
            {{ member.username }}
          </p>
        </div>
      </div>
    </div>
    <FullCalendar :options="calendarOptions" :key="calendarKey" />
  </div>

  <div class="modal" :class="{ 'is-active': isMedicationRecordsModalActive }">
    <div
      class="modal-background"
      @click="isMedicationRecordsModalActive = false"
    ></div>
    <div class="modal-content">
      <div class="notification has-text-centered has-background-white p-3">
        <div class="has-text-right">
          <button
            class="delete"
            type="button"
            @click="isMedicationRecordsModalActive = false"
          ></button>
        </div>
        <div class="content mb-3">
          <p class="is-size-4 m-3">
            <strong class="has-text-grey-dark">
              {{ selectedDate }}
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import FullCalendar from '@fullcalendar/vue3';
import dayGridPlugin from '@fullcalendar/daygrid';
import interactionPlugin from '@fullcalendar/interaction';

import { Filter } from '@main/js/composables/model/MedicationRecords.js';
import { HttpRequestClient } from '@main/js/composables/HttpRequestClient.js';
import FilteredMedicationRecords from '@main/js/components/medicationrecord/FilteredMedicationRecords.vue';
import noProfileImage from '@main/images/no_profile_image.png';

const props = defineProps({
  isParticipatingInSharedGroup: Boolean,
  csrf: String,
});

const self = reactive({ value: undefined });
const members = reactive([]);

const filter = reactive(new Filter());
const filteredDedicationRecords = ref(null);

const calendarKey = ref(0);
const resultMessage = ref(null);

const selectedDate = ref('');
const isMedicationRecordsModalActive = ref(false);

const calendarOptions = {
  locale: 'ja',
  plugins: [dayGridPlugin, interactionPlugin],
  initialView: 'dayGridMonth',
  buttonText: {
    today: '今月',
  },
  events: async (info, successCallback, failureCallback) => {
    await loadUsers();

    filter.end = info.startStr.slice(0, 10);
    filter.end = info.endStr.slice(0, 10);
    const params = filter.createParams();
    const result = await HttpRequestClient.submitGetRequest(
      '/api/medication-records?' + params.toString()
    );
    const jsonData = result.medicationRecords.map((medicationRecord) => ({
      title: medicationRecord.takenMedicine.medicineName,
      start: medicationRecord.takenAt.slice(0, 10).replace(/\//g, '-'),
    }));
    successCallback(jsonData);
  },
  dateClick: (info) => {
    dateSelected(info.dateStr);
  },
  eventClick: (info) => {
    dateSelected(info.event.start.toLocaleDateString().slice(0, 10));
  },
};

function dateSelected(date) {
  const selectedDateFilter = filter.copy();
  selectedDateFilter.start = date;
  selectedDateFilter.end = date;
  filteredDedicationRecords.value.loadMedicationRecords(selectedDateFilter);
  selectedDate.value = date.replace(/-/g, '/');
  isMedicationRecordsModalActive.value = true;
}

async function loadUsers() {
  if (self.value !== undefined) return;

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
}

function activeUserOnly(accountId) {
  filter.activeUserOnly(accountId);
  calendarKey.value += 1; // keyを変更してFullCalendarを再レンダリングする
}
</script>