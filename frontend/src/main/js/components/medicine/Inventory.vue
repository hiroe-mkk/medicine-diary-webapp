<template>
  <div class="block" v-if="inventory.value !== undefined">
    <p
      class="has-text-weight-bold has-text-success-dark has-text-centered mb-3"
    >
      在庫
    </p>
    <div class="columns is-mobile has-text-grey-dark">
      <div class="column is-fullheight is-three-fifths p-2">
        <div
          class="notification has-background-link-bis has-text-right py-2 px-4"
        >
          <p class="has-text-weight-bold has-text-centered mb-2">使用中</p>
          <p>
            <strong class="is-size-5">
              {{ inventory.value.remainingQuantity }} /
              {{ inventory.value.quantityPerPackage }}
            </strong>
            <strong>{{ props.doseUnit }}</strong>
          </p>
          <p
            class="has-text-weight-semibold"
            v-if="inventory.value.startedOn !== undefined"
          >
            使用開始日 ( {{ inventory.value.startedOn }} )
          </p>
          <p
            class="has-text-weight-semibold"
            v-if="inventory.value.expirationOn !== undefined"
          >
            有効期限 ( {{ inventory.value.expirationOn }} )
          </p>
        </div>
      </div>
      <div class="column is-fullheight p-2">
        <div
          class="notification has-background-link-bis has-text-right py-2 px-4"
        >
          <p class="has-text-weight-bold has-text-centered mb-2">未使用</p>
          <strong class="is-size-5">{{ inventory.value.unusedPackage }}</strong>
          <strong>個</strong>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, defineExpose } from 'vue';
import {
  HttpRequestClient,
  HttpRequestFailedError,
} from '@main/js/composables/HttpRequestClient.js';
import { FieldErrors } from '@main/js/composables/model/FieldErrors.js';
import ResultMessage from '@main/js/components/ResultMessage.vue';

const props = defineProps({
  medicineId: String,
  doseUnit: String,
  remainingQuantity: Number,
  quantityPerPackage: Number,
  startedOn: String,
  expirationOn: String,
  unusedPackage: Number,
  csrf: String,
});
defineExpose({ activateInventoryAdjustmentModal });

const inventory = reactive({ value: undefined });
const editingInventory = reactive({ value: undefined });
const isInventoryAdjustmentModalActive = ref(false);

onMounted(async () => {
    inventory.value = {
      remainingQuantity: props.remainingQuantity,
      quantityPerPackage: props.quantityPerPackage,
      startedOn: props.startedOn,
      expirationOn: props.expirationOn,
      unusedPackage: props.unusedPackage,
    };
});

function activateInventoryAdjustmentModal() {
  if (inventory.value !== undefined) {
    editingInventory.value = {
      remainingQuantity: inventory.value.remainingQuantity,
      quantityPerPackage: inventory.value.quantityPerPackage,
      startedOn: inventory.value.startedOn,
      expirationOn: inventory.value.expirationOn,
      unusedPackage: inventory.value.unusedPackage,
    };
  } else {
    editingInventory.value = {
      remainingQuantity: 0.0,
      quantityPerPackage: 0.0,
      startedOn: '',
      expirationOn: '',
      unusedPackage: 0,
    };
  }
  isInventoryAdjustmentModalActive.value = true;
}
</script>
