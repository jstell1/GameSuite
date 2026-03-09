
import { createContext } from "react";
import { Platform } from "react-native";

export const GameContext = createContext({});

// LAN IP of your PC
const MOBILE_IP = "192.168.0.43:8080";

/*
Determine protocol
*/
const isWeb = Platform.OS === "web";

const httpProtocol =
  isWeb && typeof window !== "undefined"
    ? window.location.protocol === "https:" ? "https" : "http"
    : "http";

const wsProtocol =
  isWeb && typeof window !== "undefined"
    ? window.location.protocol === "https:" ? "wss" : "ws"
    : "ws";

/*
Determine host
*/
const host =
  isWeb && typeof window !== "undefined"
    ? window.location.host
    : MOBILE_IP;

/*
Base URLs
*/
export const API_HOST = `${httpProtocol}://${host}`;

export const WS_HOST = `${wsProtocol}://${host}/ingame`;

export const gamesListURI = `${httpProtocol}://${host}/games`;