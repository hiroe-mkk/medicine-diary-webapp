<template>
  <div class="container is-max-desktop p-3">
    <div class="content has-text-centered mb-3">
      <p class="is-size-4 has-text-weight-bold has-text-grey-dark">お薬一覧</p>
      <div
        class="tabs is-toggle is-toggle-rounded is-centered is-small mb-0"
        v-if="isJoinedSharedGroup"
      >
        <ul class="m-0">
          <li
            :class="{ 'is-active': medicineType === 'OWNED' }"
            @click="medicineType = 'OWNED'"
          >
            <a>
              <small class="has-text-weight-bold px-2">あなた</small>
            </a>
          </li>
          <li
            :class="{ 'is-active': medicineType === 'SHARED_GROUP' }"
            @click="medicineType = 'SHARED_GROUP'"
          >
            <a>
              <small class="has-text-weight-bold px-3">共有</small>
            </a>
          </li>
          <li
            :class="{ 'is-active': medicineType === 'MEMBERS' }"
            @click="medicineType = 'MEMBERS'"
          >
            <a>
              <small class="has-text-weight-bold px-2">メンバー</small>
            </a>
          </li>
        </ul>
      </div>
    </div>

    <div class="content is-flex is-justify-content-flex-end mb-2">
      <div class="field has-addons">
        <div class="control">
          <input
            class="input is-rounded is-small"
            type="text"
            v-model="editingFilterEffect"
            placeholder="症状で検索する"
          />
        </div>
        <div class="control">
          <button
            type="button"
            class="button is-link is-small is-rounded"
            @click="loadMedicineOverviews(editingFilterEffect)"
          >
            <span class="icon fas fa-lg is-flex is-align-items-center m-0">
              <i class="fa-solid fa-magnifying-glass"></i>
            </span>
          </button>
        </div>
      </div>
    </div>

    <div
      class="notification has-background-white p-3"
      v-show="medicineType === 'OWNED'"
    >
      <MedicineOverviews
        :medicineOverviews="ownedMedicines.value"
        :isJoinedSharedGroup="props.isJoinedSharedGroup"
        @searched="loadMedicineOverviews"
      ></MedicineOverviews>
    </div>
    <div
      class="notification has-background-white p-3"
      v-show="
        props.isJoinedSharedGroup && medicineType === 'SHARED_GROUP'
      "
    >
      <MedicineOverviews
        :medicineOverviews="sharedGroupMedicines.value"
        :isJoinedSharedGroup="props.isJoinedSharedGroup"
        @searched="loadMedicineOverviews"
      >
      </MedicineOverviews>
    </div>
    <div
      class="notification has-background-white p-3"
      v-show="props.isJoinedSharedGroup && medicineType === 'MEMBERS'"
    >
      <MedicineOverviews
        :medicineOverviews="membersMedicines.value"
        :isJoinedSharedGroup="props.isJoinedSharedGroup"
        @searched="loadMedicineOverviews"
      >
      </MedicineOverviews>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, inject } from 'vue';
import { HttpRequestClient } from '@main/js/composables/HttpRequestClient.js';
import MedicineOverviews from '@main/js/components/medicine/MedicineOverviews.vue';

const props = defineProps({
  isJoinedSharedGroup: Boolean,
  filterEffect: { type: String, default: '' },
});
const activateResultMessage = inject('activateResultMessage');

const editingFilterEffect = ref('');

const medicineType = ref('OWNED');
const ownedMedicines = reactive({ value: undefined });
const sharedGroupMedicines = reactive({ value: undefined });
const membersMedicines = reactive({ value: undefined });

onMounted(() => {
  loadMedicineOverviews(props.filterEffect);
});

function loadMedicineOverviews(effect) {
  editingFilterEffect.value = effect;
  const params = new URLSearchParams();
  params.append('effect', effect);

  HttpRequestClient.submitGetRequest('/api/medicines?' + params.toString())
    .then((data) => {
      ownedMedicines.value = data.ownedMedicines;
      sharedGroupMedicines.value = data.sharedGroupMedicines;
      membersMedicines.value = data.membersMedicines;
    })
    .catch(() => {
      activateResultMessage(
        'ERROR',
        'エラーが発生しました。',
        '通信状態をご確認のうえ、再度お試しください。'
      );
    });
}
</script>
