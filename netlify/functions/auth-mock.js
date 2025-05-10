// Mock authentication function for immediate testing
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

  // Extract path from event
  const path = event.path.split('/').pop();
  
  // Handle POST request
  if (event.httpMethod === 'POST') {
    try {
      // Parse request body
      const requestBody = JSON.parse(event.body);
      
      // Generate mock response based on the endpoint
      let responseData;
      
      if (path === 'signin') {
        responseData = {
          id: 1,
          username: requestBody.username || 'user123',
          email: requestBody.email || 'user@example.com',
          roles: ['ROLE_USER'],
          accessToken: 'mock-jwt-token-12345',
          tokenType: 'Bearer'
        };
      } else if (path === 'signup') {
        responseData = {
          message: 'User registered successfully!',
          user: {
            id: 2,
            username: requestBody.username || 'newuser',
            email: requestBody.email || 'newuser@example.com',
            roles: ['ROLE_USER']
          }
        };
      } else {
        responseData = {
          message: 'Unknown auth endpoint'
        };
      }
      
      // Return mock response
      return {
        statusCode: 200,
        headers,
        body: JSON.stringify(responseData)
      };
    } catch (error) {
      console.error('Error processing request:', error);
      
      // Return error response
      return {
        statusCode: 500,
        headers,
        body: JSON.stringify({
          message: 'Error processing request',
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