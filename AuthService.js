import axios from 'axios';

const API_URL = 'http://localhost:8080/api/auth/';
const USER_API_URL = 'http://localhost:8080/api/users/';

class AuthService {
  // Login user and save token to localStorage
  login(username, password) {
    return axios
      .post(API_URL + 'signin', {
        username,
        password
      })
      .then(response => {
        if (response.data.accessToken) {
          localStorage.setItem('token', response.data.accessToken);
          localStorage.setItem('user', JSON.stringify({
            id: response.data.id,
            username: response.data.username,
            email: response.data.email,
            roles: response.data.roles
          }));
        }
        return response.data;
      });
  }

  // Logout user and remove token from localStorage
  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  }

  // Register a new user
  register(username, email, password) {
    return axios.post(API_URL + 'signup', {
      username,
      email,
      password,
      role: ["user"]
    });
  }

  // Get current user from localStorage
  getCurrentUser() {
    return JSON.parse(localStorage.getItem('user'));
  }

  // Check if user is logged in
  isLoggedIn() {
    return !!localStorage.getItem('token');
  }

  // Get JWT token
  getToken() {
    return localStorage.getItem('token');
  }

  // Check if user has specific role
  hasRole(role) {
    const user = this.getCurrentUser();
    if (!user) return false;
    return user.roles.includes(role);
  }

  // Set axios authorization header with JWT token
  setAuthHeader() {
    const token = this.getToken();
    if (token) {
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    } else {
      delete axios.defaults.headers.common['Authorization'];
    }
  }
  
  // Fetch current user profile from backend
  fetchUserProfile() {
    this.setAuthHeader();
    return axios.get(USER_API_URL + 'profile')
      .then(response => {
        // Update local storage with the latest user data
        localStorage.setItem('user', JSON.stringify(response.data));
        return response.data;
      });
  }
}

export default new AuthService(); 