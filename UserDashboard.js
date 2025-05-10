import React, { useState, useEffect } from 'react';
import AuthService from './AuthService';

const UserDashboard = () => {
  const [userProfile, setUserProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    // Fetch user profile when component mounts
    const fetchUserProfile = async () => {
      try {
        setLoading(true);
        const profile = await AuthService.fetchUserProfile();
        setUserProfile(profile);
        setError(null);
      } catch (err) {
        setError('Failed to load user profile. Please try again later.');
        console.error('Error fetching user profile:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchUserProfile();
  }, []);

  if (loading) {
    return <div>Loading user profile...</div>;
  }

  if (error) {
    return <div className="error">{error}</div>;
  }

  if (!userProfile) {
    return <div>No user profile found. Please log in again.</div>;
  }

  return (
    <div className="user-dashboard">
      <h2>Welcome, {userProfile.username}!</h2>
      
      <div className="profile-section">
        <h3>Your Profile</h3>
        <div className="profile-details">
          <p><strong>Username:</strong> {userProfile.username}</p>
          <p><strong>Email:</strong> {userProfile.email}</p>
          <p><strong>User ID:</strong> {userProfile.id}</p>
          <p><strong>Roles:</strong> {userProfile.roles.join(', ')}</p>
        </div>
      </div>
      
      {/* Add more dashboard sections here */}
      <div className="dashboard-actions">
        <button onClick={() => AuthService.logout()}>Logout</button>
      </div>
    </div>
  );
};

export default UserDashboard; 