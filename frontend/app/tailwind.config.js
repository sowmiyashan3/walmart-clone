/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{js,jsx,ts,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        walmart: {
          blue: '#0071dc',
          yellow: '#ffc220',
          darkBlue: '#004f9a',
        }
      }
    },
  },
  plugins: [],
}