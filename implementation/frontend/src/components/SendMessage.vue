<template>
  <div class="message-container">
    <input
        v-model="message"
        type="text"
        placeholder="Write your message here..."
        class="message-input"
    />
    <button v-on:click="sendMessage()" class="send-button">Send to Attendees</button>
  </div>
</template>

<script>
import {getApiBaseURL, getUserToken} from "./globalEvent.js";

export default {
  data() {
    return {
      apiBaseUrl: getApiBaseURL(),
      userToken: getUserToken(),
      message: "",
    };
  },
  methods: {
    async sendMessage() {
      console.log(`The message is: "${this.message}"`);
      /*
      // Commented out as the logic is not required yet
      await axios
          .post(this.apiBaseUrl + `your-service-endpoint`, {
            headers: {
              Authorization: this.userToken,
            },
            data: {
              // Your data here
            },
          })
          .then((response) => {
            console.log(response);
          })
          .catch((error) => {
            console.log(error);
          });
      */
    },
  },
};
</script>

<style scoped>
.message-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.message-input {
  width: 300px;
  height: 30px;
  margin-right: 10px;
  padding: 5px;
}

.send-button {
  height: 40px;
  background-color: lightblue;
  border: none;
  border-radius: 5px;
  padding: 0 20px;
}
</style>
