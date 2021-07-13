import {Vue} from "vue-class-component";
import {Inject} from "vue-property-decorator";
import AccountService from "@/account/account.service";
import axios from "axios";

export default class LoginComponent extends Vue {

    @Inject("accountService")
    private accountService: AccountService;

    public authenticationError: boolean = false;
    public username: string = "";
    public password: string = "";
    public rememberMe: boolean = false;

    public doLogin(): void {

        const data = {username: this.username, password: this.password, rememberMe: this.rememberMe};

        axios
            .post("api/authenticate", data)
            .then(result => {

                const bearerToken = result.headers.authorization;

                if (bearerToken && bearerToken.slice(0, 7) === "Bearer ") {

                    const jwt = bearerToken.slice(7, bearerToken.length);

                    if (this.rememberMe) {

                        localStorage.setItem("authentication-token", jwt);
                        sessionStorage.removeItem("authentication-token");
                    } else {

                        sessionStorage.setItem("authentication-token", jwt);
                        localStorage.removeItem("authentication-token");
                    }
                }

                this.authenticationError = false;
                //go to first page
                // this.$root.$emit('bv::hide::modal', 'login-page');

                this.accountService.fetchAccount().then(_ => {
                });

            })
            .catch(() => {

                this.authenticationError = true;
            });
    }
}