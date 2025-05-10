// Handler for the signin endpoint
module.exports = (req, res) => {
  // We don't need to handle OPTIONS requests anymore as they're handled by Vercel routing
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