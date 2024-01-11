<template>
  <div class="block" v-if="inventory.value !== undefined">
    <p
      class="is-size-5 has-text-weight-bold has-text-success-dark has-text-centered mb-3"
    >
      在庫
    </p>
    <div class="tile is-ancestor has-text-grey-dark">
      <div
        class="tile is-parent is-clickable is-8 p-2"
        @click="activateUsingPackageModal()"
      >
        <div
          class="tile is-child notification has-background-success-light has-text-right py-2 px-4"
        >
          <p class="has-text-weight-semibold has-text-centered mb-2">使用中</p>
          <p class="m-0">
            <strong class="is-size-4">
              {{ inventory.value.remainingQuantity }} /
              {{ inventory.value.quantityPerPackage }}
            </strong>
            <span class="has-text-weight-semibold pl-1">
              {{ props.doseUnit }}
            </span>
          </p>
          <p
            class="m-0"
            v-if="
              inventory.value.startedOn !== undefined &&
              inventory.value.startedOn !== ''
            "
          >
            <span class="has-text-weight-semibold pr-1">使用開始日</span>
            <strong class="">
              ( {{ inventory.value.startedOn.replace(/-/g, '/') }} )
            </strong>
          </p>
          <p
            class="m-0"
            v-if="
              inventory.value.expirationOn !== undefined &&
              inventory.value.expirationOn !== ''
            "
          >
            <span class="has-text-weight-semibold pr-1">有効期限</span>
            <strong>
              ( {{ inventory.value.expirationOn.replace(/-/g, '/') }} )
            </strong>
          </p>
        </div>
      </div>
      <div
        class="tile is-parent is-clickable p-2"
        @click="activateUnusedPackageModal()"
      >
        <div
          class="tile is-child notification has-background-success-light is-flex is-flex-direction-column is-justify-content-space-between has-text-right py-2 px-4"
        >
          <p class="has-text-weight-semibold has-text-centered mb-2">未使用</p>
          <p>
            <strong class="is-size-4">
              {{ inventory.value.unusedPackage }}
            </strong>
            <span class="has-text-weight-semibold pl-1">個</span>
          </p>
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
          在庫管理
        </p>
        <form class="form" method="post" @submit.prevent="adjustInventory()">
          <div class="content py-3">
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

            <p class="has-text-weight-bold has-text-grey-dark">
              未使用のパッケージ
            </p>
            <div class="notification has-background-white-bis py-4 px-5">
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

  <div
    class="modal"
    :class="{ 'is-active': isUsingPackageModalActive }"
    v-if="editingInventory.value !== undefined"
  >
    <div
      class="modal-background"
      @click="isUsingPackageModalActive = false"
    ></div>
    <div class="modal-content">
      <div class="notification has-background-white py-3 px-5">
        <div class="has-text-right">
          <button
            class="delete"
            type="button"
            @click="isUsingPackageModalActive = false"
          ></button>
        </div>
        <p
          class="is-size-5 has-text-weight-bold has-text-link has-text-centered"
        >
          使用中パッケージ
        </p>
        <form class="form" method="post" @submit.prevent="adjustInventory()">
          <div class="content py-3 px-2">
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
            <p class="help">
              ※服用記録を追加すると、服用した量に応じて使用中のパッケージの残量が更新されます。<br />
              　服用記録を修正および削除をするときは、入力フォームからご自身で修正を行ってください。
            </p>
          </div>

          <input
            name="unusedPackage"
            v-model="editingInventory.value.unusedPackage"
            hidden
          />
          <div class="field is-grouped is-grouped-centered p-2">
            <p class="control">
              <button class="button is-small is-rounded is-link">完了</button>
            </p>
            <p class="control">
              <button
                type="button"
                class="button is-small is-rounded is-outlined is-danger"
                @click="isUsingPackageModalActive = false"
              >
                キャンセル
              </button>
            </p>
          </div>
        </form>
      </div>
    </div>
  </div>

  <div
    class="modal"
    :class="{ 'is-active': isUnusedPackageModalActive }"
    v-if="editingInventory.value !== undefined"
  >
    <div
      class="modal-background"
      @click="isUnusedPackageModalActive = false"
    ></div>
    <div class="modal-content">
      <div class="notification has-background-white py-3 px-5">
        <div class="has-text-right">
          <button
            class="delete"
            type="button"
            @click="isUnusedPackageModalActive = false"
          ></button>
        </div>
        <p
          class="is-size-5 has-text-weight-bold has-text-link has-text-centered"
        >
          未使用パッケージ
        </p>
        <form class="form" method="post" @submit.prevent="adjustInventory()">
          <div class="content py-3 px-2">
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

          <input
            name="remainingQuantity"
            v-model="editingInventory.value.remainingQuantity"
            hidden
          />
          <input
            name="quantityPerPackage"
            v-model="editingInventory.value.quantityPerPackage"
            hidden
          />
          <input
            name="startedOn"
            v-model="editingInventory.value.startedOn"
            hidden
          />
          <input
            name="expirationOn"
            v-model="editingInventory.value.expirationOn"
            hidden
          />
          <div class="field is-grouped is-grouped-centered p-2">
            <p class="control">
              <button class="button is-small is-rounded is-link">完了</button>
            </p>
            <p class="control">
              <button
                type="button"
                class="button is-small is-rounded is-outlined is-danger"
                @click="isUnusedPackageModalActive = false"
              >
                キャンセル
              </button>
            </p>
          </div>
        </form>
      </div>
    </div>
  </div>

  <div
    class="modal"
    :class="{ 'is-active': isStoppageConfirmationModalActive }"
  >
    <div
      class="modal-background"
      @click="isStoppageConfirmationModalActive = false"
    ></div>
    <div class="modal-content is-flex is-justify-content-center">
      <div class="message is-inline-block is-info">
        <div class="message-body">
          <div class="content">
            <p class="has-text-centered mb-2">
              <strong class="is-size-5 mb-1">在庫管理を終了しますか？</strong>
              <br />
            </p>
            <div class="field is-grouped is-grouped-centered p-2">
              <p class="control">
                <button
                  type="button"
                  class="button is-small is-rounded is-link"
                  @click="stopInventoryManagement()"
                >
                  終了する
                </button>
              </p>
              <p class="control">
                <button
                  type="button"
                  class="button is-small is-rounded is-outlined is-danger"
                  @click="isStoppageConfirmationModalActive = false"
                >
                  キャンセル
                </button>
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {
  ref,
  reactive,
  onMounted,
  defineEmits,
  defineExpose,
  inject,
} from 'vue';
import {
  HttpRequestClient,
  HttpRequestFailedError,
} from '@main/js/composables/HttpRequestClient.js';
import { FieldErrors } from '@main/js/composables/model/FieldErrors.js';

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
const emits = defineEmits(['updated:is-enabled']);
defineExpose({
  activateAdjustmentModal,
  activateStoppageConfirmationModal,
});
const activateResultMessage = inject('activateResultMessage');

