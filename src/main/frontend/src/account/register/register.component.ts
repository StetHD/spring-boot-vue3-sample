import {Options, Vue} from "vue-class-component";
import {Inject} from "vue-property-decorator";
import {email, alphaNum, maxLength, minLength, required, sameAs} from "@vuelidate/validators";
import useVuelidate from "@vuelidate/core";
import LoginService from "@/account/login/login.service";
import RegisterService from "@/account/register/register.service";
import {EMAIL_ALREADY_USED_TYPE, LOGIN_ALREADY_USED_TYPE} from "@/constants";


@Options({
    validations: {
        registerAccount: {
            username: {
                required,
                minLength: minLength(1),
                maxLength: maxLength(50),
                alphaNum
            },
            email: {
                required,
                minLength: minLength(5),
                maxLength: maxLength(254),
                email,
            },
            password: {
                required,
                minLength: minLength(4),
                maxLength: maxLength(254),
            },
        },
        confirmPassword: {
            required,
            minLength: minLength(4),
            maxLength: maxLength(50),
            sameAsPassword: sameAs(function () {

                return this.registerAccount.password;
            }),
        },
    },
})
export default class Register extends Vue {

    validations = useVuelidate();

    @Inject("registerService")
    private registerService: RegisterService;

    @Inject("loginService")
    private loginService: LoginService;

    public registerAccount: { username: string, email: string, password: string } = {
        username: "",
        email: "",
        password: "",
    };

    public confirmPassword: string = "";
    public error: string;
    public errorEmailExists: string;
    public errorUserExists: string;
    public success: boolean;

    public usernameCheck() {

    }

    public register(): void {

        this.registerService.processRegistration(this.registerAccount)
            .then(() => {
                this.success = true;
            })
            .catch(error => {

                this.success = false;

                if (error.response.status === 400 && error.response.data.type === LOGIN_ALREADY_USED_TYPE) {

                    this.errorUserExists = 'ERROR';
                } else if (error.response.status === 400 && error.response.data.type === EMAIL_ALREADY_USED_TYPE) {

                    this.errorEmailExists = 'ERROR';
                } else {

                    this.error = 'ERROR';
                }
            });
    }

    public openLogin(): void {

        this.loginService.goToLogin();
    }
}
