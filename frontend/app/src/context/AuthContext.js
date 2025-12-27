import React, { createContext, useContext, useState, useEffect } from 'react';

const AuthContext = createContext();

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(() => {
    const savedUser = localStorage.getItem('martUser');
    return savedUser ? JSON.parse(savedUser) : null;
  });

  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    if (user) {
      localStorage.setItem('martUser', JSON.stringify(user));
    } else {
      localStorage.removeItem('martUser');
    }
  }, [user]);

  const login = async (email, password) => {
    setIsLoading(true);
    try {
      // Simulating API call - replace with actual backend call
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      const mockUser = {
        id: '1',
        email: email,
        name: 'John Doe',
        address: {
          street: '123 Main St',
          city: 'Dallas',
          state: 'TX',
          zipCode: '75001'
        }
      };
      
      setUser(mockUser);
      return { success: true, user: mockUser };
    } catch (error) {
      return { success: false, error: error.message };
    } finally {
      setIsLoading(false);
    }
  };

  const register = async (userData) => {
    setIsLoading(true);
    try {
      // Simulating API call - replace with actual backend call
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      const newUser = {
        id: Date.now().toString(),
        ...userData
      };
      
      setUser(newUser);
      return { success: true, user: newUser };
    } catch (error) {
      return { success: false, error: error.message };
    } finally {
      setIsLoading(false);
    }
  };

  const logout = () => {
    setUser(null);
    localStorage.removeItem('martUser');
  };

  const updateProfile = (updatedData) => {
    setUser(prev => ({ ...prev, ...updatedData }));
  };

  const value = {
    user,
    isLoading,
    login,
    register,
    logout,
    updateProfile,
    isAuthenticated: !!user,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};