<template>
  <div
    class="notification mb-0"
    :class="{
      'has-background-white-bis': !hasErrors,
      'has-background-danger-light': hasErrors,
    }"
  >
    <div class="content mb-3">
      <ul class="mt-0">
        <li v-for="(effect, index) in effects">
          <div class="is-flex is-justify-content-space-between mb-1">
            <span>{{ effect }}</span>
            <span>
              <span
                class="icon is-small fas is-clickable m-1"
                @click="editEffect(index)"
              >
                <i class="fa-solid fa-pen-to-square"></i>
              </span>
              <span
                class="icon is-small fas is-clickable m-1"
                @click="deleteEffect(index)"
              >
                <i class="fa-solid fa-circle-minus"></i>
              </span>
            </span>
          </div>
          <input type="text" name="effects" :value="effect" hidden />
        </li>
      </ul>
    </div>
    <div class="has-text-right">
      <button
        class="button is-small is-info is-rounded"
        type="button"
        @click="addEffect()"
      >
        追加する
      </button>
    </div>
    <p class="help is-danger" v-for="error in errors">{{ error }}</p>
  </div>

  <div class="modal" :class="{ 'is-active': isEditModalActive }">
    <div class="modal-background" @click="isEditModalActive = false"></div>
    <div class="modal-content">
      <div class="notification is-white">
        <div class="title is-5 has-text-link-dark has-text-centered">効能</div>
        <section class="content">
          <div class="field">
            <div class="control">
              <input
                class="input is-info"
                type="text"
                v-model="editingEffect.value"
                maxlength="30"
                placeholder="例) 頭痛"
              />
            </div>
          </div>
          <div class="field is-grouped is-grouped-centered">
            <p class="control">
              <button
                type="button"
                class="button is-small is-rounded is-link"
                @click="editCompleted()"
              >
                完了
              </button>
            </p>
            <p class="control">
              <button
                type="button"
                class="button is-small is-rounded is-danger"
                @click="isEditModalActive = false"
              >
                キャンセル
              </button>
            </p>
          </div>
        </section>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { ArrayConverter } from '@main/js/composables/ArrayConverter.js';

const props = defineProps({
  effects: { type: String, default: '[]' },
  errors: { type: String, default: '[]' },
});

const effects = reactive([]);
const errors = reactive([]);
const hasErrors = computed(() => errors.length !== 0);

const isEditModalActive = ref(false);
const editingEffect = reactive({ value: '', index: 0 });

onMounted(() => {
  effects.push(...ArrayConverter.fromString(props.effects));
  errors.push(...ArrayConverter.fromString(props.errors));
});

function addEffect() {
  editingEffect.value = '';
  editingEffect.index = effects.length;
  isEditModalActive.value = true;
}

function editEffect(index) {
  editingEffect.value = effects[index];
  editingEffect.index = index;
  isEditModalActive.value = true;
}

function editCompleted() {
  if (editingEffect.value.length !== 0) {
    effects[editingEffect.index] = editingEffect.value.trim();
  }
  isEditModalActive.value = false;
}

function deleteEffect(index) {
  effects.splice(index, 1);
}
</script>
