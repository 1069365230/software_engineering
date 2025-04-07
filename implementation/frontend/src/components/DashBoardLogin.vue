<template>
  <div class="container">
    <div class="component-user-login">
      <LoginForm />
    </div>
    <div class="component-user-reg">
      <UserForm />
    </div>
  </div>
</template>


<script>
import LoginForm from "./LoginForm.vue";
import UserForm from "./UserForm.vue";

export default {
  name: "App",
  components: {
    LoginForm,
    UserForm,
  },
};
</script>

<style>
.container {
  display: flex;
  flex-direction: column;
  align-items: center;
}

</style>