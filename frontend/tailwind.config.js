/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{vue,js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        apple: {
          blue: '#007AFF',
          green: '#34c759',
          orange: '#ff9500',
          red: '#ff3b30',
          gray: '#86868b',
          bg: '#f5f5f7',
          text: '#1d1d1f',
        },
        // 二次元渐变色系
        anime: {
          // 柔和粉色系
          'pink-light': '#FFE5F1',
          'pink': '#FFB3D9',
          'pink-deep': '#FF85C1',

          // 梦幻紫色系
          'purple-light': '#F3E5FF',
          'purple': '#D4ADFC',
          'purple-deep': '#B185DB',

          // 清新青色系
          'cyan-light': '#E0F7FF',
          'cyan': '#9FDFFF',
          'cyan-deep': '#5EC4FF',

          // 活力橙色系
          'orange-light': '#FFE8D6',
          'orange': '#FFBA8F',
          'orange-deep': '#FF9B5E',

          // 温柔薰衣草
          'lavender-light': '#EDE7F6',
          'lavender': '#C5B9E8',
          'lavender-deep': '#9B8BC7',
        }
      },
      backgroundImage: {
        // 二次元风格渐变背景
        'anime-gradient-1': 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
        'anime-gradient-2': 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
        'anime-gradient-3': 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
        'anime-gradient-4': 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',

        // 柔光渐变
        'soft-pink': 'linear-gradient(135deg, #FFE5F1 0%, #FFB3D9 50%, #FF85C1 100%)',
        'soft-purple': 'linear-gradient(135deg, #F3E5FF 0%, #D4ADFC 50%, #B185DB 100%)',
        'soft-cyan': 'linear-gradient(135deg, #E0F7FF 0%, #9FDFFF 50%, #5EC4FF 100%)',
        'soft-lavender': 'linear-gradient(135deg, #EDE7F6 0%, #C5B9E8 50%, #9B8BC7 100%)',

        // 双色交错渐变
        'anime-sunset': 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)',
        'anime-ocean': 'linear-gradient(135deg, #a8edea 0%, #fed6e3 100%)',
        'anime-dream': 'linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%)',
        'anime-sky': 'linear-gradient(135deg, #a1c4fd 0%, #c2e9fb 100%)',
      },
      boxShadow: {
        // 二次元风格阴影 - 更柔和、更有层次
        'anime-sm': '0 2px 8px rgba(139, 92, 246, 0.08), 0 1px 2px rgba(139, 92, 246, 0.06)',
        'anime': '0 4px 16px rgba(139, 92, 246, 0.12), 0 2px 6px rgba(139, 92, 246, 0.08)',
        'anime-md': '0 8px 24px rgba(139, 92, 246, 0.15), 0 4px 8px rgba(139, 92, 246, 0.1)',
        'anime-lg': '0 16px 40px rgba(139, 92, 246, 0.2), 0 8px 16px rgba(139, 92, 246, 0.12)',
        'anime-xl': '0 24px 60px rgba(139, 92, 246, 0.25), 0 12px 24px rgba(139, 92, 246, 0.15)',

        // 发光阴影
        'glow-pink': '0 0 20px rgba(255, 179, 217, 0.4), 0 0 40px rgba(255, 179, 217, 0.2)',
        'glow-purple': '0 0 20px rgba(212, 173, 252, 0.4), 0 0 40px rgba(212, 173, 252, 0.2)',
        'glow-cyan': '0 0 20px rgba(159, 223, 255, 0.4), 0 0 40px rgba(159, 223, 255, 0.2)',
        'glow-blue': '0 0 20px rgba(0, 122, 255, 0.4), 0 0 40px rgba(0, 122, 255, 0.2)',
      },
      dropShadow: {
        'anime': '0 4px 12px rgba(139, 92, 246, 0.15)',
        'glow': '0 0 8px rgba(255, 255, 255, 0.8)',
      },
      backdropBlur: {
        'anime': '40px',
      },
      animation: {
        // 新增动画
        'float': 'float 3s ease-in-out infinite',
        'pulse-soft': 'pulse-soft 2s cubic-bezier(0.4, 0, 0.6, 1) infinite',
        'shimmer': 'shimmer 2s linear infinite',
        'bounce-soft': 'bounce-soft 1s ease-in-out infinite',
        'scale-in': 'scale-in 0.3s ease-out',
        'slide-up': 'slide-up 0.4s ease-out',
        'fade-in': 'fade-in 0.3s ease-out',
        'ripple': 'ripple 0.6s ease-out',
      },
      keyframes: {
        float: {
          '0%, 100%': { transform: 'translateY(0px)' },
          '50%': { transform: 'translateY(-10px)' },
        },
        'pulse-soft': {
          '0%, 100%': { opacity: '1', transform: 'scale(1)' },
          '50%': { opacity: '0.8', transform: 'scale(1.05)' },
        },
        shimmer: {
          '0%': { backgroundPosition: '-200% 0' },
          '100%': { backgroundPosition: '200% 0' },
        },
        'bounce-soft': {
          '0%, 100%': { transform: 'translateY(0)' },
          '50%': { transform: 'translateY(-5px)' },
        },
        'scale-in': {
          '0%': { transform: 'scale(0.9)', opacity: '0' },
          '100%': { transform: 'scale(1)', opacity: '1' },
        },
        'slide-up': {
          '0%': { transform: 'translateY(20px)', opacity: '0' },
          '100%': { transform: 'translateY(0)', opacity: '1' },
        },
        'fade-in': {
          '0%': { opacity: '0' },
          '100%': { opacity: '1' },
        },
        ripple: {
          '0%': { transform: 'scale(0)', opacity: '1' },
          '100%': { transform: 'scale(2.5)', opacity: '0' },
        },
      },
      transitionTimingFunction: {
        'bounce-in': 'cubic-bezier(0.68, -0.55, 0.265, 1.55)',
        'smooth': 'cubic-bezier(0.4, 0, 0.2, 1)',
      },
      fontFamily: {
        sans: ['-apple-system', 'BlinkMacSystemFont', 'SF Pro Display', 'SF Pro Text', 'Helvetica Neue', 'Arial', 'sans-serif'],
      },
      letterSpacing: {
        tightest: '-0.03em',
      },
      borderRadius: {
        '2xl': '16px',
        '3xl': '20px',
      }
    },
  },
  plugins: [],
}
