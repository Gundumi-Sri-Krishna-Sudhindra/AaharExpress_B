// CORS preflight handler for Netlify
exports.handler = async function(event, context) {
  console.log("CORS preflight handler called for path:", event.path);
  console.log("HTTP Method:", event.httpMethod);

  // Always set full CORS headers for all responses
  const headers = {
    'Access-Control-Allow-Origin': 'https://aahar-express-f.vercel.app',
    'Access-Control-Allow-Methods': 'GET, POST, PUT, DELETE, OPTIONS',
    'Access-Control-Allow-Headers': 'X-Requested-With, Content-Type, Accept, Authorization, Origin, Access-Control-Request-Method, Access-Control-Request-Headers',
    'Access-Control-Allow-Credentials': 'true',
    'Access-Control-Max-Age': '86400', // 24 hours
    'Content-Type': 'text/plain'
  };

  // Handle both preflight and actual requests
  if (event.httpMethod === 'OPTIONS') {
    console.log("Handling OPTIONS preflight request");
    return {
      statusCode: 204, // No Content for preflight
      headers,
      body: ''
    };
  } else {
    console.log("Non-OPTIONS request to CORS handler, responding with CORS headers only");
    return {
      statusCode: 200,
      headers,
      body: JSON.stringify({ message: 'CORS enabled' })
    };
  }
}; 