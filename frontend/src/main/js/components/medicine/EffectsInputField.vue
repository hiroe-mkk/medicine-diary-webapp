<template>
  <div
    class="notification py-3 px-5"
    :class="{
      'has-background-white': errors.length === 0,
      'has-background-danger-light': errors.length !== 0,
    }"
  >
    <div class="content m-3">
      <ul class="m-0">
        <li v-for="(effect, index) in effects">
          <div class="is-flex is-justify-content-space-between mb-1">
            <span>{{ effect }}</span>
            <span>
              <span
                class="icon fas is-clickable m-1 is-small"
                @click="editEffect(index)"
              >
                <i class="fa-solid fa-pen-to-square"></i>
              </span>
              <span
                class="icon fas is-clickable m-1 is-small"
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
      <div class="notification has-background-white py-3 px-5">
        <div class="has-text-right">
          <button
            class="delete"
            type="button"
            @click="isEditModalActive = false"
          ></button>
        </div>
        <p
          class="is-size-5 has-text-weight-bold has-text-link has-text-centered"
        >
          効能
        </p>
        <div class="field my-3">
          <div class="control">
            <input
              class="input is-info is-rounded"
              type="text"
              list="effectOptions"
              placeholder="その他"
              v-model="editingEffect.value"
              maxlength="30"
            />
            <datalist id="effectOptions">
              <option v-for="effectOption in effectOptions">
                {{ effectOption }}
              </option>
            </datalist>
          </div>
        </div>
        <div class="field is-grouped is-grouped-centered p-2">
          <p class="control">
            <button
              type="button"
              class="button is-small is-rounded is-link"
              @click="editCompleted()"
              :disabled="editingEffect.value === ''"
            >
              完了
            </button>
          </p>
          <p class="control">
            <button
              type="button"
              class="button is-small is-rounded is-outlined is-danger"
              @click="isEditModalActive = false"
            >
              キャンセル
            </button>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ArrayConverter } from '@main/js/composables/ArrayConverter.js';

const props = defineProps({
  effects: { type: String, default: '[]' },
  errors: { type: String, default: '[]' },
});

const effectOptions = [
  '頭痛',
  '発熱',
  'のどの痛み',
  '鼻づまり',
  '咳',
  '嘔吐',
  '下痢',
  '腹痛',
  '便秘',
  '食欲不振',
  '疲労感',
  '関節痛',
  '筋肉痛',
  '動悸',
  '呼吸困難',
  '皮膚の発疹',
  '目のかゆみ',
  '耳の痛み',
  '頻尿',
  '眠れない',
];
const effects = reactive([]);
const errors = reactive([]);

const isEditModalActive = ref(false);
const editingEffect = reactive({ value: '', index: 0 });

onMounted(() => {
  effects.push(...ArrayConverter.fromString(props.effects));
  errors.push(...new Set(ArrayConverter.fromString(props.errors)));
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
