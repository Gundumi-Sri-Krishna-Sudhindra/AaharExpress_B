// CORS preflight handler for Netlify
exports.handler = async function(event, context) {
  // Return CORS headers for preflight OPTIONS requests
  return {
    statusCode: 204, // No Content
    headers: {
      'Access-Control-Allow-Origin': 'https://aahar-express-f.vercel.app',
      'Access-Control-Allow-Methods': 'GET, POST, PUT, DELETE, OPTIONS',
      'Access-Control-Allow-Headers': 'X-Requested-With, Content-Type, Accept, Authorization',
      'Access-Control-Allow-Credentials': 'true',
      'Access-Control-Max-Age': '86400', // 24 hours
      'Content-Type': 'text/plain'
    },
    body: ''
  };
}; 