import { useEffect, useState, createContext, useRef, useContext, useCallback } from 'react';
import Constants from "expo-constants";
import { API, WS } from './config.json';

export const GameContext = createContext({});
//export const { API_HOST, WS_HOST } = Constants.expoConfig.extra;

// LAN IP of your PC (reachable from emulator or device)
const MOBILE_IP = API;

// API base URL
export const API_HOST =
  Platform.OS === "web"
    ? typeof window !== "undefined" 
      ? window.location.origin // safe on web
      : `http://${MOBILE_IP}` // fallback if somehow window is undefined
    : `http://${MOBILE_IP}`; // mobile (Android/iOS)

// WebSocket URL
export const WS_HOST =
  Platform.OS === "web"
    ? typeof window !== "undefined"
      ? `${window.location.protocol === "https:" ? "wss" : "ws"}://${window.location.host}/ingame`
      : `http://${MOBILE_IP}`
    : `ws://${MOBILE_IP}/ingame`; // mobile (Android/iOS)