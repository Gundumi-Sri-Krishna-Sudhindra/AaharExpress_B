// Helper function to create authorization header with JWT token
export default function authHeader() {
  const token = localStorage.getItem('token');
  
  if (token) {
    // For Spring Boot backend
    return { Authorization: `Bearer ${token}` };
  } else {
    return {};
  }
} 