// Netlify function for the /api/auth/signin endpoint
const axios = require('axios');

exports.handler = async function(event, context) {
  // Set CORS headers for all responses
  const headers = {
    'Access-Control-Allow-Origin': 'https://aahar-express-f.vercel.app',
    'Access-Control-Allow-Methods': 'GET, POST, PUT, DELETE, OPTIONS',
    'Access-Control-Allow-Headers': 'X-Requested-With, Content-Type, Accept, Authorization',
    'Access-Control-Allow-Credentials': 'true',
    'Content-Type': 'application/json'
  };

  // Handle OPTIONS preflight request
  if (event.httpMethod === 'OPTIONS') {
    return {
      statusCode: 204,
      headers,
      body: ''
    };
  }

  // Handle POST request
  if (event.httpMethod === 'POST') {
    try {
      // Parse request body
      const requestBody = JSON.parse(event.body);
      
      // Replace with your actual Spring Boot API URL
      const springBootUrl = 'https://your-spring-boot-api.com/api/auth/signin';
      
      // Forward the request to your Spring Boot API
      const response = await axios.post(springBootUrl, requestBody, {
        headers: {
          'Content-Type': 'application/json'
        }
      });
      
      // Return the response from your Spring Boot API
      return {
        statusCode: response.status,
        headers,
        body: JSON.stringify(response.data)
      };
    } catch (error) {
      console.error('Error forwarding request:', error);
      
      // Return error response
      return {
        statusCode: error.response?.status || 500,
        headers,
        body: JSON.stringify({
          message: 'Error processing signin request',
          error: error.message
        })
      };
    }
  }
  
  // Method not allowed
  return {
    statusCode: 405,
    headers,
    body: JSON.stringify({ message: 'Method not allowed' })
  };
}; 