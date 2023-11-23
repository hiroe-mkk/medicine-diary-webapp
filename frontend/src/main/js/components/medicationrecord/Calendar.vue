<template>
  <div class="container has-text-centered is-max-desktop pt-0 pb-2 px-2">
    <FullCalendar :options="calendarOptions" :key="calendarKey" />
  </div>

  <div class="modal" :class="{ 'is-active': isMedicationRecordsModalActive }">
    <div
      class="modal-background"
      @click="isMedicationRecordsModalActive = false"
    ></div>
    <div class="modal-content">
      <div class="notification has-text-centered has-background-white p-3">
        <p class="is-flex is-justify-content-space-between">
          <span>　</span>
          <strong class="is-size-5 has-text-grey-dark">
            {{ selectedDate }}
          </strong>
          <button
            class="delete"
            type="button"
            @click="isMedicationRecordsModalActive = false"
          ></button>
        </p>
        <FilteredMedicationRecords
          ref="filteredMedicationRecords"
          :displayRecorder="false"
          :allowLoadMore="false"
          :elements="['medicine', 'time']"
        ></FilteredMedicationRecords>
      </div>
    </div>
  </div>

  <ResultMessage ref="resultMessage"></ResultMessage>
</template>

<script setup>
import { ref } from 'vue';
import FullCalendar from '@fullcalendar/vue3';
import dayGridPlugin from '@fullcalendar/daygrid';
import interactionPlugin from '@fullcalendar/interaction';

import { Filter } from '@main/js/composables/model/MedicationRecords.js';
import { HttpRequestClient } from '@main/js/composables/HttpRequestClient.js';
import FilteredMedicationRecords from '@main/js/components/medicationrecord/FilteredMedicationRecords.vue';
import ResultMessage from '@main/js/components/ResultMessage.vue';

const props = defineProps({ accountId: String, csrf: String });

const filteredMedicationRecords = ref(null);

const selectedDate = ref('');
const isMedicationRecordsModalActive = ref(false);

const calendarKey = ref(0);
const resultMessage = ref(null);

const calendarOptions = {
  locale: 'ja',
  plugins: [dayGridPlugin, interactionPlugin],
  initialView: 'dayGridMonth',
  contentHeight: 'auto',
  buttonText: {
    today: '今月',
  },
  events: async (info, successCallback, failureCallback) => {
    const filter = new Filter();
    filter.accountId = props.accountId;
    filter.start = info.startStr.slice(0, 10);
    filter.end = info.endStr.slice(0, 10);
    filter.sizePerPage = 100; // TODO: サーバーから薬IDのみを件数上限なしで取得できるように変更する
    const params = filter.createParams();
    try {
      const result = await HttpRequestClient.submitGetRequest(
        '/api/medication-records?' + params.toString()
      );

      const dates = new Set(
        result.medicationRecords.map((medicationRecord) =>
          medicationRecord.takenAt.slice(0, 10).replace(/\//g, '-')
        )
      );
      const events = Array.from(dates).map((date) => ({ start: date }));
      successCallback(events);
    } catch (error) {
      failureCallback(error);
      resultMessage.value.activate(
        'ERROR',
        'エラーが発生しました。',
        '通信状態をご確認のうえ、再度お試しください。'
      );
    }
  },
  eventContent: function (arg) {
    return {
      html: `<i class="fa-solid fa-capsules p-1"></i>`,
    };
  },
  dateClick: (info) => {
    dateSelected(info.dateStr.replace(/-/g, '/'));
  },
  eventClick: (info) => {
    dateSelected(info.event.start.toLocaleDateString().slice(0, 10));
  },
};

function dateSelected(date) {
  const filter = new Filter();
  filter.accountId = props.accountId;
  filter.start = date;
  filter.end = date;
  filteredMedicationRecords.value.loadMedicationRecords(filter);
  const [year, month, day] = date.split('/');
  selectedDate.value = `${year}年${month}月${day}日`;
  isMedicationRecordsModalActive.value = true;
}
</script>

<style>
.fc-toolbar-title {
  /* ヘッダーの年月日を太字にする */
  font-weight: bold;
}
</style>
