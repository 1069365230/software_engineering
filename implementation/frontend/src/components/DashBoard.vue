<template>
  <div class="container">
    <div class="component1">
      <LogoutVue />
      <EventBoardVue />
    </div>
    <div class="component-container">
      <div class="component2">
        <BookmarkSectionVue />
      </div>
      <div class="column-space"></div>
      <div class="component3">
        <AttendingSectionVue />
      </div>
    </div>
    <div class="component4">
      <RecommenderSectionVue />
    </div>
  </div>
</template>


<script>
import EventBoardVue from "./EventBoard.vue";
import BookmarkSectionVue from "./BookmarkSection.vue";
import AttendingSectionVue from "./AttendingSection.vue";
import RecommenderSectionVue from "./RecommenderSection.vue";
import LogoutVue from "./Logout.vue";

export default {
  name: "App",
  components: {
    LogoutVue,
    EventBoardVue,
    BookmarkSectionVue,
    AttendingSectionVue,
    RecommenderSectionVue,
  },
};
</script>

<style>
.container {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.component-container {
  display: flex;
}

.column-space {
  width: 300px;
}
</style>