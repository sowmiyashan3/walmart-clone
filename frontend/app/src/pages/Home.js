import React from 'react';
import Header from '../components/Header';
import ProductCard from '../components/ProductCard';
import Footer from '../components/Footer';
import { useProducts } from '../context/ProductContext';
import { useNavigate } from 'react-router-dom';

const Home = () => {
  const { getFlashDeals, getTopDeals } = useProducts();
  const navigate = useNavigate();

  const flashDeals = getFlashDeals();
  const topDeals = getTopDeals();

  const shopByCategory = [
    { name: 'Electronics', icon: '💻', color: 'bg-blue-100' },
    { name: 'Clothing', icon: '👕', color: 'bg-pink-100' },
    { name: 'Home', icon: '🏠', color: 'bg-green-100' },
    { name: 'Sports', icon: '⚽', color: 'bg-orange-100' },
    { name: 'Toys', icon: '🧸', color: 'bg-purple-100' },
    { name: 'Beauty', icon: '💄', color: 'bg-red-100' },
    { name: 'Garden', icon: '🌱', color: 'bg-lime-100' },
    { name: 'Books', icon: '📚', color: 'bg-indigo-100' },
  ];

  const handleCategoryClick = (categoryName) => {
    navigate('/products');
  };

  const handleViewAllDeals = () => {
    navigate('/products');
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <Header />

      {/* Hero Banner */}
      <div className="bg-gradient-to-r from-blue-500 to-blue-700 text-white py-12">
        <div className="max-w-7xl mx-auto px-4">
          <div className="flex items-center justify-between">
            <div>
              <h2 className="text-4xl font-bold mb-2">Holiday Savings Are Here! 🎄</h2>
              <p className="text-xl mb-4">Save big on everything you need this season</p>
              <button 
                onClick={() => navigate('/products')}
                className="bg-yellow-400 text-blue-800 px-8 py-3 rounded-full font-bold hover:bg-yellow-500 transition"
              >
                Shop Now
              </button>
            </div>
            <div className="text-8xl">🎁</div>
          </div>
        </div>
      </div>

      {/* Flash Deals Banner */}
      <div className="bg-red-600 text-white py-3">
        <div className="max-w-7xl mx-auto px-4">
          <div className="flex items-center justify-between">
            <h3 className="text-xl font-bold">⚡ Flash Deals - Ending Soon!</h3>
            <span className="text-lg font-semibold">Time Left: 2h 15m</span>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 py-6">
        {/* Flash Deals Section */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
          {flashDeals.map(deal => (
            <ProductCard 
              key={deal.id} 
              product={deal} 
              isFlashDeal={true}
            />
          ))}
        </div>

        {/* Shop by Category */}
        <div className="mb-8">
          <h3 className="text-2xl font-bold mb-4 text-gray-800">Shop by Category</h3>
          <div className="grid grid-cols-4 md:grid-cols-8 gap-4">
            {shopByCategory.map((cat, idx) => (
              <button 
                key={idx} 
                onClick={() => handleCategoryClick(cat.name)}
                className={`${cat.color} rounded-lg p-4 hover:scale-105 transition flex flex-col items-center gap-2`}
              >
                <span className="text-4xl">{cat.icon}</span>
                <span className="text-xs font-semibold text-gray-700">{cat.name}</span>
              </button>
            ))}
          </div>
        </div>

        {/* Top Deals Section */}
        <div>
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-2xl font-bold text-gray-800">Top Deals</h3>
            <button 
              onClick={handleViewAllDeals}
              className="text-blue-600 hover:underline font-semibold"
            >
              View All
            </button>
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-6 gap-4">
            {topDeals.map(deal => (
              <ProductCard 
                key={deal.id} 
                product={deal}
              />
            ))}
          </div>
        </div>

        {/* Walmart+ Banner */}
        <div className="mt-8 bg-gradient-to-r from-yellow-400 to-yellow-500 rounded-lg p-8 flex items-center justify-between">
          <div>
            <h3 className="text-3xl font-bold text-blue-900 mb-2">Try Walmart+ free for 30 days</h3>
            <p className="text-blue-800 mb-4">Free delivery, member prices, and more!</p>
            <button className="bg-blue-600 text-white px-6 py-3 rounded-full font-bold hover:bg-blue-700">
              Start Free Trial
            </button>
          </div>
          <div className="text-8xl">⭐</div>
        </div>
      </div>

      <Footer />
    </div>
  );
};

export default Home;