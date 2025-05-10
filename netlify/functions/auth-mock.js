// Mock authentication function for immediate testing
exports.handler = async function(event, context) {
  console.log("Mock auth function called with path:", event.path);
  console.log("HTTP Method:", event.httpMethod);

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
  console.log("Endpoint extracted:", path);
  
  // Handle POST request
  if (event.httpMethod === 'POST') {
    try {
      // Parse request body
      let requestBody = {};
      if (event.body) {
        try {
          requestBody = JSON.parse(event.body);
          console.log("Request body:", JSON.stringify(requestBody));
        } catch (e) {
          console.log("Could not parse request body as JSON:", event.body);
        }
      }
      
      // Generate mock response based on the endpoint
      let responseData;
      
      if (path === 'signin') {
        // Always provide a successful login response regardless of credentials
        responseData = {
          id: 1,
          username: requestBody.username || 'user123',
          email: requestBody.email || 'user@example.com',
          roles: ['ROLE_USER'],
          accessToken: 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMTIzIiwiaWF0IjoxNzE2MzIzNjQ4LCJleHAiOjE3MTY0MTAwNDh9.mockToken',
          tokenType: 'Bearer'
        };
        
        console.log("Sending successful signin response");
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
        
        console.log("Sending successful signup response");
      } else {
        responseData = {
          message: 'Unknown auth endpoint'
        };
        
        console.log("Unknown endpoint requested:", path);
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