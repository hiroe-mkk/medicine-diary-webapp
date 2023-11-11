<template>
  <div class="field is-horizontal my-3">
    <div class="field-label is-flex is-align-items-center">
      <label class="label has-text-grey">お薬</label>
    </div>
    <div class="field-body">
      <div class="field">
        <div class="control">
          <div class="field">
            <div class="field-body">
              <div class="field">
                <div class="control mb-1">
                  <div
                    class="select is-rounded is-fullwidth"
                    :class="{
                      'is-info': medicineIdErrors.length === 0,
                      'is-danger': medicineIdErrors.length !== 0,
                    }"
                  >
                    <select v-model="takenMedicine.medicine">
                      <option
                        v-for="medicine in medicines"
                        :value="medicine"
                        :text="medicine.medicineName"
                      ></option>
                    </select>
                    <input
                      type="text"
                      name="takenMedicine"
                      :value="
                        takenMedicine.medicine !== undefined
                          ? takenMedicine.medicine.medicineId
                          : ''
                      "
                      hidden
                    />
                    <span
                      class="help is-danger"
                      v-for="error in medicineIdErrors"
                    >
                      {{ error }}
                    </span>
                  </div>
                </div>
              </div>

              <div class="field">
                <div class="control has-icons-right mb-1">
                  <input
                    class="input is-info is-rounded"
                    type="number"
                    name="quantity"
                    :value="takenMedicine.quantity"
                    min="0"
                    max="10000"
                    :class="{
                      'is-info': quantityErrors.length === 0,
                      'is-danger': quantityErrors.length !== 0,
                    }"
                  />
                  <span
                    class="icon is-small is-right has-text-grey has-text-weight-bold"
                    v-if="takenMedicine.medicine !== undefined"
                  >
                    {{
                      takenMedicine.medicine.dosageAndAdministration.takingUnit
                    }}
                  </span>
                  <span class="help is-danger" v-for="error in quantityErrors">
                    {{ error }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <ResultMessage ref="resultMessage"></ResultMessage>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ArrayConverter } from '@main/js/composables/ArrayConverter.js';
import { HttpRequestClient } from '@main/js/composables/HttpRequestClient.js';
import ResultMessage from '@main/js/components/ResultMessage.vue';

const props = defineProps({
  medicineId: String,
  quantity: Number,
  medicineIdErrors: { type: String, default: '[]' },
  quantityErrors: { type: String, default: '[]' },
});

const takenMedicine = reactive({ medicine: undefined, quantity: 0.0 });
const medicines = reactive([]);
const medicineIdErrors = reactive([]);
const quantityErrors = reactive([]);

const resultMessage = ref(null);

onMounted(async () => {
  medicineIdErrors.push(...ArrayConverter.fromString(props.medicineIdErrors));
  quantityErrors.push(...ArrayConverter.fromString(props.quantityErrors));

  await loadMedicines();
  if (props.medicineId !== undefined) {
    takenMedicine.medicine = medicines.find(
      (medicine) => medicine.medicineId === props.medicineId
    );

    if (props.quantity == 0) {
      takenMedicine.quantity =
        takenMedicine.medicine.dosageAndAdministration.quantity;
    } else {
      takenMedicine.quantity = props.quantity;
    }
  }
});

function loadMedicines() {
  return HttpRequestClient.submitGetRequest('/api/medicines?user')
    .then((data) => {
      medicines.push(...data.medicines);
    })
    .catch((error) => {
      resultMessage.value.activate(
        'ERROR',
        'エラーが発生しました。',
        '通信状態をご確認のうえ、再度お試しください。'
      );
    });
}
</script>
