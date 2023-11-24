<template>
  <div class="block" v-if="inventory.value !== undefined">
    <p
      class="is-size-5 has-text-weight-bold has-text-success-dark has-text-centered mb-3"
    >
      在庫
    </p>
    <div class="tile is-ancestor has-text-grey-dark">
      <div class="tile is-parent is-8 p-2">
        <div
          class="tile is-child notification has-background-white-bis has-text-right py-2 px-4"
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
            使用開始日 ( {{ inventory.value.startedOn.replace(/-/g, '/') }} )
          </p>
          <p
            class="has-text-weight-semibold"
            v-if="
              inventory.value.expirationOn !== undefined &&
              inventory.value.expirationOn !== ''
            "
          >
            有効期限 ( {{ inventory.value.expirationOn.replace(/-/g, '/') }} )
          </p>
        </div>
      </div>
      <div class="tile is-parent p-2">
        <div
          class="tile is-child notification has-background-white-bis is-flex is-flex-direction-column is-justify-content-space-between has-text-right py-2 px-4"
        >
          <p class="has-text-weight-bold has-text-centered mb-2">未使用</p>
          <strong class="is-size-5"
            >{{ inventory.value.unusedPackage }}個</strong
          >
        </div>
      </div>
    </div>
  </div>

  <div
    class="modal"
    :class="{ 'is-active': isInventoryAdjustmentModalActive }"
    v-if="editingInventory.value !== undefined"
  >
    <div
      class="modal-background"
      @click="isInventoryAdjustmentModalActive = false"
    ></div>
    <div class="modal-content">
      <div class="notification has-background-white py-3 px-5">
        <div class="has-text-right">
          <button
            class="delete"
            type="button"
            @click="isInventoryAdjustmentModalActive = false"
          ></button>
        </div>
        <p
          class="is-size-5 has-text-weight-bold has-text-link has-text-centered"
        >
          在庫
        </p>
        <form class="form" method="post" @submit.prevent="submitForm()">
          <div class="content my-3">
            <p class="has-text-weight-bold has-text-grey-dark">
              使用中のパッケージ
            </p>
            <div class="notification has-background-white-bis pt-3 pb-4 px-5">
              <div class="field">
                <label class="label has-text-grey">残量</label>
                <div class="control has-icons-right mb-1">
                  <input
                    class="input is-info is-rounded"
                    type="number"
                    name="remainingQuantity"
                    v-model="editingInventory.value.remainingQuantity"
                    min="0"
                    step="0.001"
                    max="10000"
                    :class="{
                      'is-danger': fieldErrors.contains('remainingQuantity'),
                    }"
                  />
                  <span
                    class="icon is-small is-right has-text-grey has-text-weight-bold"
                  >
                    {{ props.doseUnit }}
                  </span>
                  <p
                    class="help is-danger"
                    v-for="error in fieldErrors.get('remainingQuantity')"
                  >
                    {{ error }}
                  </p>
                </div>
              </div>
              <div class="field">
                <label class="label has-text-grey">内容量</label>
                <div class="control has-icons-right mb-1">
                  <input
                    class="input is-info is-rounded"
                    type="number"
                    name="quantityPerPackage"
                    v-model="editingInventory.value.quantityPerPackage"
                    min="0"
                    step="0.001"
                    max="10000"
                    :class="{
                      'is-danger': fieldErrors.contains('quantityPerPackage'),
                    }"
                  />
                  <span
                    class="icon is-small is-right has-text-grey has-text-weight-bold"
                  >
                    {{ props.doseUnit }}
                  </span>
                  <p
                    class="help is-danger"
                    v-for="error in fieldErrors.get('quantityPerPackage')"
                  >
                    {{ error }}
                  </p>
                </div>
              </div>
              <div class="field">
                <label class="label has-text-grey">使用開始日</label>
                <div class="control is-expanded">
                  <input
                    class="input is-info is-rounded"
                    type="date"
                    name="startedOn"
                    v-model="editingInventory.value.startedOn"
                    :class="{
                      'is-danger': fieldErrors.contains('startedOn'),
                    }"
                  />
                  <p
                    class="help is-danger"
                    v-for="error in fieldErrors.get('startedOn')"
                  >
                    {{ error }}
                  </p>
                </div>
              </div>
              <div class="field">
                <label class="label has-text-grey">有効期限</label>
                <div class="control is-expanded">
                  <input
                    class="input is-info is-rounded"
                    type="date"
                    name="expirationOn"
                    v-model="editingInventory.value.expirationOn"
                    :class="{
                      'is-danger': fieldErrors.contains('expirationOn'),
                    }"
                  />
                  <p
                    class="help is-danger"
                    v-for="error in fieldErrors.get('expirationOn')"
                  >
                    {{ error }}
                  </p>
                </div>
              </div>
            </div>

            <p class="has-text-weight-bold has-text-grey-dark pb-2">
              未使用のパッケージ
            </p>
            <div class="notification has-background-white-bis pt-3 pb-4 px-5">
              <div class="field">
                <div class="control has-icons-right">
                  <input
                    class="input is-info is-rounded"
                    type="number"
                    name="unusedPackage"
                    v-model="editingInventory.value.unusedPackage"
                    min="0"
                    step="1"
                    :class="{
                      'is-danger': fieldErrors.contains('unusedPackage'),
                    }"
                  />
                  <span
                    class="icon is-small is-right has-text-grey has-text-weight-bold"
                  >
                    個
                  </span>
                  <p
                    class="help is-danger"
                    v-for="error in fieldErrors.get('unusedPackage')"
                  >
                    {{ error }}
                  </p>
                </div>
              </div>
            </div>
            <div>
              <p class="help">
                ※服用記録を追加すると、服用した量に応じて使用中のパッケージの残量が更新されます。<br />
                　服用記録を修正および削除をするときは、入力フォームからご自身で修正を行ってください。
              </p>
            </div>
          </div>
          <div class="field is-grouped is-grouped-centered p-2">
            <p class="control">
              <button class="button is-small is-rounded is-link">完了</button>
            </p>
            <p class="control">
              <button
                type="button"
                class="button is-small is-rounded is-outlined is-danger"
                @click="isInventoryAdjustmentModalActive = false"
              >
                キャンセル
              </button>
            </p>
          </div>
        </form>
      </div>
    </div>
  </div>

  <ResultMessage ref="resultMessage"></ResultMessage>
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

