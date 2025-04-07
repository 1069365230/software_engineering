<template>
  <h1>BOOKMARK SECTION</h1>
  <template v-if="bookmarkedEvents && bookmarkedEvents.length">
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

    <div>
      <table>
        <tr>
          <td></td>
          <td>Event Name</td>
        </tr>
        <tr
            v-for="event in bookmarkedEvents"
            :key="event.id"
            class="event_rows"
        >
          <td>
            <button
                v-on:click="unbookmark(event.globalId)"
                class="remove_from_section_button"
            >
              Unbookmark this Event
            </button>
          </td>
          <td>{{ event.name }}</td>
        </tr>
      </table>
    </div>
  </template>
  <template v-else>
    <h3>No Events are Bookmarked yet...</h3>
  </template>
</template>

<script>
import axios from "axios";
import {getApiBaseURL, getUserID, getUserToken} from "./globalEvent.js";
import fileDownload from "js-file-download";
import emitter from "tiny-emitter/instance";

export default {
  data() {
    return {
      userID: getUserID(),
      userToken: getUserToken(),
      apiBaseUrl: getApiBaseURL(),
      bookmarkedEvents: [],
    };
  },

  methods: {
    async render() {
      let result = await axios
          .get(this.apiBaseUrl + `marktag-service/user/${this.userID}/bookmarked-events`, {
            headers: {
              Authorization: getUserToken(),
            },
          })
          .catch((err) => {
            console.log(err);
          });

      if (result) {
        this.bookmarkedEvents = result.data;
      }
      console.log(this.bookmarkedEvents);
    },

    async exportEvents(format) {
      let filename = "";
      if (format === "CALENDAR") {
        filename = "bookmarked-events.ics";
      } else {
        filename = "bookmarked-events." + format;
      }

      console.log(format);
      await axios
          .get(
              this.apiBaseUrl + `export-service/user/${this.userID}/export/bookmarked-events/${format}`,
              {
                responseType: "blob",
                headers: {
                  Authorization: getUserToken(),
                }
              }
          )
          .then((res) => {
            fileDownload(res.data, filename);
          })
          .catch((err) => {
            console.log(err);
          });
    },

    async unbookmark(eventID) {
      console.log("eventID: " + eventID);
      const url = this.apiBaseUrl + `marktag-service/user/${this.userID}/event/${eventID}/unbookmark`;
      const result = await axios
          .put(url, {}, {
            headers: {
              Authorization: getUserToken(),
            },
          })
          .then((res) => {
            console.log(res);
            this.render();
          })
          .catch((err) => {
            console.log(url);
            console.log(err);
          });
      console.log(result);
    },
  },

  async mounted() {
    this.render();

    emitter.on("eventBookmarked", async () => {
      this.render();
    });
    //set the global events from event board component, get all the information from above
    console.log(this.globalEvents);
  },
};
</script>

<style>
table {
  border-collapse: collapse;
  border: none;
}

tr {
  border: 0;
  display: block;
}

td {
  width: 190px;
  height: 60px;
  border: none;
  padding: 3px;
}

.remove_from_section_button {
  width: 190px;
  height: 65px;
  border: none;
  border-radius: 9px;
}

.dropdown {
  width: 190px;
  height: 60px;
  border: 3px;
}

.tag_section {
  border: 1px;
}

.tag_items {
  text-align: left;
}

.add_tag_button {
  width: 190px;
  height: 30px;
}

.remove_tag_button {
  width: 190px;
  height: 30px;
}
</style>
