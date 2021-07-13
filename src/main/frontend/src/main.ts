import {Vue} from "vue-property-decorator";
import {createApp} from "vue";
import App from "./App.vue";
// import "./registerServiceWorker";
import router from "./router";
import "./index.scss";
import {initVueApp, initVueXStore} from "@/shared/config/config";
import LoginService from "@/account/login/login.service";
import AccountService from "@/account/account.service";
import RegisterService from "@/account/register/register.service";
// import Vuelidate from "vuelidate";

initVueApp();

const store = initVueXStore();

const loginService = new LoginService(router);
const accountService = new AccountService(store, router);
const registerService = new RegisterService();

createApp(App)
    // .use(Vuelidate)
    .use(store)
    .use(router)
    .provide("loginService", loginService)
    .provide("accountService", accountService)
    .provide("registerService", registerService)
    .mount("#app");

