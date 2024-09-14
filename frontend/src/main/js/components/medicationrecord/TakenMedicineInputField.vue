<template>
  <div class="field my-3">
    <div class="field-body">
      <div class="field">
        <label class="label has-text-grey">
          種類
          <span class="tag is-rounded is-danger is-light ml-2">必須</span>
        </label>
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
          </div>
          <p class="help is-danger" v-for="error in medicineIdErrors">
            {{ error }}
          </p>
        </div>
      </div>

      <div class="field">
        <label class="label has-text-grey">
          量
          <span class="tag is-rounded is-danger is-light ml-2">必須</span>
        </label>
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
          <p class="help is-danger" v-for="error in quantityErrors">
            {{ error }}
          </p>
          <span
            class="icon is-small is-right has-text-grey has-text-weight-bold"
            v-if="takenMedicine.medicine !== undefined"
          >
            {{ takenMedicine.medicine.dosageAndAdministration.doseUnit }}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ArrayConverter } from '@main/js/composables/ArrayConverter.js';
import { HttpRequestClient } from '@main/js/composables/HttpRequestClient.js';
import { inject, onMounted, reactive } from 'vue';

const props = defineProps({
  medicineId: String,
  quantity: Number,
  medicineIdErrors: { type: String, default: '[]' },
  quantityErrors: { type: String, default: '[]' },
});
const activateResultMessage = inject('activateResultMessage');

const takenMedicine = reactive({
  medicine: undefined,
  quantity: 0.0,
});
const medicines = reactive([]);
const medicineIdErrors = reactive([]);
const quantityErrors = reactive([]);

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
  return HttpRequestClient.submitGetRequest(
    '/api/medicines?available',
    activateResultMessage
  ).then((data) => {
    medicines.push(...data.medicines);
  });
}
</script>
