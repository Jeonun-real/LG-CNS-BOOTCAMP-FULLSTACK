import { defineConfig, loadEnv } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '');

  return {
    plugins: [react()],
    server: {
      host: '127.0.0.1',
      port: 3200,
      strictPort: true,
    },
    preview: {
      host: '127.0.0.1',
      port: 3200,
      strictPort: true,
    },
    // 기존 CRA 환경변수 코드를 수정하지 않고 그대로 사용할 수 있게 연결한다.
    define: {
      'process.env.REACT_APP_BACKEND_ENDPOINT': JSON.stringify(
        env.REACT_APP_BACKEND_ENDPOINT,
      ),
      'process.env.REACT_APP_WEATHER_API_KEY': JSON.stringify(
        env.REACT_APP_WEATHER_API_KEY,
      ),
    },
  };
});
