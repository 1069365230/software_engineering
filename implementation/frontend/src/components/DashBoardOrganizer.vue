<template>

  <div class="component-inventory-organizer">
    <LogoutVue/>
    <EventInventoryVue/>
  </div>
</template>


<script>
import EventInventoryVue from "./EventInventory.vue";
import LogoutVue from "./Logout.vue";

export default {
  name: "App",
  components: {
    LogoutVue,
    EventInventoryVue,
  },
};
</script>

<style>
</style>