<template>
  <div class="field is-horizontal my-3">
    <div class="field-label is-flex is-align-items-center">
      <label class="label has-text-grey">所有</label>
    </div>
    <div class="field-body">
      <div class="field">
        <div class="control">
          <div class="notification has-background-white-bis py-3 px-5">
            <div class="field my-3">
              <div class="field-body">
                <div class="field is-narrow">
                  <div class="control">
                    <label class="radio">
                      <input
                        type="radio"
                        name="isWantToOwn"
                        value="true"
                        :checked="isWantToOwn"
                        @click="isWantToOwn = true"
                      />
                      <strong class="has-text-grey"
                        >あなたのお薬として登録する</strong
                      >
                    </label>
                    <label class="radio">
                      <input
                        type="radio"
                        name="isWantToOwn"
                        value="false"
                        :checked="!isWantToOwn"
                        @click="isWantToOwn = false"
                      />
                      <strong class="has-text-grey"
                        >共有グループのお薬として登録する</strong
                      >
                    </label>
                  </div>
                </div>
              </div>
            </div>
            <div class="field my-3" v-show="isWantToOwn">
              <div class="field-body">
                <div class="field">
                  <div class="control">
                    <label class="checkbox">
                      <input
                        type="checkbox"
                        name="isPublic"
                        value="true"
                        :checked="isPublic"
                        @click="isPublic = !isPublic"
                      />
                      <strong class="has-text-grey">
                        メンバーからの閲覧を許可する
                      </strong>
                    </label>
                  </div>
                </div>
              </div>
            </div>
            <p class="help">
              ※共有グループのお薬として登録すると、他のメンバーもこのお薬を管理できます。
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const props = defineProps({
  isWantToOwn: Boolean,
  isPublic: Boolean,
});

const isWantToOwn = ref(props.isWantToOwn);
const isPublic = ref(props.isPublic);
</script>
