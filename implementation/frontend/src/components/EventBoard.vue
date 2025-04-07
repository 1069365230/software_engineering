<template>
  <h1>EVENT BOARD</h1>
  <button v-on:click="refreshBoard()" class="refreash-eventboard-button">
    Refresh Event Board
  </button>
  <template v-if="allEvents && allEvents.length">
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
          <td class="tag_column">Tags</td>
          <td>Tag Selection</td>
        </tr>
        <tr v-for="event in sortedEvents" :key="event.id" class="event_rows">
          <td>{{ event.name }}</td>
          <td>{{ event.maxCapacity }}</td>
          <td>{{ event.city }}</td>
          <td>{{ event.country }}</td>
          <td>{{ event.startDate.split("T")[0] }}</td>
          <td>{{ event.endDate.split("T")[0] }}</td>
          <td>{{ event.description }}</td>
          <td>
            <li v-for="tag in event.tags" :key="tag" class="tag_items">
              {{ tag }}
            </li>
          </td>
          <td>
            <select
                name="event_tag"
                v-on:change="selectTag($event, event)"
                class="dropdown"
            >
              <option>Select Tag</option>
              <option value="EDUCATION">Education</option>
              <option value="SPORT">Sport</option>
              <option value="FOOD">Food</option>
            </select>
          </td>
          <td v-if="event.selectedTag !== ''" class="tag_section">
            <button
                v-on:click="tagHandling(event, 'add')"
                class="add_tag_button"
            >
              Add this Tag
            </button>
            <button
                v-if="event.tags.length !== 0"
                v-on:click="tagHandling(event, 'remove')"
                class="remove_tag_button"
            >
              Remove this Tag
            </button>
          </td>

          <td>
            <button v-on:click="getRating(event.id)">
              See Rating and Feedback
            </button>
            <Dialog v-if="trigger" :Toggle="toggleDialog">
              <h3>Feedback and Rating</h3>
              <h6>Average Location Rating: {{ this.LocationScore }}</h6>
              <h6>Average Overall Rating: {{ this.OverallScore }}</h6>
              <h6>Average Desciption Rating: {{ this.DescriptionScore }}</h6>
              <div class="smaller_space"></div>
              <h3>Feedback</h3>
              <ul>
                <li
                    v-for="feedback in feedbacks"
                    :key="feedback.id"
                    class="feedback_items"
                >
                  {{ feedback.attendeeName }}: {{ feedback.comment }}
                </li>
              </ul>
              <p v-if="feedbacks.length === 0">{{ this.Feedback }}</p>
            </Dialog>
            <div class="smaller_space"></div>
            <button v-on:click="bookmark(event.id)">Bookmark this Event</button>
            <div class="smaller_space"></div>
            <button v-on:click="attend(event.id)">Attend this Event</button>
          </td>
        </tr>
      </table>
    </div>
  </template>
  <template v-else>
    <h3>No Events are posted yet...</h3>
  </template>
</template>

<script>
import axios from "axios";
import {getApiBaseURL, getUserID, getUserToken} from "./globalEvent.js";
import Dialog from "./Dialog.vue";
import BookmarkSection from "./BookmarkSection.vue";
import emitter from "tiny-emitter/instance";

