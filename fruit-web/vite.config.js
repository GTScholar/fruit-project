import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  css: {
    preprocessorOptions: {
      scss: {
        // 全局注入主题变量，组件中可直接使用 $primary 等
        additionalData: `@use "@/assets/styles/variables.scss" as *;`
      }
    }
  },
  server: {
    port: 5173,
    open: true,
    proxy: {
      // 后端接口代理，避免跨域
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
