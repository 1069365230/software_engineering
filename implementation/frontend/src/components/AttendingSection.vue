<template>
  <h1>ATTENDANCE SECTION</h1>
  <template v-if="attendingEvents && attendingEvents.length">
    <button v-on:click="exportEvents('JSON')" class="export_button">
      Export as JSON
    </button>
    <button v-on:click="exportEvents('CALENDAR')" class="export_button">
      Export as Calendar
    </button>
    <button v-on:click="exportEvents('XML')" class="export_button">
      Export as XML
    </button>

    <div class="space"></div>
    <div v-if="isAlertVisibleFail" class="alertFail">
      {{ attendingError }}
    </div>
    <div>
      <table>
        <tr>
          <td></td>
          <td>Event Name</td>
        </tr>
        <tr v-for="event in attendingEvents" :key="event.id" class="event_rows">
          <td>
            <button
                v-on:click="unattend(event.eventId)"
                class="remove_from_section_button"
            >
              Unattend this Event
            </button>
          </td>
          <td>{{ event.eventName }}</td>
          <td>
            <button v-on:click="writeFeedback()">
              Rate and Feedback
            </button>
            <Dialog v-if="trigger" :Toggle="toggleDialog">
              <h3>Give a Rating</h3>
              <div class="slider_container">
                <h4 class="padding_space">Location</h4>
                <input
                    type="range"
                    v-model="locationRatingSliderValue"
                    min="0"
                    max="10"
                    step="1"
                    class="padding_space"
                />
                <p>Rating score: {{ locationRatingSliderValue }}</p>
              </div>
              <div class="slider_container">
                <h4 class="padding_space">Overall</h4>
                <input
                    type="range"
                    v-model="overallRatingSliderValue"
                    min="0"
                    max="10"
                    step="1"
                    class="padding_space"
                />
                <p>Rating score: {{ overallRatingSliderValue }}</p>
              </div>
              <div class="slider_container">
                <h4 class="padding_space">Description</h4>
                <input
                    type="range"
                    v-model="descriptionRatingSliderValue"
                    min="0"
                    max="10"
                    step="1"
                    class="padding_space"
                />
                <p>Rating score: {{ descriptionRatingSliderValue }}</p>
              </div>
              <h3>Write a Feedback</h3>
              <div class="smaller_space"></div>
              <textarea
                  v-model="message"
                  placeholder="write some feedback"
              ></textarea>
              <div class="smaller_space"></div>
              <button
                  v-on:click="sendFeedback(event.eventId, message, this.locationRatingSliderValue, this.descriptionRatingSliderValue, this.overallRatingSliderValue)">
                Send
              </button>
            </Dialog>
          </td>
          <td>
            <button v-on:click="retrieveQrCode(event.ticketSerialNr)" class="export_button">
             Download Ticket
           </button>
          </td>
        </tr>
      </table>
    </div>
  </template>
  <template v-else>
    <h3>Not attending any Events yet...</h3>
  </template>
</template>

<script>
import axios from "axios";
import {getApiBaseURL, getUserID, getUserToken} from "./globalEvent.js";
import fileDownload from "js-file-download";
import Dialog from "./Dialog.vue";
import emitter from 'tiny-emitter/instance';

export default {
  components: {
    Dialog,
  },

  data() {
    return {
      attendingError: "",
      isAlertVisibleFail: false,
      userID: getUserID(),
      userToken: getUserToken(),
      apiBaseUrl: getApiBaseURL(),

      locationRatingSliderValue: 0,
      descriptionRatingSliderValue: 0,
      overallRatingSliderValue: 0,

      trigger: false,
      attendingEvents: [],

      feedbackJson: {
        comment: "",
        eventId: 0,
        attendeeId: this.userID,
        locationrating: 0,
        descriptionrating: 0,
        overallrating: 0,
      },
    };
  },
  methods: {
    async render() {
      //attendees/{id}/event-bookings
      //should get all the attending events
      const url = "";
      let result = await axios
          .get(this.apiBaseUrl + `attendance-service/attendees/${this.userID}/event-bookings`, {
            headers: {
              Authorization: getUserToken(),
            },
          })
          .catch((err) => {
            console.log(err);
          });

      if (result) {
        console.log(result.data);
        this.attendingEvents = result.data;
      }
    },

    notificationFadeOutFail() {
      this.isAlertVisibleFail = true;
      setTimeout(() => {
        this.isAlertVisibleFail = false;
      }, 3000);
    },

    //attendance service
    async unattend(eventID) {
      //remove attance from attandance service
      console.log("Unattend this event with ID: " + eventID);

      const url = this.apiBaseUrl + `attendance-service/attendees/${this.userID}/event-bookings?eventId=${eventID}`;
      await axios
        .delete(url, {
          headers: {
            Authorization: getUserToken(),
          },
        })
        .then((res) => {
          console.log(res);
          this.render();
        })
        .catch((err) => {
          this.attendingError = err.response.data;
          this.notificationFadeOutFail();
          console.log(err.response.data);
        });
    },

    //attendance service
    async retrieveQrCode(ticketId) {
      let filename = ticketId + ".jpg";
      // Retrieve QR-Code from event-booking
      console.log("Retrieve ticket with ID: " + ticketId);

      await axios
          .get(
              this.apiBaseUrl + `attendance-service/attendees/${this.userID}/tickets/${ticketId}`,
              {
                responseType: "blob",
                headers: {
                  Authorization: getUserToken(),
                },
              }
          )
          .then((res) => {
            fileDownload(res.data, filename);
          })
          .catch((err) => {
            console.log(err);
          });
    },


    //export service
    async exportEvents(format) {
      let filename = "";
      if (format === "CALENDAR") {
        filename = "attending-events.ics";
      } else {
        filename = "attending-events." + format;
      }

      console.log(format);
      await axios
          .get(
              this.apiBaseUrl + `export-service/user/${this.userID}/export/attending-events/${format}`,
              {
                responseType: "blob",
                headers: {
                  Authorization: getUserToken(),
                },
              }
          )
          .then((res) => {
            fileDownload(res.data, filename);
          })
          .catch((err) => {
            console.log(err);
          });
    },

    //feedback service
    toggleDialog() {
      this.trigger = !this.trigger;
    },
    writeFeedback() {
      this.toggleDialog();
    },
    constructFeedbackJson(
        eventID,
        message,
        locationRating,
        descriptionRating,
        overallRating
    ) {
      this.feedbackJson.comment = message;
      this.feedbackJson.eventId = eventID;
      this.feedbackJson.attendeeId = this.userID;
      this.feedbackJson.locationrating = locationRating;
      this.feedbackJson.descriptionrating = descriptionRating;
      this.feedbackJson.overallrating = overallRating;
    },
    async sendFeedback(eventID, message, locationRating, descriptionRating, overallRating) {

      this.constructFeedbackJson(eventID, message, locationRating, descriptionRating, overallRating);

      console.log("json that is being send to feedback service: " + this.feedbackJson);
      await axios
          .post(this.apiBaseUrl + "feedback-service/feedbacks/new", this.feedbackJson, {
            headers: {
              Authorization: getUserToken(),
            },
          })
          .then((response) => {
            console.log("Data sent:", response.data);
          })
          .catch((error) => {
            console.log(error);
          });

      //close the popup
      this.toggleDialog();
    },
  },

  async mounted() {
    this.render();

    emitter.on("eventAttended", async () => {
      this.render();
    });
  },
};
</script>

<style>
td {
  width: 190px;
  height: 30px;
}

.slider_container {
  display: flex;
  align-items: center;
}

.padding_space {
  margin-right: 10px;
}
</style>
