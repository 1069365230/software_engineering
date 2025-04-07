<template>
  <h1>{{ badLogin }}</h1>
  <div class="component-container">
    <h1>Attendee Login</h1>
    <form @submit="onSubmit">
      <div class="form-group">
        <label for="username">Username:</label>
        <input type="text" id="username" v-model="username"/>
        <label for="password">Password:</label>
        <input type="password" id="password" v-model="password"/>
      </div>
      <button type="submit">Login</button>
      <p>{{ errorMessage }}</p>
    </form>
    <div class="column-space"></div>
    <h1>Organizer Login</h1>
    <form @submit="onSubmitOrganizer">
      <div class="form-group">
        <label for="usernameOrganizer">Username:</label>
        <input type="text" id="usernameOrganizer" v-model="usernameOrganizer"/>
        <label for="passwordOrganizer">Password:</label>
        <input type="password" id="passwordOrganizer" v-model="passwordOrganizer"/>
      </div>
      <button type="submit">Login</button>
    </form>
    <div class="column-space"></div>
    <h1>Admin Login</h1>
    <form @submit="onSubmitAdmin">
      <div class="form-group">
        <label for="usernameAdmin">Username:</label>
        <input type="text" id="usernameAdmin" v-model="usernameAdmin"/>
        <label for="passwordAdmin">Password:</label>
        <input type="password" id="passwordAdmin" v-model="passwordAdmin"/>
      </div>
      <button type="submit">Login</button>
    </form>
  </div>
</template>

<script>
import axios from "axios";
import {setRole, setUserID, setUserToken} from "./globalEvent.js";

export default {
  data() {
    return {
      badLogin: "",
      username: "",
      password: "",
      errorMessage: "",

      usernameOrganizer: "",
      passwordOrganizer: "",
      errorMessageOrganizer: "",

      usernameAdmin: "",
      passwordAdmin: "",
      errorMessageAdmin: "",
    };
  },
  methods: {
    async login(type) {
      let url = "";
      if (type === "attendee") {
        url = `http://localhost:8083/authenticate/login?username=${this.username}&password=${this.password}`;
      } else if (type === "organizer") {
        url = `http://localhost:8083/authenticate/login?username=${this.usernameOrganizer}&password=${this.passwordOrganizer}`;
      } else if (type === "administrator") {
        url = `http://localhost:8083/authenticate/login?username=${this.usernameAdmin}&password=${this.passwordAdmin}`;
      } else {
        return;
      }

      let result;

      try {
        result = await axios.get(url);
      } catch (err) {
        console.error(err);
        this.errorMessage = "Error logging in.";
        return;
      }

      if (result && result.data) {
        if (type === result.data.role) {
          console.log(result.data.userId + "_____________________")
          setUserID(result.data.userId);
          setUserToken(result.data.accessToken);
          setRole(result.data.role);
          //this.errorMessage = result.data.errorMessage;
          this.errorMessage = "passed"
        } else {
          this.badLogin = result.data.role + " used the wrong login form";
        }
      } else {
        this.errorMessage = "No result received.";
        return;
      }
    },

    async onSubmit(event) {
      event.preventDefault(); // Otherwise navigation starts before request has finished
      await this.login("attendee");

      if (this.errorMessage === "passed") {
        console.log("Logged in");
        this.$router.push({name: "DashBoard"});
      }
    },

    async onSubmitOrganizer(event) {
      event.preventDefault();
      await this.login("organizer");

      if (this.errorMessage === "passed") {
        console.log("Logged in");
        this.$router.push({name: "DashBoardOrganizer"});
      }
    },

    async onSubmitAdmin(event) {
      event.preventDefault();
      await this.login("administrator");

      if (this.errorMessage === "passed") {
        console.log("Logged in");
        this.$router.push({name: "DashBoardAdmin"});
      }
    },
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
</style>
