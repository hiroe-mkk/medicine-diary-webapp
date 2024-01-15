<template>
  <div class="container is-max-desktop p-3">
    <div class="content has-text-centered mb-3">
      <p class="is-size-4 has-text-weight-bold has-text-grey-dark">お薬一覧</p>
      <div
        class="tabs is-toggle is-toggle-rounded is-centered is-small mb-0"
        v-if="isParticipatingInSharedGroup"
      >
        <ul class="m-0">
          <li
            :class="{ 'is-active': medicineType === 'OWNED' }"
            @click="medicineType = 'OWNED'"
          >
            <a>
              <small class="has-text-weight-bold">　 あなた 　</small>
            </a>
          </li>
          <li
            :class="{ 'is-active': medicineType === 'SHARED_GROUP' }"
            @click="medicineType = 'SHARED_GROUP'"
          >
            <a>
              <small class="has-text-weight-bold">　　共有　　</small>
            </a>
          </li>
          <li
            :class="{ 'is-active': medicineType === 'MEMBERS' }"
            @click="medicineType = 'MEMBERS'"
          >
            <a>
              <small class="has-text-weight-bold">　メンバー　</small>
            </a>
          </li>
        </ul>
      </div>
    </div>

    <div
      class="notification has-background-white p-3"
      v-show="medicineType === 'OWNED'"
    >
      <MedicineOverviews
        :medicineOverviews="ownedMedicines.value"
        :isParticipatingInSharedGroup="props.isParticipatingInSharedGroup"
      ></MedicineOverviews>
    </div>
    <div
      class="notification has-background-white p-3"
      v-show="
        props.isParticipatingInSharedGroup && medicineType === 'SHARED_GROUP'
      "
    >
      <MedicineOverviews
        :medicineOverviews="shredGroupMedicines.value"
        :isParticipatingInSharedGroup="props.isParticipatingInSharedGroup"
      >
      </MedicineOverviews>
    </div>
    <div
      class="notification has-background-white p-3"
      v-show="props.isParticipatingInSharedGroup && medicineType === 'MEMBERS'"
    >
      <MedicineOverviews
        :medicineOverviews="membersMedicines.value"
        :isParticipatingInSharedGroup="props.isParticipatingInSharedGroup"
      >
      </MedicineOverviews>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { HttpRequestClient } from '@main/js/composables/HttpRequestClient.js';
import MedicineOverviews from '@main/js/components/medicine/MedicineOverviews.vue';

const props = defineProps({ isParticipatingInSharedGroup: Boolean });

const medicineType = ref('OWNED');
const ownedMedicines = reactive({ value: undefined });
const shredGroupMedicines = reactive({ value: undefined });
const membersMedicines = reactive({ value: undefined });

onMounted(() => {
  HttpRequestClient.submitGetRequest('/api/medicines')
    .then((data) => {
      ownedMedicines.value = data.ownedMedicines;
      shredGroupMedicines.value = data.shredGroupMedicines;
      membersMedicines.value = data.membersMedicines;
    })
    .catch(() => {
      activateResultMessage(
        'ERROR',
        'エラーが発生しました。',
        '通信状態をご確認のうえ、再度お試しください。'
      );
    });
});
</script>
