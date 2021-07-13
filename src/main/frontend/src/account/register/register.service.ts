import axios from "axios";

export default class RegisterService {

    public processRegistration(account: { username: string, email: string, password: string }): Promise<any> {

        return axios.post("api/register", account);
    }
}