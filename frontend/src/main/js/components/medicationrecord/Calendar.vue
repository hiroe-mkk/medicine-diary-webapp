<template>
  <div class="container has-text-centered is-max-desktop pt-0 pb-6 px-2">
    <FullCalendar :options="calendarOptions" ref="calendar" />
  </div>

  <div class="modal" :class="{ 'is-active': isMedicationRecordsModalActive }">
    <div
      class="modal-background"
      @click="isMedicationRecordsModalActive = false"
    ></div>
    <div class="modal-content">
      <div
        class="notification has-text-centered has-background-white py-3 px-5"
      >
        <p class="is-flex is-justify-content-space-between py-1">
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
          :isAllowLoadMore="false"
          :isShowAppendButton="props.isSelfCalendar"
          :elements="['medicine', 'time']"
          :csrf="props.csrf"
          @deleted="medicationRecordDeleted"
        >
          <a
            class="button is-small is-link is-rounded is-outlined px-4"
            :href="`/medication-records/add?date=${selectedDateStr}`"
          >
            追加する
            <span class="icon fas fa-lg is-flex is-align-items-center m-0">
              <i class="fa-solid fa-file-pen"></i>
            </span>
          </a>
        </FilteredMedicationRecords>
      </div>
    </div>
  </div>
</template>

<script setup>
import dayGridPlugin from '@fullcalendar/daygrid';
import interactionPlugin from '@fullcalendar/interaction';
import FullCalendar from '@fullcalendar/vue3';
import { inject, onMounted, ref } from 'vue';

import FilteredMedicationRecords from '@main/js/components/medicationrecord/FilteredMedicationRecords.vue';
import { HttpRequestClient } from '@main/js/composables/HttpRequestClient.js';
import { Filter } from '@main/js/composables/model/MedicationRecords.js';

const props = defineProps({
  accountId: String,
  isSelfCalendar: Boolean,
  csrf: String,
});
const activateResultMessage = inject('activateResultMessage');

const filteredMedicationRecords = ref(null);

const selectedDateStr = ref('');
const isMedicationRecordsModalActive = ref(false);

const calendar = ref(null);
let calendarApi = null;

onMounted(() => {
  calendarApi = calendar.value.getApi();
});

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
        '/api/medication-records?' + params.toString(),
        activateResultMessage
      );

      const dates = result.medicationRecords
        .map((medicationRecord) =>
          medicationRecord.takenMedicineOn.replace(/\//g, '-')
        )
        .reduce((map, date) => {
          const count = map.get(date) || 0;
          map.set(date, count + 1);
          return map;
        }, new Map());
      const events = Array.from(dates.entries()).map(([date, count]) => ({
        start: date,
        count: count,
      }));
      successCallback(events);
    } catch (error) {
      failureCallback(error);
      activateResultMessage(
        'ERROR',
        'エラーが発生しました。',
        '通信状態をご確認のうえ、再度お試しください。'
      );
    }
  },
  eventColor: 'transparent',
  eventContent: function (arg) {
    const count = arg.event._def.extendedProps.count;
    const icons =
      `<span class="icon is-medium fas fas fa-lg has-text-link-dark pl-1">
         <i class="fa-solid fa-capsules"></i>
       </span>`.repeat(count);
    const iconDesktop = `<p class="is-hidden-touch mx-2">${icons}</p>`;
    const iconTouch = `
        <p class="icon-text has-text-link-dark is-hidden-desktop m-1">
          <span class="icon fas fa-lg"><i class="fa-solid fa-capsules"></i></span>
          <strong>× ${count}</strong>
        </p>`;
    return { html: iconDesktop + iconTouch };
  },
  dateClick: (info) => {
    dateSelected(info.dateStr);
  },
  eventClick: (info) => {
    const jstOffset = 9 * 60; // JSTのUTCオフセット（日本はUTC+9）
    const jstTime = info.event.start.getTime() + jstOffset * 60 * 1000;
    const jstDate = new Date(jstTime);
    dateSelected(jstDate.toISOString().slice(0, 10));
  },
};

function dateSelected(dateStr) {
  selectedDateStr.value = dateStr;

  const filter = new Filter();
  filter.accountId = props.accountId;
  filter.start = dateStr;
  filter.end = dateStr;

  filteredMedicationRecords.value.loadMedicationRecords(filter);
  isMedicationRecordsModalActive.value = true;
}

function toDateJpnStr(date) {
  if (date === undefined || date == '') return;

  const [year, month, day] = date.split('-');
  return `${year}年${month}月${day}日`;
}

function medicationRecordDeleted(medicationRecordId) {
  calendarApi.refetchEvents();
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
