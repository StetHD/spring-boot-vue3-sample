import {Router} from "vue-router";

export default class LoginService {

    constructor(private router: Router) {
    }

    public goToLogin(): void {

        this.router.push('/login').then(_ => {
        });
    }
}