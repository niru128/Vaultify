import React, { useState } from 'react'
import toast from 'react-hot-toast';
import axios from 'axios';
import api from '../services/api';
import { useNavigate } from 'react-router-dom';

const API = api;

export default function Login() {

    const [isLogin, setIsLogin] = useState(true);
    const [name, setName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");

    const navigate = useNavigate();

    const handleLogin = async () => {

        try {
            const url = isLogin ? '/auth/login' : '/auth/register';
            const payload = isLogin ? { email, password } : { name, email, password };

            const response = await API.post(url, payload);

            if (isLogin) {
                localStorage.setItem('token', response.data.token);
                toast.success("Login successful!");
                navigate('/dashboard');
            } else {
                toast.success("Registration successful! Please login.");
                setIsLogin(true);
            }

        } catch (error) {
            console.log("Login failed : Invalid credentials ", error);
            toast.error(isLogin ? "Login failed!" : "Registration failed!");
        }
    }
    return (
        <div className='h-screen flex items-center justify-center bg-linear-to-r from-white to-gray-500'>
            <div className='bg-white p-8 rounded-xl shadow-lg w-120'>

                <h1 className='text-4xl text-center mb-5 font-bold text-red-500' >Welcome to Vaultify </h1>


                <h2 className='text-xl font-bold text-center mb-6 text-gray-600'>
                    {isLogin ? 'Login to Your Account' : 'Create a New Account'}
                </h2>
                {!isLogin && (
                    <input
                        type="text"
                        placeholder="Name"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        className="w-full mb-4 p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-400"
                    />
                )}
                <input
                    type="email"
                    placeholder="Email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    className="w-full mb-4 p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-400"
                />
                <input
                    type="password"
                    placeholder="Password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    className='w-full mb-6 p-2 border border-gray-300 rounded-md'
                />
                <button
                    onClick={handleLogin}
                    className="w-full bg-blue-500 text-white py-2 rounded-lg hover:bg-blue-600 transition duration-200 cursor-pointer font-bold"
                >
                    {isLogin ? "Login" : "Register"}
                </button>

                <p className='text-sm text-center mt-4 cursor-pointer'>
                    {isLogin ? "Don't have an account?" : "Already have an account?"}
                    <span
                        className='text-blue-500 cursor-pointer ml-1'
                        onClick={() => setIsLogin(!isLogin)}
                    >
                        {isLogin ? "Register" : "Login"}
                    </span>
                </p>
            </div>
        </div>
    )
}
