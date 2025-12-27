import React, { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Header from '../components/Header';
import Footer from '../components/Footer';
import { useProducts } from '../context/ProductContext';
import { useCart } from '../context/CartContext';
import { Star, Truck, Shield, ArrowLeft } from 'lucide-react';

const ProductDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { getProductById } = useProducts();
  const { addToCart } = useCart();
  const [quantity, setQuantity] = useState(1);
  
  const product = getProductById(id);

  if (!product) {
    return (
      <div className="min-h-screen bg-gray-50">
        <Header />
        <div className="max-w-7xl mx-auto px-4 py-12 text-center">
          <h2 className="text-2xl font-bold text-gray-800 mb-4">Product not found</h2>
          <button
            onClick={() => navigate('/')}
            className="bg-blue-600 text-white px-6 py-2 rounded hover:bg-blue-700"
          >
            Back to Home
          </button>
        </div>
        <Footer />
      </div>
    );
  }

  const handleAddToCart = () => {
    for (let i = 0; i < quantity; i++) {
      addToCart(product);
    }
    navigate('/cart');
  };

  const handleBuyNow = () => {
    handleAddToCart();
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <Header />

      <div className="max-w-7xl mx-auto px-4 py-6">
        {/* Breadcrumb */}
        <button
          onClick={() => navigate(-1)}
          className="flex items-center gap-2 text-blue-600 hover:underline mb-6"
        >
          <ArrowLeft size={20} />
          Back to products
        </button>

        <div className="bg-white rounded-lg shadow-lg overflow-hidden">
          <div className="grid md:grid-cols-2 gap-8 p-8">
            {/* Product Image */}
            <div className="flex items-center justify-center bg-gray-50 rounded-lg p-12">
              <div className="text-9xl">{product.image}</div>
            </div>

            {/* Product Info */}
            <div>
              <h1 className="text-3xl font-bold text-gray-800 mb-4">
                {product.title}
              </h1>

              {/* Rating */}
              <div className="flex items-center gap-2 mb-4">
                <div className="flex items-center gap-1">
                  {[...Array(5)].map((_, i) => (
                    <Star
                      key={i}
                      size={20}
                      fill={i < Math.floor(product.rating) ? '#FFA500' : 'none'}
                      color="#FFA500"
                    />
                  ))}
                </div>
                <span className="font-semibold">{product.rating}</span>
                <span className="text-gray-500">({product.reviews} reviews)</span>
              </div>

              {/* Price */}
              <div className="mb-6">
                <div className="flex items-baseline gap-3 mb-2">
                  <span className="text-4xl font-bold text-blue-600">
                    ${product.price}
                  </span>
                  {product.originalPrice && (
                    <>
                      <span className="text-xl text-gray-500 line-through">
                        ${product.originalPrice}
                      </span>
                      <span className="bg-red-100 text-red-600 px-2 py-1 rounded text-sm font-bold">
                        Save ${product.originalPrice - product.price}
                      </span>
                    </>
                  )}
                </div>
                <p className="text-sm text-gray-600">Price when purchased online</p>
              </div>

              {/* Description */}
              <div className="mb-6 pb-6 border-b">
                <h3 className="font-bold text-lg mb-2">About this item</h3>
                <p className="text-gray-700">{product.description}</p>
              </div>

              {/* Features */}
              <div className="mb-6 space-y-3">
                <div className="flex items-center gap-3">
                  <Truck className="text-green-600" size={24} />
                  <div>
                    <p className="font-semibold">Free shipping</p>
                    <p className="text-sm text-gray-600">Arrives by Dec 28</p>
                  </div>
                </div>
                <div className="flex items-center gap-3">
                  <Shield className="text-blue-600" size={24} />
                  <div>
                    <p className="font-semibold">90-day returns</p>
                    <p className="text-sm text-gray-600">Free & easy returns</p>
                  </div>
                </div>
              </div>

              {/* Quantity Selector */}
              <div className="mb-6">
                <label className="block font-semibold mb-2">Quantity:</label>
                <div className="flex items-center gap-3">
                  <button
                    onClick={() => setQuantity(Math.max(1, quantity - 1))}
                    className="bg-gray-200 hover:bg-gray-300 px-4 py-2 rounded font-bold"
                  >
                    -
                  </button>
                  <span className="text-xl font-semibold w-12 text-center">
                    {quantity}
                  </span>
                  <button
                    onClick={() => setQuantity(quantity + 1)}
                    className="bg-gray-200 hover:bg-gray-300 px-4 py-2 rounded font-bold"
                  >
                    +
                  </button>
                </div>
              </div>

              {/* Action Buttons */}
              <div className="space-y-3">
                <button
                  onClick={handleAddToCart}
                  className="w-full bg-blue-600 text-white py-3 rounded-full font-bold hover:bg-blue-700 transition text-lg"
                >
                  Add to Cart
                </button>
                <button
                  onClick={handleBuyNow}
                  className="w-full bg-yellow-400 text-gray-800 py-3 rounded-full font-bold hover:bg-yellow-500 transition text-lg"
                >
                  Buy Now
                </button>
              </div>
            </div>
          </div>

          {/* Additional Info */}
          <div className="border-t p-8">
            <h3 className="text-2xl font-bold mb-4">Product Specifications</h3>
            <div className="grid md:grid-cols-2 gap-4">
              <div className="flex justify-between py-2 border-b">
                <span className="font-semibold">Category:</span>
                <span>{product.category}</span>
              </div>
              <div className="flex justify-between py-2 border-b">
                <span className="font-semibold">Item ID:</span>
                <span>#{product.id.toString().padStart(6, '0')}</span>
              </div>
              <div className="flex justify-between py-2 border-b">
                <span className="font-semibold">Availability:</span>
                <span className="text-green-600 font-semibold">In Stock</span>
              </div>
              <div className="flex justify-between py-2 border-b">
                <span className="font-semibold">Shipping:</span>
                <span>Free 2-day shipping</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <Footer />
    </div>
  );
};

export default ProductDetail;