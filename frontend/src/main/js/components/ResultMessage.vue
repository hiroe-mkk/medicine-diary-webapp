<template>
  <div
    class="simple-massage message is-inline-block"
    :class="resultMessage.color"
    v-if="isSimpleMessageActive"
  >
    <div class="message-body p-3">
      <div class="content">
        <strong class="title is-6">
          {{ resultMessage.message }}
        </strong>
      </div>
    </div>
  </div>

  <div class="modal" :class="{ 'is-active': isDetailsMessageActive }">
    <div class="modal-background" @click="isDetailsMessageActive = false"></div>
    <div class="modal-content is-flex is-justify-content-center">
      <div class="message is-inline-block" :class="resultMessage.color">
        <div class="message-body">
          <div class="content">
            <div class="has-text-centered m-2">
              <strong class="is-5 mb-1">
                {{ resultMessage.message }}
              </strong>
              <br />
              <small class="is-6">{{ resultMessage.details }}</small>
            </div>
            <div class="has-text-right">
              <button
                class="button is-small is-rounded"
                :class="resultMessage.color"
                @click="isDetailsMessageActive = false"
              >
                OK
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ResultMessage } from '@main/js/composables/model/ResultMessage.js';

const props = defineProps({
  type: String,
  message: String,
  details: String,
});

const isSimpleMessageActive = ref(false);
const isDetailsMessageActive = ref(false);
const resultMessage = reactive(new ResultMessage());

onMounted(() => {
  activate(props.type, props.message, props.details);
});

function activate(type, message, details) {
  resultMessage.set(type, message, details);
  if (!resultMessage.hasMessage) return;

  if (resultMessage.hasDetails) {
    isDetailsMessageActive.value = true;
  } else {
    isSimpleMessageActive.value = true;
    setTimeout(() => {
      isSimpleMessageActive.value = false;
    }, 5000);
  }
}
</script>

<style scoped>
.simple-massage {
  position: fixed;
  top: 4rem;
  right: 0.5rem;
  z-index: 20;
}
</style>
