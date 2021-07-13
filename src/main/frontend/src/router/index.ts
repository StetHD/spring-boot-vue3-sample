import { createRouter, createWebHistory, RouteRecordRaw } from "vue-router";
import Login from "../account/login/Login.vue";
import Register from "../account/register/Register.vue";

const routes: Array<RouteRecordRaw> = [
  {
    path: "/login",
    name: "Login",
    component: Login
  },
  {
    path: "/register",
    name: "Register",
    component: Register
  },
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
});

export default router;
