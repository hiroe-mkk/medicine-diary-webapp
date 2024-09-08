<template>
  <div class="container is-max-tablet p-3">
    <div class="has-text-centered">
      <div class="message is-link is-inline-block has-text-weight-semibold">
        <p class="message-body has-text-left">
          共有グループに招待したい方のメールアドレスを入力してください。<br />
          入力されたメールアドレス宛てに、招待メールをお送りします。
        </p>
      </div>
    </div>
    
    <form class="form" method="post" @submit.prevent="submitInviteForm()">
      <div class="field has-text-left my-3">
        <label class="label">メールアドレス</label>
        <div class="control">
          <input
            class="input is-info is-rounded"
            type="email"
            name="emailAddress"
            v-model="editingEmailAddress"
            :class="{ 'is-danger': fieldErrors.contains('emailAddress') }"
          />
        </div>

        <p class="help">※ 招待は、メール送信後1週間有効です。</p>
        <p
          class="help is-danger"
          v-for="error in fieldErrors.get('emailAddress')"
        >
          {{ error }}
        </p>
      </div>
      <div class="field is-grouped is-grouped-centered p-2">
        <p class="control">
          <button
            class="button is-rounded is-link"
            :disabled="editingEmailAddress.trim() === ''"
          >
            送信する
          </button>
        </p>
      </div>
    </form>

    <div class="block has-text-left mt-6">
      <a class="icon-text is-size-6" href="/shared-group">
        <span class="icon has-text-link m-0">
          <i class="fa-solid fa-angle-left"></i>
        </span>
        <strong class="has-text-link">共有グループ管理画面にもどる</strong>
      </a>
    </div>
  </div>
</template>

<script setup>
import {
    HttpRequestClient,
    HttpRequestFailedError,
} from '@main/js/composables/HttpRequestClient.js';
import { FieldErrors } from '@main/js/composables/model/FieldErrors.js';
import { inject, reactive, ref } from 'vue';

const props = defineProps({
  csrf: String,
});
const activateResultMessage = inject('activateResultMessage');

const editingEmailAddress = ref('');
const fieldErrors = reactive(new FieldErrors());

function submitInviteForm() {
  fieldErrors.clear();

  const form = new FormData();
  form.set('emailAddress', editingEmailAddress.value);
  form.set('_csrf', props.csrf);

  HttpRequestClient.submitPostRequest('/api/shared-group/invite', form)
    .then(() => {
      activateResultMessage(
        'INFO',
        '共有グループに招待するメールを送信しました。',
        'メールが届かない場合は、メールアドレスが正しく入力されているか、または迷惑メールフォルダを確認してください。'
      );
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
</script>
