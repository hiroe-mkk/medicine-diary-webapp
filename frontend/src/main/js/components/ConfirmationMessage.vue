<template>
  <div class="modal" :class="{ 'is-active': isModalActive }">
    <div class="modal-background" @click="isModalActive = false"></div>
    <div class="modal-content is-flex is-justify-content-center">
      <div class="message is-inline-block is-info">
        <div class="message-body">
          <div class="content">
            <p class="has-text-centered mb-2">
              <strong class="is-size-5 mb-1">
                {{ notificationMessage.message }}
              </strong>
              <br />
            </p>
            <p class="has-text-left mb-2">
              <small class="has-text-weight-semibold">
                {{ notificationMessage.details }}
              </small>
            </p>
            <form class="form" method="post" :action="props.path">
              <input name="_csrf" :value="props.csrf" hidden />
              <template v-for="(value, name) in params.values">
                <input :name="name" :value="value" hidden />
              </template>
              <div class="field is-grouped is-grouped-centered p-2">
                <p class="control">
                  <button class="button is-small is-rounded is-link">
                    {{ props.buttonLabel }}
                  </button>
                </p>
                <p class="control">
                  <button
                    type="button"
                    class="button is-small is-rounded is-outlined is-danger"
                    @click="isModalActive = false"
                  >
                    キャンセル
                  </button>
                </p>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { NotificationMessage } from '@main/js/composables/model/NotificationMessage.js';

const props = defineProps({
  message: String,
  details: String,
  path: String,
  buttonLabel: String,
  csrf: String,
});
defineExpose({ activate });

const isModalActive = ref('');
const notificationMessage = reactive(new NotificationMessage());
const params = reactive({ values: undefined });

function activate(values) {
  notificationMessage.set('', props.message, props.details);
  if (!notificationMessage.hasMessage) return;
  params.values = values;

  isModalActive.value = true;
}
</script>