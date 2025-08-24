

export default {
  expo: {
    name: "my-app",
    slug: "my-app",
    extra: {
      API_HOST: process.env.API_HOST || "http://localhost:8080",
      WS_HOST: process.env.WS_HOST || "ws://localhost:8080/ingame"
    }
  }
};
