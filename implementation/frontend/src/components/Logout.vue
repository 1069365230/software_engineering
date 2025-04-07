<template>
  <button v-on:click="logout()">Logout</button>
</template>

<script>
import { setUserToken, setUserID } from "./globalEvent.js";

export default {
  data() {
    return {};
  },
  methods: {
    logout() {
        setUserToken("");
        setUserID("");
        this.$router.push({ name: "DashBoardLogin" });
    },
  },
};
</script>



<style>
</style>
