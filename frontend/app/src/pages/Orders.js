import React from 'react';
import { useNavigate } from 'react-router-dom';
import Header from '../components/Header';
import Footer from '../components/Footer';
import { Package, Truck, CheckCircle } from 'lucide-react';

const Orders = () => {
  const navigate = useNavigate();

  const mockOrders = [
    {
      id: '1001',
      date: '2024-12-20',
      total: 527.89,
      status: 'Delivered',
      items: [
        { id: 1, title: 'Smart TV 55"', image: '📺', quantity: 1, price: 398 },
        { id: 2, title: 'Wireless Headphones', image: '🎧', quantity: 2, price: 49 },
      ],
    },
    {
      id: '1002',
      date: '2024-12-21',
      total: 108.00,
      status: 'In Transit',
      items: [
        { id: 3, title: 'Coffee Maker', image: '☕', quantity: 1, price: 29 },
        { id: 4, title: 'Air Fryer', image: '🍳', quantity: 1, price: 79 },
      ],
    },
    {
      id: '1003',
      date: '2024-12-22',
      total: 188.00,
      status: 'Processing',
      items: [
        { id: 5, title: 'Vacuum Cleaner', image: '🧹', quantity: 1, price: 149 },
        { id: 6, title: 'Blender', image: '🥤', quantity: 1, price: 39 },
      ],
    },
  ];

  const getStatusIcon = (status) => {
    switch (status) {
      case 'Delivered':
        return <CheckCircle className="text-green-600" size={24} />;
      case 'In Transit':
        return <Truck className="text-blue-600" size={24} />;
      case 'Processing':
        return <Package className="text-yellow-600" size={24} />;
      default:
        return <Package className="text-gray-600" size={24} />;
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'Delivered':
        return 'bg-green-100 text-green-700';
      case 'In Transit':
        return 'bg-blue-100 text-blue-700';
      case 'Processing':
        return 'bg-yellow-100 text-yellow-700';
      default:
        return 'bg-gray-100 text-gray-700';
    }
  };

  if (mockOrders.length === 0) {
    return (
      <div className="min-h-screen bg-gray-50">
        <Header />
        <div className="max-w-7xl mx-auto px-4 py-12">
          <div className="bg-white rounded-lg shadow-lg p-12 text-center">
            <Package className="mx-auto mb-6 text-gray-400" size={80} />
            <h2 className="text-3xl font-bold text-gray-800 mb-4">
              No Orders Yet
            </h2>
            <p className="text-gray-600 mb-6">
              You haven't placed any orders yet. Start shopping to see your orders here!
            </p>
            <button
              onClick={() => navigate('/products')}
              className="bg-blue-600 text-white px-8 py-3 rounded-full font-bold hover:bg-blue-700 transition"
            >
              Start Shopping
            </button>
          </div>
        </div>
        <Footer />
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <Header />

      <div className="max-w-7xl mx-auto px-4 py-6">
        <h1 className="text-3xl font-bold text-gray-800 mb-6">My Orders</h1>

        <div className="space-y-6">
          {mockOrders.map((order) => (
            <div key={order.id} className="bg-white rounded-lg shadow overflow-hidden">
              {/* Order Header */}
              <div className="bg-gray-50 px-6 py-4 border-b">
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-6">
                    <div>
                      <p className="text-sm text-gray-600">Order #</p>
                      <p className="font-bold">{order.id}</p>
                    </div>
                    <div>
                      <p className="text-sm text-gray-600">Date Placed</p>
                      <p className="font-semibold">
                        {new Date(order.date).toLocaleDateString('en-US', {
                          month: 'long',
                          day: 'numeric',
                          year: 'numeric',
                        })}
                      </p>
                    </div>
                    <div>
                      <p className="text-sm text-gray-600">Total</p>
                      <p className="font-bold text-blue-600">${order.total.toFixed(2)}</p>
                    </div>
                  </div>
                  <div className="flex items-center gap-3">
                    {getStatusIcon(order.status)}
                    <span
                      className={`px-4 py-2 rounded-full text-sm font-bold ${getStatusColor(
                        order.status
                      )}`}
                    >
                      {order.status}
                    </span>
                  </div>
                </div>
              </div>

              {/* Order Items */}
              <div className="p-6">
                <div className="space-y-4">
                  {order.items.map((item) => (
                    <div
                      key={item.id}
                      className="flex items-center gap-4 pb-4 border-b last:border-b-0"
                    >
                      <div className="w-20 h-20 bg-gray-50 rounded flex items-center justify-center text-4xl">
                        {item.image}
                      </div>
                      <div className="flex-1">
                        <h3 className="font-semibold text-gray-800 mb-1">
                          {item.title}
                        </h3>
                        <p className="text-sm text-gray-600">
                          Quantity: {item.quantity}
                        </p>
                      </div>
                      <div className="text-right">
                        <p className="font-bold text-blue-600">
                          ${item.price.toFixed(2)}
                        </p>
                      </div>
                    </div>
                  ))}
                </div>

                {/* Order Actions */}
                <div className="flex gap-4 mt-6">
                  <button className="flex-1 bg-blue-600 text-white py-2 px-4 rounded hover:bg-blue-700 transition font-semibold">
                    Track Order
                  </button>
                  <button
                    onClick={() => navigate(`/product/${order.items[0].id}`)}
                    className="flex-1 bg-gray-200 text-gray-800 py-2 px-4 rounded hover:bg-gray-300 transition font-semibold"
                  >
                    Buy Again
                  </button>
                  <button className="flex-1 border border-gray-300 text-gray-800 py-2 px-4 rounded hover:bg-gray-50 transition font-semibold">
                    View Invoice
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>

      <Footer />
    </div>
  );
};

export default Orders;