<template>
  <div class="content has-text-centered">
    <strong class="is-size-4 has-text-grey-dark">
      {{ props.username }}
    </strong>
  </div>
</template>
<script setup>
const props = defineProps({ username: String });
</script>
