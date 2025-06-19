import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  ...(process.env.NODE_ENV === 'production' && { output: 'export' }),
  trailingSlash: true,
  images: {
    unoptimized: true
  },
  reactStrictMode: true,
  devIndicators: false,
  assetPrefix: process.env.NODE_ENV === 'production'
      ? 'https://cloudgroep22storage.z6.web.core.windows.net'
      : '',
  basePath: process.env.NODE_ENV === 'production' ? '' : ''
};

export default nextConfig;
