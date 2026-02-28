import { useEffect, useState, createContext, useRef, useContext, useCallback } from 'react';
import Constants from "expo-constants";

export const GameContext = createContext({});
export const { API_HOST, WS_HOST } = Constants.expoConfig.extra;