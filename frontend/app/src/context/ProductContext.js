import React, { createContext, useContext, useState, useEffect } from 'react';

const ProductContext = createContext();

export const useProducts = () => {
  const context = useContext(ProductContext);
  if (!context) {
    throw new Error('useProducts must be used within a ProductProvider');
  }
  return context;
};

// Mock product data - replace with API calls later
const mockProducts = [
  { id: 1, title: 'Smart TV 55"', price: 398, originalPrice: 598, image: '📺', rating: 4.5, reviews: 2341, category: 'Electronics', description: 'High-quality 4K Smart TV with HDR support' },
  { id: 2, title: 'Wireless Headphones', price: 49, originalPrice: 99, image: '🎧', rating: 4.3, reviews: 1523, category: 'Electronics', description: 'Noise-cancelling wireless headphones' },
  { id: 3, title: 'Coffee Maker', price: 29, originalPrice: 59, image: '☕', rating: 4.7, reviews: 892, category: 'Home', description: '12-cup programmable coffee maker' },
  { id: 4, title: 'Air Fryer', price: 79, originalPrice: 129, image: '🍳', rating: 4.6, reviews: 3421, category: 'Home', description: 'Digital air fryer with 8 preset programs' },
  { id: 5, title: 'Vacuum Cleaner', price: 149, originalPrice: 249, image: '🧹', rating: 4.4, reviews: 1876, category: 'Home', description: 'Cordless stick vacuum with powerful suction' },
  { id: 6, title: 'Blender', price: 39, originalPrice: 69, image: '🥤', rating: 4.5, reviews: 1234, category: 'Home', description: 'High-speed blender for smoothies and shakes' },
  { id: 7, title: 'Gaming Console', price: 299, originalPrice: 499, image: '🎮', rating: 4.8, reviews: 5432, category: 'Electronics', description: 'Next-gen gaming console with 1TB storage' },
  { id: 8, title: 'Laptop 15.6"', price: 599, originalPrice: 899, image: '💻', rating: 4.6, reviews: 3210, category: 'Electronics', description: 'Powerful laptop with Intel i5 and 8GB RAM' },
  { id: 9, title: 'Smart Watch', price: 199, originalPrice: 349, image: '⌚', rating: 4.4, reviews: 2109, category: 'Electronics', description: 'Fitness tracking smartwatch with GPS' },
  { id: 10, title: 'Tablet 10"', price: 249, originalPrice: 399, image: '📱', rating: 4.5, reviews: 1876, category: 'Electronics', description: '10-inch tablet with HD display' },
  { id: 11, title: 'Microwave Oven', price: 89, originalPrice: 149, image: '🍱', rating: 4.3, reviews: 1543, category: 'Home', description: '1000W microwave with smart sensor' },
  { id: 12, title: 'Yoga Mat', price: 19, originalPrice: 39, image: '🧘', rating: 4.6, reviews: 987, category: 'Sports', description: 'Non-slip exercise yoga mat' },
];

export const ProductProvider = ({ children }) => {
  const [products, setProducts] = useState(mockProducts);
  const [filteredProducts, setFilteredProducts] = useState(mockProducts);
  const [categories, setCategories] = useState(['All', 'Electronics', 'Home', 'Sports']);
  const [selectedCategory, setSelectedCategory] = useState('All');
  const [searchQuery, setSearchQuery] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    filterProducts();
  }, [selectedCategory, searchQuery]);

  const filterProducts = () => {
    let filtered = products;

    if (selectedCategory !== 'All') {
      filtered = filtered.filter(product => product.category === selectedCategory);
    }

    if (searchQuery.trim()) {
      filtered = filtered.filter(product =>
        product.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
        product.description.toLowerCase().includes(searchQuery.toLowerCase())
      );
    }

    setFilteredProducts(filtered);
  };

  const getProductById = (id) => {
    return products.find(product => product.id === parseInt(id));
  };

  const getProductsByCategory = (category) => {
    if (category === 'All') return products;
    return products.filter(product => product.category === category);
  };

  const searchProducts = (query) => {
    setSearchQuery(query);
  };

  const setCategory = (category) => {
    setSelectedCategory(category);
  };

  const clearSearch = () => {
    setSearchQuery('');
  };

  const getFlashDeals = () => {
    return products.slice(6, 10).map(product => ({
      ...product,
      timeLeft: '2h 15m',
      isFlashDeal: true
    }));
  };

  const getTopDeals = () => {
    return products.slice(0, 6);
  };

  const value = {
    products,
    filteredProducts,
    categories,
    selectedCategory,
    searchQuery,
    isLoading,
    getProductById,
    getProductsByCategory,
    searchProducts,
    setCategory,
    clearSearch,
    getFlashDeals,
    getTopDeals,
  };

  return <ProductContext.Provider value={value}>{children}</ProductContext.Provider>;
};