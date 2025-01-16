import { useForm } from "react-hook-form";
import { useNavigate } from "react-router-dom";
import axios from 'axios';

export default function LoginForm() {
    const navigate = useNavigate();

    const { register, handleSubmit, formState: { errors }, getValues } = useForm({ mode: "onChange" });

    function onSubmit(data) {
        axios.post('/api/register', {
            username: data.username,
            password: data.password
        }, {
            withCredentials: true
        })
            .then((response) => {
                console.log(response);
                navigate('/');
            })
            .catch((error) => {
                console.log(error);
            })
    }

    return (
        <div className="RegisterForm">
            <form onSubmit={handleSubmit(onSubmit)}>
                <label htmlFor="username">username:</label>
                <input id="username" type="text" placeholder="user"
                    {...register("username", {
                        required: "username required"
                    })}
                />
                {errors.username && <small role="alert">{errors.username.message}</small>}
                <label htmlFor="password">password:</label>
                <input id="password" type="password" placeholder="******"
                    {...register("password", {
                        required: "password required",
                        minLength: {
                            value: 7,
                            message: "Password must be longer than 7 characters"
                        }
                    })}
                />
                {errors.password && <small role="alert">{errors.password.message}</small>}
                <label htmlFor="passwordConfirm">password confirm:</label>
                <input id="passwordConfirm" type="password" placeholder="******"
                    {...register("passwordConfirm", {
                        required: "please enter password again",
                        validate: (val) => {
                            if (getValues("password") !== val) {
                                return "password doesn't match";
                            }
                        }
                    })}
                />
                {errors.passwordConfirm && <small role="alert">{errors.passwordConfirm.message}</small>}
                <input type="submit" />
            </form>
        </div>
    );
}