import React from 'react';
import Header from '../components/Header';
import ProductCard from '../components/ProductCard';
import Footer from '../components/Footer';
import { useProducts } from '../context/ProductContext';
import { ChevronDown } from 'lucide-react';

const ProductList = () => {
  const { filteredProducts, categories, selectedCategory, setCategory, searchQuery, clearSearch } = useProducts();

    const handleCategoryChange = (category) => {
    setCategory(category);
    // Clear search query when changing category
    if (clearSearch) {
      clearSearch();
    }
  };

  const handleClearFilters = () => {
    setCategory('All');
    if (clearSearch) {
      clearSearch();
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <Header />

      <div className="max-w-7xl mx-auto px-4 py-6">
        {/* Header */}
        <div className="mb-6">
          {searchQuery && (
            <h1 className="text-2xl font-bold text-gray-800 mb-2">
              Search results for "{searchQuery}"
            </h1>
          )}
          <p className="text-gray-600">
            {filteredProducts.length} {filteredProducts.length === 1 ? 'result' : 'results'} found
          </p>
        </div>

        <div className="flex gap-6">
          {/* Sidebar Filter */}
          <div className="w-64 flex-shrink-0">
            <div className="bg-white rounded-lg shadow p-4 sticky top-24">
              <h3 className="font-bold text-lg mb-4">Categories</h3>
              <div className="space-y-2">
                {categories.map((category) => (
                  <button
                    key={category}
                    onClick={() => handleCategoryChange(category)}
                    className={`w-full text-left px-3 py-2 rounded transition ${
                      selectedCategory === category
                        ? 'bg-blue-600 text-white'
                        : 'hover:bg-gray-100 text-gray-700'
                    }`}
                  >
                    {category}
                  </button>
                ))}
              </div>

              <div className="mt-6 pt-6 border-t">
                <h3 className="font-bold text-lg mb-4">Price Range</h3>
                <div className="space-y-2">
                  <label className="flex items-center">
                    <input type="checkbox" className="mr-2" />
                    <span className="text-sm">Under $25</span>
                  </label>
                  <label className="flex items-center">
                    <input type="checkbox" className="mr-2" />
                    <span className="text-sm">$25 - $50</span>
                  </label>
                  <label className="flex items-center">
                    <input type="checkbox" className="mr-2" />
                    <span className="text-sm">$50 - $100</span>
                  </label>
                  <label className="flex items-center">
                    <input type="checkbox" className="mr-2" />
                    <span className="text-sm">$100 - $200</span>
                  </label>
                  <label className="flex items-center">
                    <input type="checkbox" className="mr-2" />
                    <span className="text-sm">Over $200</span>
                  </label>
                </div>
              </div>

              <div className="mt-6 pt-6 border-t">
                <h3 className="font-bold text-lg mb-4">Rating</h3>
                <div className="space-y-2">
                  <label className="flex items-center">
                    <input type="checkbox" className="mr-2" />
                    <span className="text-sm">⭐⭐⭐⭐ & Up</span>
                  </label>
                  <label className="flex items-center">
                    <input type="checkbox" className="mr-2" />
                    <span className="text-sm">⭐⭐⭐ & Up</span>
                  </label>
                </div>
              </div>
            </div>
          </div>

          {/* Product Grid */}
          <div className="flex-1">
            {/* Sort Bar */}
            <div className="bg-white rounded-lg shadow p-4 mb-6 flex items-center justify-between">
              <span className="text-gray-700 font-medium">Sort by:</span>
              <div className="flex gap-4">
                <button className="flex items-center gap-1 text-blue-600 hover:underline">
                  Best Match <ChevronDown size={16} />
                </button>
                <button className="text-gray-700 hover:text-blue-600">
                  Price: Low to High
                </button>
                <button className="text-gray-700 hover:text-blue-600">
                  Price: High to Low
                </button>
                <button className="text-gray-700 hover:text-blue-600">
                  Customer Rating
                </button>
              </div>
            </div>

            {/* Products */}
            {filteredProducts.length > 0 ? (
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
                {filteredProducts.map((product) => (
                  <ProductCard key={product.id} product={product} />
                ))}
              </div>
            ) : (
              <div className="bg-white rounded-lg shadow p-12 text-center">
                <div className="text-6xl mb-4">🔍</div>
                <h3 className="text-xl font-bold text-gray-800 mb-2">
                  No products found
                </h3>
                <p className="text-gray-600 mb-4">
                  Try adjusting your search or filters
                </p>
                <button
                  onClick={handleClearFilters}
                  className="bg-blue-600 text-white px-6 py-2 rounded hover:bg-blue-700"
                >
                  Clear Filters
                </button>
              </div>
            )}
          </div>
        </div>
      </div>

      <Footer />
    </div>
  );
};

export default ProductList;