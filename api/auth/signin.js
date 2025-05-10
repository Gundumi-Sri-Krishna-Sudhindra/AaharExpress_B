// Handler for the signin endpoint
module.exports = (req, res) => {
  // Set CORS headers explicitly for all response types
  res.setHeader('Access-Control-Allow-Origin', 'https://aahar-express-f.vercel.app');
  res.setHeader('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, OPTIONS');
  res.setHeader('Access-Control-Allow-Headers', 'X-Requested-With, Content-Type, Accept, Authorization');
  res.setHeader('Access-Control-Allow-Credentials', 'true');
  res.setHeader('Access-Control-Max-Age', '86400');

  // Handle OPTIONS request first - critical for CORS preflight
  if (req.method === 'OPTIONS') {
    return res.status(200).end();
  }

  // For non-OPTIONS requests (handle actual signin)
  if (req.method === 'POST') {
    try {
      // Successfully handled signin
      return res.status(200).json({
        message: 'Signin successful',
        token: 'mock-jwt-token',
        // Include other fields your frontend expects
        user: {
          id: 1,
          username: 'test_user',
          email: 'test@example.com',
          fullName: 'Test User',
          roles: ['ROLE_USER']
        }
      });
    } catch (error) {
      return res.status(500).json({ 
        message: 'Error processing request',
        error: error.toString() 
      });
    }
  }

  // Method not allowed
  return res.status(405).json({ message: 'Method not allowed' });
}; 