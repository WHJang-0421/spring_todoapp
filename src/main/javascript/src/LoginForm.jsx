import { useForm } from "react-hook-form";
import axios from 'axios';
import qs from 'qs';
import { useNavigate } from "react-router-dom";

export default function LoginForm() {
    const { register, handleSubmit, formState: { errors } } = useForm({ mode: "onChange" });
    const navigate = useNavigate();

    function onSubmit(data) {
        axios.post('/api/login', qs.stringify({
            username: data.username,
            password: data.password
        }), {
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
            },
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
        <div className="LoginForm">
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
                <input type="submit" />
            </form>
        </div>
    );
}