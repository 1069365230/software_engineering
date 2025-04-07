let globalEvents = [];
let UserToken = "";
let UserID = "";
let apiBaseUrl = "http://localhost:8081/";
let Role = "";

export function setRole(role) {
    Role = role;
}

export function getRole() {
    return Role;
}

export function setUserID(Id) {
    UserID = Id;
}

export function getUserID() {
    return UserID;
}

export function setUserToken(token) {
    UserToken = token;
}

export function getUserToken() {
    return UserToken;
}

export function setGlobalEvents(events) {
    globalEvents = events;
}

export function getGlobalEvents() {
    return globalEvents;
}

export function getApiBaseURL() {
    return apiBaseUrl;
}