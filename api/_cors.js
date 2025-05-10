// Simple handler for CORS preflight requests
module.exports = (req, res) => {
  // This file exists to support CORS preflight requests
  // Most CORS handling is done via vercel.json
  res.status(204).end();
}; 