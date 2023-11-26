<template>
  <div class="container has-text-centered is-max-desktop pt-0 pb-6 px-2">
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
          <strong class="is-size-4 has-text-grey-dark">
            {{ toDateJpnStr(selectedDateStr) }}
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
          :canAppend="canAppend"
          :elements="['medicine', 'time']"
        >
          <a
            class="button is-small is-rounded is-link px-5"
            :href="`/medication-records/add?date=${selectedDateStr}`"
            >追加する
          </a>
        </FilteredMedicationRecords>
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

const props = defineProps({
  accountId: String,
  canAppend: Boolean,
  csrf: String,
});

const filteredMedicationRecords = ref(null);

const selectedDateStr = ref('');
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
  eventColor: 'transparent',
  eventContent: function (arg) {
    return {
      html: `<span class="icon is-medium fas fa-2x has-text-link-dark"><i class="fa-solid fa-capsules p-1"></i></span>`,
    };
  },
  dateClick: (info) => {
    dateSelected(info.dateStr);
  },
  eventClick: (info) => {
    dateSelected(
      info.event.start.toLocaleDateString().slice(0, 10).replace(/\//g, '-')
    );
  },
};

function dateSelected(dateStr) {
  selectedDateStr.value = dateStr;

  const filter = new Filter();
  filter.accountId = props.accountId;
  const slashDateStr = dateStr.replace(/-/g, '/');
  filter.start = slashDateStr;
  filter.end = slashDateStr;

  filteredMedicationRecords.value.loadMedicationRecords(filter);
  isMedicationRecordsModalActive.value = true;
}

function toDateJpnStr(date) {
  if (date === undefined || date == '') return;

  const [year, month, day] = date.split('-');
  return `${year}年${month}月${day}日`;
}
</script>

<style>
.fc-toolbar-title {
  font-weight: bold; /* ヘッダーの年月を太字にする */
}

.fc {
  z-index: 1; /* 他の要素よりも背面に配置 */
}
</style>