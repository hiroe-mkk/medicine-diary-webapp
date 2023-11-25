<template>
  <div
    class="simple-massage message is-inline-block"
    :class="notificationMessage.color"
    v-if="isSimpleMessageActive"
  >
    <div class="message-body p-3">
      <div class="content">
        <strong>
          {{ notificationMessage.message }}
        </strong>
      </div>
    </div>
  </div>

  <div class="modal" :class="{ 'is-active': isDetailsMessageActive }">
    <div class="modal-background" @click="isDetailsMessageActive = false"></div>
    <div class="modal-content is-flex is-justify-content-center">
      <div class="message is-inline-block" :class="notificationMessage.color">
        <div class="message-body">
          <div class="content m-3">
            <div class="has-text-centered m-2">
              <strong class="is-size-5 mb-1">
                {{ notificationMessage.message }}
              </strong>
              <br />
              <small>{{ notificationMessage.details }}</small>
            </div>
            <div class="has-text-right">
              <button
                class="button is-small is-rounded"
                :class="notificationMessage.color"
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
import { ref, reactive, onMounted, defineExpose } from 'vue';
import { NotificationMessage } from '@main/js/composables/model/NotificationMessage.js';

const props = defineProps({
  type: String,
  message: String,
  details: String,
});
defineExpose({ activate });

const isSimpleMessageActive = ref(false);
const isDetailsMessageActive = ref(false);
const notificationMessage = reactive(new NotificationMessage());

onMounted(() => {
  activate(props.type, props.message, props.details);
});

function activate(type, message, details) {
  notificationMessage.set(type, message, details);
  if (!notificationMessage.hasMessage) return;

  if (notificationMessage.hasDetails) {
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