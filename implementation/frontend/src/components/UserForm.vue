<template>
  <h1>User Registration Form</h1>
  <div v-if="isAlertVisibleSucc" class="alertSucc">
    User registered successfully!
    <!-- <button @click="closeAlertSucc">Close</button> -->
  </div>
  <div v-if="isAlertVisibleFail" class="alertFail">
    User registered Failed!
    <!-- <button @click="closeAlertFail">Close</button> -->
  </div>
  <form @submit="onSubmit" id="user-registration-form">
    <div class="form-group">
      <label for="reg-username">User Name:</label>
      <input type="text" id="username-register" v-model="userJson.username" required/>

      <label for="reg-email">Email:</label>
      <input type="text" id="email-register" v-model="userJson.email" required/>

      <label for="reg-password">Password:</label>
      <input type="text" id="password-register" v-model="userJson.password" required/>

      <label for="reg-fname">Firstname:</label>
      <input type="text" id="forename-register" v-model="userJson.forename" required/>

      <label for="reg-lanme">Lastname:</label>
      <input type="text" id="surname-register" v-model="userJson.surname" required/>

      <label for="reg-ccode">CountryCode:</label>
      <input
          type="text"
          id="countryCode-register"
          v-model="userJson.countryCode"
          required
      />

      <label for="reg-gener">Gender:</label>
      <select id="reg-role" v-model="userJson.gender" required>
        <option value="m">m</option>
        <option value="f">f</option>
        <option value="x">x</option>
      </select>

      <label for="reg-hometown">Hometown:</label>
      <input type="text" id="hometown-register" v-model="userJson.hometown" required/>

      <label for="reg-role">Role:</label>
      <select id="reg-role" v-model="userJson.role" required>
        <option value="attendee">attendee</option>
        <option value="organizer">organizer</option>
        <option value="administrator">administrator</option>
      </select>
    </div>
    <button type="submit">Register</button>
  </form>
</template>

<script>
import axios from "axios";

export default {
  data() {
    return {
      isAlertVisibleSucc: false,
      isAlertVisibleFail: false,
      userJson: {
        username: "",
        email: "",
        password: "",
        forename: "",
        surname: "",
        countryCode: "",
        gender: "",
        hometown: "",
        role: "",
      },
    };
  },
  methods: {
    notificationFadeOutSucc() {
      this.isAlertVisibleSucc = true;
      setTimeout(() => {
        this.isAlertVisibleSucc = false;
        const form = document.getElementById("user-registration-form");
        form.submit();
      }, 5000);
    },
    notificationFadeOutFail() {
      this.isAlertVisibleFail = true;
      setTimeout(() => {
        this.isAlertVisibleFail = false;
      }, 3000);
    },

    async onSubmit(event) {
      event.preventDefault();
      await axios
          .post(`http://localhost:8083/management/users`, this.userJson)
          .then((response) => {
            //if reponse code
            //if (response.status === 201) {
            console.log("user is registered in the login service", response);
            this.notificationFadeOutSucc();
            location.reload();
            //}
          })
          .catch((error) => {
            console.log("user registration failed");
            this.notificationFadeOutFail();
            console.log(error);
          });
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

.form-group input, .form-group select {
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
