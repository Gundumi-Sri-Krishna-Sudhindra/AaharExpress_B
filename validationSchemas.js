import * as Yup from 'yup';

// Signup form validation schema
export const signupSchema = Yup.object().shape({
    username: Yup.string()
        .min(3, 'Username must be at least 3 characters')
        .max(20, 'Username cannot exceed 20 characters')
        .required('Username is required'),
    email: Yup.string()
        .email('Invalid email format')
        .max(50, 'Email cannot exceed 50 characters')
        .required('Email is required'),
    password: Yup.string()
        .min(6, 'Password must be at least 6 characters')
        .max(40, 'Password cannot exceed 40 characters')
        .required('Password is required'),
    confirmPassword: Yup.string()
        .oneOf([Yup.ref('password'), null], 'Passwords must match')
        .required('Confirm password is required')
});

// Login form validation schema
export const loginSchema = Yup.object().shape({
    username: Yup.string()
        .required('Username is required'),
    password: Yup.string()
        .required('Password is required')
});

// Forgot password form validation schema
export const forgotPasswordSchema = Yup.object().shape({
    email: Yup.string()
        .email('Invalid email format')
        .max(50, 'Email cannot exceed 50 characters')
        .required('Email is required')
}); 