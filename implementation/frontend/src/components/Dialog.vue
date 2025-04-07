<template>
  <div class="popup">
    <div class="inner">
      <slot />
      <button class="close-popup" v-on:click=Toggle>close</button>
    </div>
  </div>
</template>

<script>
export default {
  props: ['Toggle'],
  data() {
    return {
      
    };
  },
  methods: {

  },
};
</script>

<style lang="scss" scoped>
.popup {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 99;
  background-color: rgb(122, 122, 122, 0.6);

  display: flex;
  align-items: center;
  justify-content: center;
}
.inner {
  background-color: azure;
  padding: 90px;
}
</style>
