import {Store} from "vuex";
import {Router} from "vue-router";
import axios from "axios";

export default class AccountService {

    constructor(private store: Store<any>, private router: Router) {

        this.init();
    }

    public init(): void {
        this.fetchProfiles();
    }

    public fetchProfiles(): Promise<boolean> {

        return new Promise(resolve => {
            axios
                .get("management/info")
                .then(response => {

                    if (response.data && response.data.activeProfiles) {
                        this.store.commit('setActiveProfiles', response.data['activeProfiles']);
                    }

                    resolve(true);
                })
                .catch(() => resolve(false));
        })
    }

    public fetchAccount(): Promise<boolean> {

        return new Promise(resolve => {

            axios
                .get("api/account")
                .then(response => {

                    this.store.commit("authenticate");
                    const account = response.data;

                    if (account) {

                        this.store.commit("authenticate", account);
                        const requestedUrl = sessionStorage.getItem("requested-url");

                        if (requestedUrl) {

                            this.router.replace(requestedUrl)
                                .then(_ => console.log(`Redirected to ${requestedUrl} after login!`));

                            sessionStorage.removeItem("requested-url");
                        }
                    } else {

                        this.store.commit("logout");

                        this.router.push('/')
                            .then(_ => console.log("Failed to login!"));

                        sessionStorage.removeItem("requested-url");
                    }

                    resolve(true);
                })
                .catch(() => {

                    this.store.commit("logout");
                    resolve(false);
                });
        });
    }

    public checkAuthenticationAndAuthorities(_authorities: string | string[]): Promise<boolean> {

        let authorities: string[];

        if (typeof _authorities === "string") {

            authorities = [_authorities];
        } else {

            authorities = _authorities;
        }

        if (!this.authenticated || !this.authorities) {

            const token = localStorage.getItem('authentication-token') || sessionStorage.getItem('authentication-token');

            if (this.store.getters.account && this.store.getters.logon && token) {

                return this.fetchAccount();
            } else {

                return new Promise(resolve => {

                    resolve(false);
                });
            }
        }

        authorities.forEach(authority => {

            if (this.authorities.includes(authority)) {

                return new Promise(resolve => {

                    resolve(true);
                });
            }
        });

        return new Promise(resolve => {

            resolve(false);
        })
    }

    public get authenticated(): boolean {

        return this.store.getters.authenticated;
    }

    public get authorities(): string[] {

        return this.store.getters.authorities;
    }
}