const inventory = reactive({ value: undefined });
const editingInventory = reactive({ value: undefined });
const fieldErrors = reactive(new FieldErrors());

const isInventoryAdjustmentModalActive = ref(false);
const isUsingPackageModalActive = ref(false);
const isUnusedPackageModalActive = ref(false);
const isStoppageConfirmationModalActive = ref(false);

onMounted(() => {
  if (props.remainingQuantity !== undefined) {
    inventory.value = {
      remainingQuantity: props.remainingQuantity,
      quantityPerPackage: props.quantityPerPackage,
      startedOn: props.startedOn,
      expirationOn: props.expirationOn,
      unusedPackage: props.unusedPackage,
    };
  }
  emits('updated:is-enabled', inventory.value !== undefined);
});

function adjustInventory() {
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
      let message;
      if (inventory.value !== undefined) {
        message = '在庫の修正が完了しました。';
      } else {
        message = '在庫管理を開始しました。';
      }

      inventory.value = {
        remainingQuantity: editingInventory.value.remainingQuantity,
        quantityPerPackage: editingInventory.value.quantityPerPackage,
        startedOn: editingInventory.value.startedOn,
        expirationOn: editingInventory.value.expirationOn,
        unusedPackage: editingInventory.value.unusedPackage,
      };
      isInventoryAdjustmentModalActive.value = false;
      isUsingPackageModalActive.value = false;
      isUnusedPackageModalActive.value = false;

      activateResultMessage('INFO', message);
      emits('updated:is-enabled', true);
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
          activateResultMessage(
            'ERROR',
            'エラーが発生しました。',
            error.getMessage()
          );
          return;
        } else if (error.status == 500) {
          activateResultMessage(
            'ERROR',
            'システムエラーが発生しました。',
            'お手数ですが、再度お試しください。'
          );
          return;
        } else if (error.hasMessage()) {
          activateResultMessage(
            'ERROR',
            'エラーが発生しました。',
            error.getMessage()
          );
          return;
        }
      }

      activateResultMessage(
        'ERROR',
        'エラーが発生しました。',
        '通信状態をご確認のうえ、再度お試しください。'
      );
    });
}

function stopInventoryManagement() {
  const form = new FormData();
  form.set('_csrf', props.csrf);

  HttpRequestClient.submitPostRequest(
    `/api/medicines/${props.medicineId}/inventory/stop`,
    form
  )
    .then(() => {
      inventory.value = undefined;
      activateResultMessage('INFO', `在庫管理を終了しました。`);
      isStoppageConfirmationModalActive.value = false;
      emits('updated:is-enabled', false);
    })
    .catch((error) => {
      if (error instanceof HttpRequestFailedError) {
        if (error.status == 401) {
          // 認証エラーが発生した場合
          location.reload();
          return;
        } else if (error.status == 500) {
          activateResultMessage(
            'ERROR',
            'システムエラーが発生しました。',
            'お手数ですが、再度お試しください。'
          );
          return;
        } else if (error.hasMessage()) {
          activateResultMessage(
            'ERROR',
            'エラーが発生しました。',
            error.getMessage()
          );
          return;
        }
      }

      activateResultMessage(
        'ERROR',
        'エラーが発生しました。',
        '通信状態をご確認のうえ、再度お試しください。'
      );
    });
}

function activateAdjustmentModal() {
  resetEditingInventory();
  isInventoryAdjustmentModalActive.value = true;
}

function activateUsingPackageModal() {
  resetEditingInventory();
  isUsingPackageModalActive.value = true;
}

function activateUnusedPackageModal() {
  resetEditingInventory();
  isUnusedPackageModalActive.value = true;
}

function resetEditingInventory() {
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
}

function activateStoppageConfirmationModal() {
  isStoppageConfirmationModalActive.value = true;
}
</script>
