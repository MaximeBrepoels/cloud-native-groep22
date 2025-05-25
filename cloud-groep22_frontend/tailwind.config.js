/** @type {import('tailwindcss').Config} */
module.exports = {
    content: [
        "./pages/**/*.{js,ts,jsx,tsx}",
        "./components/**/*.{js,ts,jsx,tsx}",
        "./app/**/*.{js,ts,jsx,tsx}",
    ],
    theme: {
        extend: {
            colors: {
                'custom-blue': '#222629',
                'green-button-home': '#86C232',
                'bg-hover-button-home': '#61892F',
            },
        },
    },
    plugins: [],
}