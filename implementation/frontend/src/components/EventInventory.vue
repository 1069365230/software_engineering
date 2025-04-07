<template>
  <h1>Posted Events</h1>
  <template v-if="eventInventory && eventInventory.length">
    <div class="table_board">
      <table>
        <tr>
          <td>Event Name</td>
          <td>Capacity</td>
          <td>City</td>
          <td>Country</td>
          <td>Start at</td>
          <td>End at</td>
          <td>Description</td>
          <td>Edit</td>
          <td>Message</td>
          <td>Download Report Pdf</td>
        </tr>
        <tr v-for="event in sortedInventory" :key="event.id" class="event_rows">
          <template v-if="event.organizerId === userID">
            <td>{{ event.name }}</td>
            <td>{{ event.maxCapacity }}</td>
            <td>{{ event.city }}</td>
            <td>{{ event.country }}</td>
            <td>{{ event.startDate }}</td>
            <td>{{ event.endDate }}</td>
            <td>{{ event.description }}</td>
            <td>
              <button v-on:click="increaseDay(event.id)">
                Shift Date
              </button>
            </td>
            <td>
              <input
                  v-model="event.message"
                  type="text"
                  placeholder="Write your message here..."
                  class="message-input"
              />
              <button v-on:click="sendMessage(event)">Send to Attendees</button>
            </td>
            <td>
              <button v-on:click="downloadPdf(event)">
                Download Pdf
              </button>
            </td>
          </template>
        </tr>
      </table>
    </div>
  </template>

  <template v-else>
    <h3>No Event posted yet...</h3>
  </template>

  <h1>Post a new Event</h1>
  <form @submit="onSubmit" id="event-inventory">
    <div class="form-group">
      <label for="eventName">Event Name:</label>
      <input type="text" id="event-name" v-model="eventJson.name" required/>
      <div class="column-space"></div>
      <label for="maxCapacity">Max Capacity:</label>
      <input type="number" id="event-size" v-model="eventJson.maxCapacity" required/>

      <label for="type">Type:</label>
      <input type="text" id="event-type" v-model="eventJson.type" required/>

      <label for="city">City:</label>
      <input type="text" id="forename-register" v-model="eventJson.city" required/>

      <label for="country">Country:</label>
      <input type="text" id="event-country" v-model="eventJson.country" required/>

      <label for="startDate">Start Date:</label>
      <input
          type="text"
          id="event-start"
          v-model="eventJson.startDate"
          placeholder="Format: Year/Month/Day"
          required
      />

      <label for="endDate">End Date:</label>
      <input
          type="text"
          id="event-end"
          v-model="eventJson.endDate"
          placeholder="Format: Year/Month/Day"
          required
      />

      <label for="description">Desciption:</label>
      <input type="text" id="event-end" v-model="eventJson.description" required/>
    </div>

    <button type="submit">Post</button>
  </form>
</template>

<script>
import axios from "axios";
import {getApiBaseURL, getUserID, getUserToken} from "./globalEvent.js";
import fileDownload from "js-file-download";

export default {
  data() {
    return {
      eventInventory: [],
      sortedInventory: [],
      userID: getUserID(),
      userToken: getUserToken(),
      apiBaseUrl: getApiBaseURL(),

      eventJson: {
        organizerId: getUserID(),
        name: "",
        maxCapacity: "",
        type: "",
        city: "",
        country: "",
        startDate: "",
        endDate: "",
        description: "",
      },
    };
  },
  methods: {
    async render() {
      await axios
          .get(this.apiBaseUrl + `event-inventory-service/events`, {
            headers: {
              Authorization: this.userToken,
            },
          })
          .then((response) => {
            this.eventInventory = response.data;
            this.sortedInventory = this.eventInventory.sort((a, b) => a.id - b.id);
          })
          .catch((err) => {
            console.log(err);
          });

    },

    async sendMessage(event) {
      console.log(`The message for event ${event.id} is: "${event.message}"`);

      try {
        const response = await axios.get(this.apiBaseUrl + `attendance-service/events/${event.id}/attendees`, {
          headers: {
            Authorization: this.userToken,
          },
        });

        const attendeeIds = response.data.map((attendee) => parseInt(attendee.id));
        const messageRequestDto = {
          organizerId: this.userID,
          message: event.message,
          recipientIds: attendeeIds
        };

        await axios
            .post(this.apiBaseUrl + `attendance-service/events/${event.id}/messages`, messageRequestDto, {
              headers: {
                Authorization: getUserToken(),
              },
            })
            .then(() => {
              console.log("Message sent");
              //location.reload();
              this.render();
            })
            .catch((error) => {
              console.log(error);
            });

      } catch (err) {
        console.log(err);
      }
    },

    async downloadPdf(event) {
      await axios
          .get(this.apiBaseUrl + `analyticsandreport-service/reports/${event.id}/pdf`, {
            responseType: "blob",
            headers: {
              Authorization: this.userToken,
            },
          })
          .then((response) => {
            console.log(response);
            fileDownload(response.data, `${this.userID}_${event.id}_report.pdf`);
          })
          .catch((err) => {
            console.log(err);
          });
    },

    async onSubmit(event) {
      event.preventDefault();
      console.log(this.eventJson);

      let startDateObject = new Date(this.eventJson.startDate);
      startDateObject.setHours(startDateObject.getHours() + 1);

      let endDateObject = new Date(this.eventJson.endDate);
      endDateObject.setHours(endDateObject.getHours() + 1);

      this.eventJson.startDate = startDateObject.toISOString();
      this.eventJson.endDate = endDateObject.toISOString();

      await axios
          .post(this.apiBaseUrl + `event-inventory-service/events`, this.eventJson, {
            headers: {
              Authorization: getUserToken(),
            },
          })
          .then((response) => {
            console.log("Event data sent:", response.data);
            //location.reload();
            this.eventJson = {
              organizerId: getUserID(),
              name: "",
              maxCapacity: "",
              type: "",
              city: "",
              country: "",
              startDate: "",
              endDate: "",
              description: "",
            };
            this.render();
          })
          .catch((error) => {
            console.log(error);
          });
    },
    async increaseDay(eventId) {
      await axios
          .put(this.apiBaseUrl + `event-inventory-service/events/${eventId}`, {}, {
            headers: {
              Authorization: getUserToken(),
            },
          })
          .catch((error) => {
            console.log(error);
          });
      this.render();
    },
  },
  async mounted() {
    this.render();
  },
};
</script>

<style>
form {
  width: 100%;
  max-width: 300px;
  margin: 0 auto;
}

.form-group {
  display: flex;
  flex-direction: column;
  margin-bottom: 10px;
}

.form-group label {
  margin-bottom: 5px;
  margin-top: 5px;
}

.form-group input {
  padding: 5px;
  width: 100%;
}

.alertSucc {
  background-color: #1c722a;
  color: #c5d2c7;
  padding: 12px;
  margin-top: 12px;
}

.alertFail {
  background-color: #721c1c;
  color: #c5d2c7;
  padding: 12px;
  margin-top: 12px;
}
</style>
