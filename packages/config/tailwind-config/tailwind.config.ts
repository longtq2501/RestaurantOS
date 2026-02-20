import type { Config } from "tailwindcss";

const config: Omit<Config, "content"> = {
  theme: {
    extend: {
      colors: {
        primary: "#F97316", // Orange-500
        secondary: "#3B82F6", // Blue-500
        success: "#22C55E", // Green-500
        danger: "#EF4444", // Red-500
        warning: "#EAB308", // Yellow-500
        slate: "#64748B", // Slate-500
        background: "hsl(var(--background))",
        foreground: "hsl(var(--foreground))",
      },
      fontFamily: {
        sans: ["var(--font-geist-sans)"],
        mono: ["var(--font-geist-mono)"],
      },
    },
  },
  plugins: [],
};

export default config;