const fieldErrors = reactive(new FieldErrors());
const resultMessage = ref(null);

onMounted(async () => {
  if (props.remainingQuantity !== undefined) {
    inventory.value = {
      remainingQuantity: props.remainingQuantity,
      quantityPerPackage: props.quantityPerPackage,
      startedOn: props.startedOn,
      expirationOn: props.expirationOn,
      unusedPackage: props.unusedPackage,
    };
  }
});

function submitForm() {
  const form = new URLSearchParams();
  form.set('remainingQuantity', editingInventory.value.remainingQuantity);
  form.set('quantityPerPackage', editingInventory.value.quantityPerPackage);
  if (editingInventory.value.startedOn !== undefined)
    form.set('startedOn', editingInventory.value.startedOn);
  if (editingInventory.value.expirationOn !== undefined)
    form.set('expirationOn', editingInventory.value.expirationOn);
  form.set('unusedPackage', editingInventory.value.unusedPackage);
  form.set('_csrf', props.csrf);

  HttpRequestClient.submitPostRequest(
    `/api/medicines/${props.medicineId}/inventory/adjust`,
    form
  )
    .then(() => {
      inventory.value = {
        remainingQuantity: editingInventory.value.remainingQuantity,
        quantityPerPackage: editingInventory.value.quantityPerPackage,
        startedOn: editingInventory.value.startedOn,
        expirationOn: editingInventory.value.expirationOn,
        unusedPackage: editingInventory.value.unusedPackage,
      };
      isInventoryAdjustmentModalActive.value = false;
      resultMessage.value.activate('INFO', '在庫の修正が完了しました。');
      return;
    })
    .catch((error) => {
      if (error instanceof HttpRequestFailedError) {
        if (error.status == 400) {
          // バインドエラーが発生した場合
          if (!error.isBodyEmpty() && error.body.fieldErrors !== undefined) {
            fieldErrors.set(error.body.fieldErrors);
            return;
          }
        } else if (error.status == 401) {
          // 認証エラーが発生した場合
          location.reload();
          return;
        } else if (error.status == 409) {
          resultMessage.value.activate(
            'ERROR',
            'エラーが発生しました。',
            error.getMessage()
          );
          return;
        } else if (error.status == 500) {
          resultMessage.value.activate(
            'ERROR',
            'システムエラーが発生しました。',
            'お手数ですが、再度お試しください。'
          );
          return;
        } else if (error.hasMessage()) {
          resultMessage.value.activate(
            'ERROR',
            'エラーが発生しました。',
            error.getMessage()
          );
          return;
        }
      }

      resultMessage.value.activate(
        'ERROR',
        'エラーが発生しました。',
        '通信状態をご確認のうえ、再度お試しください。'
      );
    });
}

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
  fieldErrors.clear();
  isInventoryAdjustmentModalActive.value = true;
}
</script>
