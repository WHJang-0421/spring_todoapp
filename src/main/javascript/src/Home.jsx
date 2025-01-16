import { Link, useNavigate } from "react-router-dom";
import { useEffect } from 'react';
import axios from 'axios';

export default function Home() {
    const navigate = useNavigate();
    useEffect(() => {
        axios.get('/api/user', { withCredentials: true })
            .then((response) => {
                console.log(response);
                navigate('/tasks');
            })
            .catch((error) => {
                console.log(error);

            });
    });
    return (
        <div className="Home">
            <Link to='/login'>goto login</Link>,
            <Link to='/register'>goto register</Link>
            <a href='/oauth2/authorization/google'>Login with Google</a>
        </div>
    );
}