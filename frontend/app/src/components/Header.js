import React, { useState } from 'react';
import { Search, ShoppingCart, MapPin, User, Menu, ChevronDown } from 'lucide-react';
import { useCart } from '../context/CartContext';
import { useAuth } from '../context/AuthContext';
import { useProducts } from '../context/ProductContext';
import { useNavigate } from 'react-router-dom';

const Header = () => {
  const [searchInput, setSearchInput] = useState('');
  const { getCartCount } = useCart();
  const { user } = useAuth();
  const { searchProducts } = useProducts();
  const navigate = useNavigate();

  const categories = [
    'Departments', 'Grocery & Essentials', 'Toy Shop', 'Home', 'Fashion',
    'Electronics', 'Registry', 'ONE Debit', 'mart+'
  ];

  const handleSearch = (e) => {
    e.preventDefault();
    if (searchInput.trim()) {
      searchProducts(searchInput);
      navigate('/products');
    }
  };

  const handleCartClick = () => {
    navigate('/cart');
  };

  const handleSignInClick = () => {
    if (user) {
      navigate('/profile');
    } else {
      navigate('/login');
    }
  };

  const handleLogoClick = () => {
    navigate('/');
  };

  return (
    <header className="bg-blue-600 text-white sticky top-0 z-50 shadow-md">
      {/* Top Bar */}
      <div className="bg-blue-700 py-1">
        <div className="max-w-7xl mx-auto px-4 flex justify-between items-center text-xs">
          <div className="flex items-center gap-4">
            <button className="flex items-center gap-1 hover:underline">
              <MapPin size={14} />
              Dallas, 75001
            </button>
          </div>
          <div className="flex items-center gap-4">
            <button className="hover:underline" onClick={() => navigate('/orders')}>
              Reorder
            </button>
            <button className="hover:underline">Registry</button>
            <button className="hover:underline">mart+</button>
          </div>
        </div>
      </div>

      {/* Main Header */}
      <div className="max-w-7xl mx-auto px-4 py-3">
        <div className="flex items-center justify-between gap-4">
          {/* Logo */}
          <div className="flex items-center gap-2 flex-shrink-0">
            <Menu className="cursor-pointer" size={24} />
            <h1 
              className="text-2xl font-bold cursor-pointer whitespace-nowrap" 
              onClick={handleLogoClick}
            >
              mart
            </h1>
          </div>

          {/* Search Bar */}
          <div className="flex-1 max-w-2xl mx-4">
            <div className="relative">
              <input
                type="text"
                placeholder="Search everything at mart online and in store"
                value={searchInput}
                onChange={(e) => setSearchInput(e.target.value)}
                onKeyPress={(e) => e.key === 'Enter' && handleSearch(e)}
                className="w-full py-2.5 pl-4 pr-12 rounded-full text-gray-800 focus:outline-none focus:ring-2 focus:ring-yellow-400"
              />
              <button 
                onClick={handleSearch}
                className="absolute right-1 top-1/2 -translate-y-1/2 bg-yellow-400 text-gray-800 p-2 rounded-full hover:bg-yellow-500"
              >
                <Search size={20} />
              </button>
            </div>
          </div>

          {/* Right Side Icons */}
          <div className="flex items-center gap-4 flex-shrink-0">
            <button 
              className="flex flex-col items-center justify-center hover:opacity-80 min-w-[60px]"
              onClick={handleSignInClick}
            >
              <User size={24} />
              <span className="text-xs mt-1 whitespace-nowrap">
                {user ? user.name.split(' ')[0] : 'Sign In'}
              </span>
            </button>
            <button 
              className="flex flex-col items-center justify-center hover:opacity-80 relative min-w-[60px]"
              onClick={handleCartClick}
            >
              <ShoppingCart size={24} />
              {getCartCount() > 0 && (
                <span className="absolute top-0 right-2 bg-yellow-400 text-blue-800 text-xs font-bold rounded-full w-5 h-5 flex items-center justify-center">
                  {getCartCount()}
                </span>
              )}
              <span className="text-xs mt-1">Cart</span>
            </button>
          </div>
        </div>
      </div>

      {/* Navigation */}
      <nav className="bg-blue-600 border-t border-blue-500">
        <div className="max-w-7xl mx-auto px-4">
          <div className="flex items-center gap-8 py-2.5 text-sm overflow-x-auto">
            {categories.map((cat, idx) => (
              <button 
                key={idx} 
                className="whitespace-nowrap hover:underline flex items-center gap-1 flex-shrink-0"
                onClick={() => {
                  if (idx === 0) {
                    navigate('/products');
                  }
                }}
              >
                {cat}
                {idx === 0 && <ChevronDown size={16} />}
              </button>
            ))}
          </div>
        </div>
      </nav>
    </header>
  );
};

export default Header;