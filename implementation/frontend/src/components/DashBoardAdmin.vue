<template>

    <div class="component-health-admin">
      <LogoutVue />
      <HealthSectionVue />
    </div>
</template>


<script>
import HealthSectionVue from "./HealthSection.vue";
import LogoutVue from "./Logout.vue";

export default {
  name: "App",
  components: {
    LogoutVue,
    HealthSectionVue,
  },
};
</script>

<style>
</style>