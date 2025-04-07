<template>
  <h1>Services Health</h1>
  <div v-if="loading">
    <img src="@/assets/loading.gif" alt="Loading spinner"/>
  </div>
  <div v-else>
    <div class="component-container">
      <table>
        <tr>
          <td>Services Name</td>
          <td>Status</td>
        </tr>
        <tr v-for="(value, key) in services" :key="key">
          <td>{{ key }}</td>
          <td :class="{ green: value === 'UP', red: value === 'DOWN' }">{{ value }}</td>
        </tr>
      </table>
      <div class="column-space"></div>
      <div>
        <h1 v-if="downCheck">All Services are down, help</h1>
        <img v-if="downCheck" src="@/assets/down.gif" alt="GIF Image"/>
      </div>
    </div>
  </div>
  <button v-on:click="refresh()">Refresh</button>
</template>

<script>
import axios from "axios";
import {getApiBaseURL, getUserToken} from "./globalEvent.js";

export default {
  data() {
    return {
      loading: false,
      apiBaseUrl: getApiBaseURL(),
      services: {
        "login-service:8083": "DOWN",
        "recommender-service:8087": "DOWN",
        "export-service:8089": "DOWN",
        "marktag-service:8086": "DOWN",
        "attendance-service:8091": "DOWN",
        "api-gateway:8081": "DOWN",
        "event-inventory-service:8084": "DOWN",
        "analyticsandreport-service:8090": "DOWN",
        "feedback-service:8088": "DOWN",
        "notification-service:8092": "DOWN",
      },
    };
  },
  methods: {
    async render() {
      this.loading = true;
      await axios
          .get(this.apiBaseUrl + "login-service/maintenance/health", {
            headers: {
              Authorization: getUserToken(),
            },
          })
          .then((response) => {
            response.data.forEach(serviceHealthDto => {
              if (serviceHealthDto && serviceHealthDto["serviceName"]) {
                this.services[serviceHealthDto["serviceName"]] = serviceHealthDto["isUp"] === true ? "UP" : "DOWN";
              }
            });
            this.loading = false;
          })
          .catch((error) => {
            console.log(error);
            this.loading = false;
          });
    },

    async refresh() {
      this.render();
    },
  },
  async mounted() {
    this.render();
  },
  computed: {
    downCheck() {
      return Object.values(this.services).every((key) => key === "DOWN");
    },
  },
};
</script>


<style>
.green {
  background-color: rgb(0, 169, 0);
}

.red {
  background-color: rgb(169, 0, 0);
}
</style>
