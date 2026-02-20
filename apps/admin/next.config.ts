import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  /* config options here */
  transpilePackages: ["@restaurantos/ui", "@restaurantos/types", "@restaurantos/utils"],
};

export default nextConfig;
