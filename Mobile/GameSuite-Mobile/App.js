import { StatusBar } from 'expo-status-bar';
import { useEffect, useState, createContext, useRef, useContext, useCallback } from 'react';
import { 
  KeyboardAvoidingView, TouchableWithoutFeedback, 
  Keyboard, Alert, 
  StyleSheet, Text, 
  TextInput, ScrollView, 
  View, Button,
  Pressable, Dimensions,
  Platform } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { NavigationContainer } from "@react-navigation/native";
import { createNativeStackNavigator } from "@react-navigation/native-stack";
import Ajv from 'ajv';
import GameBoardScreen from './ui/GameBoardScreen';
import HomeScreen from './ui/HomeScreen';
import { GameContext, API_HOST, WS_HOST, gamesListURI } from './Global';
import GamesScreen from './ui/GamesScreen';
import ActiveGamesScreen from './ui/ActiveGamesScreen';


const listeners = {};
const Stack = createNativeStackNavigator();

export function subscribe(eventType, callback) {
  if (!listeners[eventType]) {
    listeners[eventType] = [];
  }

  listeners[eventType].push(callback);

  return function unsubscribe() {
    listeners[eventType] =
      listeners[eventType].filter(cb => cb !== callback);
  };
}

export function publish(eventType, data) {
  if (!listeners[eventType]) return;

  listeners[eventType].forEach(cb => cb(data));
}

export default function App() {

  const ajv = useRef(new Ajv());
  const ws = useRef(null);
  const sessionId = useRef(null);
  const validate = useRef(null);
  const [currGameId, setCurrGameId ] = useState("Create or Join Game");
  const playerTurn = useRef(0);
  const [ isClickable, setIsClickable ] = useState(false);
  const [game, setGame] = useState(null);
  const [createName, setCreateName] = useState("");
  const [joinName, setJoinName] = useState("");
  const [joinGameId, setJoinGameId] = useState("");
  const [ gameTurn, setGameTurn ] = useState("");
  const [ gameBoard, setGameBoard ] = useState(null);
  const [gameChoice, setGameChoice ] = useState("");
  //const gameBoard = useState(null);

  //console.log(currGameId);
  async function getSchema() {
    //console.log("in getSchema")
    const resp = await fetch(`${API_HOST}/schema`);
    //console.log(resp.text);
    const schema = await resp.json();
    //console.log(schema);
    validate.current = ajv.current.compile(schema);
  }

  function connectWebSocket() {
    return new Promise((resolve, reject) => {


        ws.current = new WebSocket(`${WS_HOST}`);
    
        ws.current.onopen = () => {
          //console.log("WebSocket connected");
          resolve(ws.current);
        };
    
        ws.current.onmessage = e => {
          
          //console.log(e.data);
          const data = JSON.parse(e.data);
            //console.log("WS Message:", data);
          const valid = validate.current(data);
    
          if (!valid) {
            let msg = {
              "badErrorRequest": {
                  "message": "don't recognize message type"
              }
            }
            ws.current.send(JSON.stringify(msg));
            //console.log(validate.current.errors);
            return;
          };
    
          let type = Object.keys(data)[0];
          let payload = data[type];
          //console.log(payload);
    
          switch(type) {
            case "sessionConnectedResponse":
                sessionId.current = payload.sessionId;
                //console.log(sessionId.current);
                break;
            case "gameCreatedResponse":
                setCurrGameId(payload.gameId);
                //listeners["gameCreatedResponse"](payload.gameId);
                //name = createName;
                //playerTurn = 1;
                //turnNum = payload.gameState.turn;
                //isClickable = false;
                setGame(payload.gameState);
                playerTurn.current = 1;
                break;
            case "gameReadyResponse":
              setCurrGameId(payload.gameId);
              
              if(playerTurn.current === 0) {
                //console.log("setting player turn to 2");
                playerTurn.current = 2;
                //name = joinName;
              }
              //console.log(payload.gameState.turn);
              setGameTurn(`Player ${payload.gameState.turn}'s turn`);
              //turnNum = payload.gameState.turn;
              //id = payload.gameId;
              //currGameId = payload.gameId;
              //setIsClickable(false);
    
              if(playerTurn.current === payload.gameState.turn) {
                setIsClickable(true);
              }
              setGame(payload.gameState);
              setGameBoard(payload.board);
              
              // navigation.navigate("GameBoard",
              //   { gameBoard }
              // );
              break;
            case "stateUpdateResponse":
              setGameTurn(`Player ${payload.gameState.turn}'s turn`);
              //console.log("new turn: " + payload.gameState.turn);
              //console.log("getting update");
              let turnNum = payload.gameState.turn;
              
              if(payload.gameState.winner == null) {
    
                if(playerTurn.current === turnNum) {
                  setIsClickable(true);
                  //console.log("my turn");
                }
                else {
                  setIsClickable(false);
                  //console.log("not my turn");
                }
    
              } else {
                setIsClickable(false);
                //console.log("game over");
                setGameTurn(`${payload.gameState.winner.name} is the winner`);
              }
              setGame(payload.gameState);
              break;
            default: 
                //console.log(data);
                break;
          }      
        };
        ws.current.onerror = e => { console.log(e.message); reject(e); };
    
        ws.current.onclose = e => { console.log(e.code, e.reason); };
    });


  }

  const resetSocket = useCallback(connectWebSocket,[]);

    
  useEffect(() => {
    getSchema();//resetSocket();//getSchema().then(() => connectWebSocket());
  }, []);

 

  return (
    <GameContext.Provider value={{ ws, playerTurn,
                                  currGameId,setCurrGameId, 
                                  isClickable, setIsClickable, 
                                  game, setGame,
                                  createName, setCreateName, 
                                  joinName, setJoinName, 
                                  joinGameId, setJoinGameId,
                                  gameTurn, setGameTurn,
                                  resetSocket, sessionId,
                                  gameBoard, setGameBoard,
                                  gameChoice, setGameChoice,
                                }}>
      <NavigationContainer>
        <Stack.Navigator
          screenOptions={{
            headerTitleAlign: 'center',
            gestureEnabled: false
          }}>
          <Stack.Screen name="Home" component={HomeScreen} />
          <Stack.Screen name="GamesScreen" component={GamesScreen} />
          <Stack.Screen name="ActiveGamesScreen" component={ActiveGamesScreen} />
          <Stack.Screen name="GameBoard" component={GameBoardScreen} />
        </Stack.Navigator>
      </NavigationContainer>
    </GameContext.Provider>
  );
}

