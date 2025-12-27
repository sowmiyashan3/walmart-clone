import React from 'react';

const Footer = () => {
  const footerLinks = {
    shop: ['All Departments', 'Trending Products', 'New Arrivals'],
    help: ['Track Orders', 'Returns', 'Contact Us'],
    account: ['Sign In', 'Register', 'My Account'],
    about: ['About mart', 'Careers', 'Sustainability']
  };

  return (
    <footer className="bg-blue-600 text-white mt-12 py-8">
      <div className="max-w-7xl mx-auto px-4">
        <div className="grid grid-cols-2 md:grid-cols-4 gap-8 mb-6">
          <div>
            <h4 className="font-bold mb-3">Shop</h4>
            <ul className="space-y-2 text-sm">
              {footerLinks.shop.map((link, idx) => (
                <li key={idx}>
                  <button className="hover:underline">{link}</button>
                </li>
              ))}
            </ul>
          </div>
          
          <div>
            <h4 className="font-bold mb-3">Help</h4>
            <ul className="space-y-2 text-sm">
              {footerLinks.help.map((link, idx) => (
                <li key={idx}>
                  <button className="hover:underline">{link}</button>
                </li>
              ))}
            </ul>
          </div>
          
          <div>
            <h4 className="font-bold mb-3">Account</h4>
            <ul className="space-y-2 text-sm">
              {footerLinks.account.map((link, idx) => (
                <li key={idx}>
                  <button className="hover:underline">{link}</button>
                </li>
              ))}
            </ul>
          </div>
          
          <div>
            <h4 className="font-bold mb-3">About</h4>
            <ul className="space-y-2 text-sm">
              {footerLinks.about.map((link, idx) => (
                <li key={idx}>
                  <button className="hover:underline">{link}</button>
                </li>
              ))}
            </ul>
          </div>
        </div>
        
        <div className="border-t border-blue-500 pt-6 text-center text-sm">
          <p>© 2024 mart Clone. All rights reserved.</p>
        </div>
      </div>
    </footer>
  );
};

export default Footer;