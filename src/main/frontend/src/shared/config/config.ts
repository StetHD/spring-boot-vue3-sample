import { setupAxiosInterceptors } from "@/shared/config/axios-interceptor";
import Vuex from "vuex";
import { accountStore } from "@/shared/config/store/account-store";

export function initVueApp() {

    setupAxiosInterceptors(() => console.log("Unauthorized!"));
}


export function initVueXStore() {

    return new Vuex.Store({
        modules: {
            accountStore,
        },
    });
}
