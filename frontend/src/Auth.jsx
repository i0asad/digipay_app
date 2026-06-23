import { useState, useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Eye, EyeOff } from 'lucide-react';
import api from './api/axiosInstance';
import './Auth.css';

const baseSchema = {
  username: z.string().min(1, 'Username is required'),
  password: z.string().min(1, 'Password is required')
};

const authSchema = z.discriminatedUnion('isLogin', [
  z.object({
    isLogin: z.literal(true),
    ...baseSchema,
    email: z.string().optional(),
    phoneNumber: z.string().optional(),
    panCard: z.string().optional(),
  }),
  z.object({
    isLogin: z.literal(false),
    ...baseSchema,
    password: z.string()
      .min(8, 'Password must be at least 8 characters')
      .regex(/[a-z]/, 'Password must contain at least one lowercase letter')
      .regex(/[A-Z]/, 'Password must contain at least one uppercase letter')
      .regex(/[0-9]/, 'Password must contain at least one number'),
    confirmPassword: z.string().min(1, 'Please confirm your password'),
    email: z.email({ message: 'Invalid email address' }),
    phoneNumber: z.string().regex(/^\+?[1-9]\d{1,14}$/, 'Invalid phone format'),
    panCard: z.string().regex(/^[A-Z]{5}[0-9]{4}[A-Z]{1}$/i, 'Invalid PAN format')
  }).refine((data) => data.password === data.confirmPassword, {
    message: "Passwords don't match",
    path: ["confirmPassword"],
  })
]);

export default function Auth({ onLogin }) {
  const location = useLocation();
  const initialMode = location.state?.isLogin ?? true;
  const [isLogin, setIsLogin] = useState(initialMode);
  const [serverError, setServerError] = useState('');
  const [serverSuccess, setServerSuccess] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);

  const {
    register,
    handleSubmit,
    setValue,
    reset,
    formState: { errors }
  } = useForm({
    resolver: zodResolver(authSchema),
    defaultValues: {
      isLogin: initialMode,
      username: '',
      password: '',
      confirmPassword: '',
      email: '',
      phoneNumber: '',
      panCard: ''
    }
  });

  const toggleMode = () => {
    const newMode = !isLogin;
    setIsLogin(newMode);
    setValue('isLogin', newMode);
    setServerError('');
    setServerSuccess('');
    reset({ isLogin: newMode, username: '', password: '', confirmPassword: '', email: '', phoneNumber: '', panCard: '' });
  };

  const onSubmit = async (data) => {
    setServerError('');
    setServerSuccess('');
    setIsLoading(true);

    const endpoint = isLogin ? '/auth/login' : '/auth/register';

    try {
      const payload = isLogin
        ? { username: data.username, password: data.password }
        : {
          username: data.username,
          password: data.password,
          email: data.email,
          phoneNumber: data.phoneNumber,
          panCard: data.panCard.toUpperCase()
        };

      const response = await api.post(endpoint, payload);

      if (isLogin) {
        onLogin(response.data.token);
      } else {
        const msg = response.data.message || 'Account successfully created! You can now log in.';
        toggleMode();
        setServerSuccess(msg);
      }
    } catch (err) {
      setServerError(err.response?.data?.message || 'Network error');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="card auth-card">
        <h2>{isLogin ? 'Welcome Back' : 'Create Account'}</h2>

        {!isLogin && (
          <div className="promo-banner">
            🎉 Special Offer: Create a free account now and get ₹100 bonus instantly!
          </div>
        )}

        {serverError && <p className="error-message">{serverError}</p>}
        {serverSuccess && <p className="success-message">{serverSuccess}</p>}

        <form onSubmit={handleSubmit(onSubmit)}>
          <div className="form-group">
            <label>Username</label>
            <input
              type="text"
              {...register('username')}
              placeholder="Enter your username"
            />
            {errors.username && <span className="field-error">{errors.username.message}</span>}
          </div>

          {!isLogin && (
            <>
              <div className="form-group">
                <label>Email</label>
                <input
                  type="email"
                  {...register('email')}
                  placeholder="Enter your email"
                />
                {errors.email && <span className="field-error">{errors.email.message}</span>}
              </div>
              <div className="form-group">
                <label>Phone Number</label>
                <input
                  type="tel"
                  {...register('phoneNumber')}
                  placeholder="e.g. +919876543210"
                />
                {errors.phoneNumber && <span className="field-error">{errors.phoneNumber.message}</span>}
              </div>
              <div className="form-group">
                <label>PAN Card</label>
                <input
                  type="text"
                  {...register('panCard')}
                  placeholder="e.g. ABCDE1234F"
                  onChange={(e) => setValue('panCard', e.target.value.toUpperCase())}
                />
                {errors.panCard && <span className="field-error">{errors.panCard.message}</span>}
              </div>
            </>
          )}

          <div className="form-group">
            <label>Password</label>
            <div className="password-input-wrapper">
              <input
                type={showPassword ? "text" : "password"}
                {...register('password')}
                placeholder="Enter your password"
              />
              <button 
                type="button" 
                className="password-toggle-btn" 
                onClick={() => setShowPassword(!showPassword)}
                aria-label={showPassword ? "Hide password" : "Show password"}
              >
                {showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
              </button>
            </div>
            {errors.password && <span className="field-error">{errors.password.message}</span>}
          </div>

          {!isLogin && (
            <>
              <div className="form-group">
                <label>Confirm Password</label>
                <div className="password-input-wrapper">
                  <input
                    type={showConfirmPassword ? "text" : "password"}
                    {...register('confirmPassword')}
                    placeholder="Confirm your password"
                  />
                  <button 
                    type="button" 
                    className="password-toggle-btn" 
                    onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                    aria-label={showConfirmPassword ? "Hide password" : "Show password"}
                  >
                    {showConfirmPassword ? <EyeOff size={20} /> : <Eye size={20} />}
                  </button>
                </div>
                {errors.confirmPassword && <span className="field-error">{errors.confirmPassword.message}</span>}
              </div>
            </>
          )}

          <button type="submit" className="button auth-submit-btn" disabled={isLoading}>
            {isLoading ? 'Processing...' : (isLogin ? 'Login' : 'Sign Up')}
          </button>
        </form>

        <div className="auth-toggle-container">
          <p className="auth-toggle-text">
            {isLogin ? "Don't have an account? " : "Already have an account? "}
            <span
              className="auth-toggle-link"
              onClick={toggleMode}
            >
              {isLogin ? 'Register here' : 'Login here'}
            </span>
          </p>
        </div>
      </div>
    </div>
  );
}
