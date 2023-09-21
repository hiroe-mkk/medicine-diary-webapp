<template>
  <nav id="navbar" class="navbar is-fixed-top has-background-link-dark">
    <div class="navbar-brand is-flex is-justify-content-space-between">
      <span
        class="navbar-burger has-text-white"
        :class="{ 'is-active': isOpened }"
        @click="isOpened = !isOpened"
      >
        <span></span>
        <span></span>
        <span></span>
      </span>
    </div>

    <div class="navbar-menu" :class="{ 'is-active': isOpened }">
      <div class="navbar-end"></div>
    </div>
  </nav>
</template>

<script setup>
import { ref } from 'vue';

const isOpened = ref(false);
</script>