export default {
  components: {
    Dialog,
    BookmarkSection,
  },

  data() {
    return {
      apiBaseUrl: getApiBaseURL(),
      userID: getUserID(),
      userToken: getUserToken(),

      trigger: false,

      eventJson: {
        organizerId: "",
        name: "",
        maxCapacity: "",
        type: "",
        city: "",
        country: "",
        startDate: "",
        endDate: "",
        description: "",
      },

      attendJson: {
        eventId: "",
      },

      //updated everytime getRating is called
      LocationScore: "No score yet",
      OverallScore: "No score yet",
      DescriptionScore: "No score yet",
      Feedback: "No feedback yet",

      //this all event will contain the tag information too
      allEvents: [],
      sortedEvents: [],
      feedbacks: [],
    };
  },
  methods: {
    //displays all the events, including the tags from marktag service
    async render() {
      let result = await axios
          .get(this.apiBaseUrl + `event-inventory-service/events`, {
            headers: {
              Authorization: getUserToken(),
            },
          })
          .catch((err) => {
            console.log(err);
          });

      if (result) {
        this.allEvents = result.data.map((event) => {
          return {
            ...event,
            selectedTag: "",
          };
        });
      }

      for (let i = 0; i < this.allEvents.length; i++) {
        //let event = this.allEvents[i];
        let globalEventId = this.allEvents[i].id;
        //user id need to be set
        await axios
            .get(
                this.apiBaseUrl + `marktag-service/user/${this.userID}/event/` +
                globalEventId +
                "/tags", {
                  headers: {
                    Authorization: getUserToken(),
                  },
                }
            )
            .then((response) => {
              console.log(response.data);
              this.allEvents[i].tags = response.data;
            })
            .catch((error) => {
              console.log(error);
            });
      }
      this.sortedEvents = this.allEvents.sort((a, b) => a.id - b.id);
    },

    //inventory service
    constructEventJson(
        organizerId,
        name,
        maxCapacity,
        type,
        city,
        country,
        startDate,
        endDate,
        description
    ) {
      this.eventJson.organizerId = organizerId;
      this.eventJson.name = name;
      this.eventJson.maxCapacity = maxCapacity;
      this.eventJson.type = type;
      this.eventJson.city = city;
      this.eventJson.country = country;
      this.eventJson.startDate = startDate;
      this.eventJson.endDate = endDate;
      this.eventJson.description = description;
    },
    async refreshBoard() {
      this.render();
    },

    //feedback service
    toggleDialog() {
      this.trigger = !this.trigger;
    },
    async setScore(eventID) {
      //getting feeback from feedback service

      const urlAnalytics = this.apiBaseUrl + `analyticsandreport-service/analytics/event/${eventID}`;
      const resultAnalytics = await axios.get(urlAnalytics, {
        headers: {
          Authorization: getUserToken(),
        },
      }).catch((err) => {
        console.log(urlAnalytics + ": " + err);
      });

      if (resultAnalytics) {
        this.LocationScore = resultAnalytics.data.locationRatingAvg;
        this.DescriptionScore = resultAnalytics.data.descriptionRatingAvg;
        this.OverallScore = resultAnalytics.data.overallRatingAvg;
      }

      const urlFeedback = this.apiBaseUrl + `feedback-service/feedbacks/event/${eventID}`;
      const resultFeedback = await axios.get(urlFeedback, {
        headers: {
          Authorization: getUserToken(),
        },
      }).catch((err) => {
        console.log(urlFeedback + " " + err);
      });

      if (resultFeedback) {
        this.feedbacks = resultFeedback.data;
        // console.log([0].attendeeName);
        // resultFeedback.data.forEach(function (item, index) {
        //   console.log(index, item);
        // });
      }
    },
    getRating(eventID) {
      this.setScore(eventID);
      this.toggleDialog();
      console.log("get rating");
    },

    //marktag service
    async bookmark(eventID) {
      console.log("book mark this event ID: " + eventID);

      const url = this.apiBaseUrl + `marktag-service/user/${this.userID}/event/${eventID}/bookmark`;
      await axios.put(url, {}, {
        headers: {
          Authorization: getUserToken(),
        },
      }).catch((err) => {
        console.log(err);
      });

      //notify bookmark compoent
      emitter.emit("eventBookmarked", "updateBookmarkBoard");
    },
    //UI part for add and remove a tag
    selectTag(e, event) {
      if (e.target.value == "Select Tag") {
        event.selectedTag = "";
      } else {
        event.selectedTag = e.target.value;
      }

      console.log(event.selectedTag);
    },
    async tagHandling(event, action) {
      const url = this.apiBaseUrl + `marktag-service/user/${this.userID}/event/${event.id}/${action}/${event.selectedTag}`;

      console.log(url);
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

    //attendance service
    constructAttendanceJson(eventID) {
      this.attendJson.eventId = eventID;
    },
    async attend(eventID) {
      console.log("attend this event ID: " + eventID);

      this.constructAttendanceJson(eventID);
      const url = this.apiBaseUrl + `attendance-service/attendees/${this.userID}/event-bookings`;
      await axios.post(url, this.attendJson, {
        headers: {
          Authorization: getUserToken(),
        },
      }).catch((err) => {
        console.log(err);
      });

      //notify attendance component
      emitter.emit("eventAttended", "updateAttendace");
    },
  },
  async mounted() {
    this.render();
  },
};
</script>
<style>
td {
  width: 190px;
  height: 30px;
}

.table_board {
  display: flex;
  justify-content: center;
}

.event_rows {
  border: 0;
  display: block;
  margin: 5px;
  border: 3px rgb(71, 71, 71) solid;
  border-radius: 9px;
  background-color: rgb(230, 230, 230);
}

.tag_column {
  text-align: left;
}

.smaller_space {
  margin-top: 10px;
}

.space {
  margin-top: 20px;
}
</style>
