import React from 'react';
import { Star } from 'lucide-react';
import { useCart } from '../context/CartContext';
import { useNavigate } from 'react-router-dom';

const ProductCard = ({ product, isFlashDeal = false }) => {
  const { addToCart } = useCart();
  const navigate = useNavigate();
  
  const discount = isFlashDeal 
    ? Math.round(((product.originalPrice - product.price) / product.originalPrice) * 100) 
    : null;

  const handleAddToCart = (e) => {
    e.stopPropagation();
    addToCart(product);
  };

  const handleCardClick = () => {
    navigate(`/product/${product.id}`);
  };

  return (
    <div 
      onClick={handleCardClick}
      className={`bg-white rounded-lg shadow hover:shadow-lg transition cursor-pointer p-4 ${isFlashDeal ? 'border-2 border-red-500 relative' : 'border border-gray-200'}`}
    >
      {isFlashDeal && (
        <div className="absolute top-2 right-2 bg-red-600 text-white px-2 py-1 rounded-full text-xs font-bold z-10">
          {discount}% OFF
        </div>
      )}
      
      <div className="text-6xl mb-3 text-center">{product.image}</div>
      
      <h4 className="font-semibold text-gray-800 mb-2 text-sm h-10 line-clamp-2">
        {product.title}
      </h4>
      
      {product.rating && (
        <div className="flex items-center gap-1 mb-2 text-xs">
          <Star size={14} fill="#FFA500" color="#FFA500" />
          <span className="font-semibold">{product.rating}</span>
          <span className="text-gray-500">({product.reviews})</span>
        </div>
      )}
      
      <div className="flex items-baseline gap-2 mb-3">
        <span className={`text-xl font-bold ${isFlashDeal ? 'text-red-600' : 'text-blue-600'}`}>
          ${product.price}
        </span>
        {product.originalPrice && (
          <span className="text-xs text-gray-500 line-through">
            ${product.originalPrice}
          </span>
        )}
      </div>
      
      {isFlashDeal && product.timeLeft && (
        <div className="text-xs text-gray-600 mb-2">
          Ends in {product.timeLeft}
        </div>
      )}
      
      <button 
        onClick={handleAddToCart}
        className="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700 transition text-sm font-semibold"
      >
        Add to Cart
      </button>
    </div>
  );
};

export default ProductCard;