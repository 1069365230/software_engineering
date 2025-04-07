<template>

  <h1>RECOMMENDATION SECTION</h1>
  <button
      v-on:click="changeEmailPreference(true)">
    Opt-in to Suggestion Emails
  </button>
  <button
      v-on:click="changeEmailPreference(false)">
    Opt-out of Suggestion Emails
  </button>
  <h3 v-if="recommendations.length === 0">No Recommendations yet</h3>
  <p
      v-for="recommendation in recommendations"
      :key="recommendation.id"
      class="recommendation_items"
  >
    Event name: {{ recommendation.name }} Type: {{ recommendation.type }} City:
    {{ recommendation.city }} Country: {{ recommendation.country }} Starting
    Date: {{ recommendation.startDate }} Ending Date:
    {{ recommendation.endDate }} RelevanceScore:
    {{ recommendation.relevanceScore }}
  </p>
</template>

<script>
import axios from "axios";
import {getApiBaseURL, getUserID, getUserToken} from "./globalEvent.js";

export default {
  data() {
    return {
      recommendations: [],
      userID: getUserID(),
      userToken: getUserToken(),
      apiBaseUrl: getApiBaseURL(),
    };
  },
  methods: {
    async render() {
      let result = await axios
          .get(this.apiBaseUrl + `recommender-service/attendees/${this.userID}/recommendations`, {
            headers: {
              Authorization: getUserToken(),
            },
          })
          .catch((err) => {
            console.log(err);
          });

      if (result) {
        this.recommendations = result.data;
      }
    },

    async changeEmailPreference(preferenceBoolean) {
      console.log("Change Email Preference to: " + preferenceBoolean);

      const url = this.apiBaseUrl + `recommender-service/attendees/${this.userID}/preferences`;
      await axios
        .patch(url, {
          receivePromotionalEmails: preferenceBoolean,
        },
        {
          headers: {
            Authorization: getUserToken(),
          },
        })
        .then((res) => {
          console.log(res);
        })
        .catch((err) => {
          this.preferenceError = err.response.data;
          this.notificationFadeOutFail();
          console.log(err.response.data);
        });
    },
  },
  async mounted() {
    this.render();
  },
};
</script>


<style>
</style>
