<template>
  <div v-if="props.medicineOverviews.length !== 0">
    <div
      class="media is-flex is-align-items-center p-3 m-0"
      v-for="medicineOverview in props.medicineOverviews"
    >
      <div class="media-left">
        <p
          class="image is-96x96 is-clickable mx-3"
          @click="activateMedicineImageModal(medicineOverview.medicineImageURL)"
          v-if="medicineOverview.medicineImageURL !== undefined"
        >
          <img :src="medicineOverview.medicineImageURL" alt="" />
        </p>
        <p
          class="image is-96x96 mx-3"
          v-if="medicineOverview.medicineImageURL === undefined"
        >
          <img :src="noMedicineImage" alt="" />
        </p>
      </div>
      <div class="media-content has-text-grey-dark">
        <p>
          <a
            class="has-text-info has-text-weight-bold is-underlined"
            :href="`/medicines/${medicineOverview.medicineId}`"
            >{{ medicineOverview.medicineName }}
          </a>
          <span
            class="icon mx-1"
            v-if="
              props.isJoinedSharedGroup && !medicineOverview.isPublic
            "
          >
            <i class="fa-solid fa-lock"></i>
          </span>
        </p>
        <p class="help is-danger" v-if="isInventoryLow(medicineOverview)">
          ※ 在庫が残りわずかです。
        </p>
        <p class="help is-danger" v-if="isExpirationNear(medicineOverview)">
          ※ 有効期限が近づいています。
        </p>
        <p class="help is-danger" v-if="isExpiration(medicineOverview)">
          ※ 有効期限が切れています。
        </p>
        <p class="is-flex is-justify-content-flex-end mt-3 mb-0">
          <span
            class="button is-rounded is-small is-link is-light has-text-weight-bold py-1 px-2 mx-1"
            v-for="effect in medicineOverview.effects"
            @click="search(effect)"
          >
            {{ `# ${effect}` }}
          </span>
        </p>
      </div>
    </div>

    <div class="modal" :class="{ 'is-active': isMedicineImageModalActive }">
      <div
        class="modal-background"
        @click="isMedicineImageModalActive = false"
      ></div>
      <div class="modal-content is-flex is-justify-content-center m-0">
        <img :src="selectedMedicineImageURL" alt="" style="max-height: 300px" />
        <div class="ml-2">
          <button
            class="delete"
            type="button"
            @click="isMedicineImageModalActive = false"
          ></button>
        </div>
      </div>
    </div>
  </div>

  <div
    class="content has-text-centered p-5"
    v-if="props.medicineOverviews.length === 0"
  >
    <p class="has-text-weight-bold has-text-grey">お薬が登録されていません。</p>
  </div>
</template>

<script setup>
import { ref, defineEmits } from 'vue';
import noMedicineImage from '@main/images/no_medicine_image.png';

const props = defineProps({
  medicineOverviews: { type: Array, default: () => [] },
  isJoinedSharedGroup: Boolean,
});
const emits = defineEmits(['searched']);

const isMedicineImageModalActive = ref(false);
const selectedMedicineImageURL = ref('');

function isInventoryLow(medicineOverview) {
  if (
    medicineOverview.inventory === undefined ||
    medicineOverview.inventory.unusedPackage !== 0
  )
    return false;

  const lowInventoryBorder =
    medicineOverview.dosageAndAdministration.quantity * 3;

  return medicineOverview.inventory.remainingQuantity <= lowInventoryBorder;
}

function isExpirationNear(medicineOverview) {
  if (medicineOverview.inventory === undefined) return false;

  const expiration = new Date(medicineOverview.inventory.expirationOn);
  var oneWeekAgoExpiration = new Date(expiration);
  oneWeekAgoExpiration.setDate(oneWeekAgoExpiration.getDate() - 7);
  const today = new Date();
  return oneWeekAgoExpiration <= today && today < expiration;
}

function isExpiration(medicineOverview) {
  if (medicineOverview.inventory === undefined) return false;

  const expiration = new Date(medicineOverview.inventory.expirationOn);
  const today = new Date();
  return expiration <= today;
}

function activateMedicineImageModal(url) {
  selectedMedicineImageURL.value = url;
  isMedicineImageModalActive.value = true;
}

function search(effect) {
  emits('searched', effect);
}
</script>
