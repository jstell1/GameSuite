import { StatusBar } from 'expo-status-bar';
import { useEffect, useState } from 'react';
import { 
  KeyboardAvoidingView, TouchableWithoutFeedback, 
  Keyboard, Alert, 
  StyleSheet, Text, 
  TextInput, ScrollView, 
  View, Button } from 'react-native';
import Constants from "expo-constants";
import { NavigationContainer } from "@react-navigation/native";
import { createNativeStackNavigator } from "@react-navigation/native-stack";

const { API_HOST, WS_HOST } = Constants.expoConfig.extra;
let ws;
let sessionId = null;
let numClicks = 0;
let isClickable = true;
let startX = -1;
let startY = -1;
let endX = -1;
let endY = -1;
let turnNum = 1;
let playerTurn;
let gameTurn;

const Stack = createNativeStackNavigator();

export default function App() {
  return (
    <NavigationContainer>
      <Stack.Navigator>
        <Stack.Screen name="Home" component={HomeScreen} />
        <Stack.Screen name="GameBoard" component={GameBoardScreen} />
      </Stack.Navigator>
    </NavigationContainer>
  );
}

const connectWebSocket = (navigation) => {
  ws = new WebSocket(`${WS_HOST}`);

  ws.onopen = () => {
    console.log("WebSocket connected");
  };

  ws.onmessage = e => {
    
    console.log(e.data);
    const data = JSON.parse(e.data);
      console.log("WS Message:", data);

      // Always check for sessionId
      if (data.sessionId && sessionId === null) {
          sessionId = data.sessionId;
      }

      if (data.resp1) {
          console.log("Game ready:", data.resp1);
          gameTurn = data.resp1.game.turn;
          navigation.navigate("GameBoard");
          //showGameBoard(gameId);
      }

      if (data.resp2) {
          if (data.resp2.game) {
              // Initial render or updates
              gameTurn = data.resp2.game.turn;
              if(playerTurn === gameTurn)
                  isClickable = true;
              else
                  isClickable = false;
              //renderBoard(data.resp2.game);
          }
      }
  };

  ws.onerror = e => { console.log(e.message); };

  ws.onclose = e => { console.log(e.code, e.reason); };
}

function HomeScreen({navigation}) {
  
  const [createName, setCreateName] = useState("");
  const [joinName, setJoinName] = useState("");
  const [joinGameId, setJoinGameId] = useState("");
  const [gameId, setGameId] = useState("Create or Join Game");
  useEffect(() => {connectWebSocket(navigation)}, []);

  const createGame = async () => {
    if(!createName) { Alert.alert("Must have name!"); return; }
    //Alert.alert("I'm here");
    const resp = await fetch(`${API_HOST}/games`, {
        method: "POST",
        headers: {
        "Content-Type": "application/json", 
        },
        body: JSON.stringify({
          name: createName,
          sessionId: sessionId
        })
      });
    //Alert.alert("I'm here too!");
    if(!resp.ok) {
      Alert.alert("Did not create game");
      return;
    }
    const data = await resp.json();
    setGameId(data.gameId);
    playerTurn = 1;
    //Alert.alert(gameId);
  }

  const joinGame = async () => {
    if(!joinName || !joinGameId) {
      Alert.alert("Must have name and game id!");
      return;
    }
    
    const resp = await fetch(`${API_HOST}/games/players`, {
      method: "PATCH",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ 
        player: joinName, 
        gameId: joinGameId, 
        sessionId: sessionId })
    });

    if(!resp.ok) {
      Alert.alert("Did not join!");
    }

    const data = await resp.json();
    setGameId(data.gameId);
    playerTurn = 2;
  }

  return (

    <KeyboardAvoidingView 
      style={{ flex: 1 }} 
    >
      <TouchableWithoutFeedback 
        onPress={Keyboard.dismiss} accessible={false}>
      <ScrollView 
          contentContainerStyle={styles.container} 
          keyboardShouldPersistTaps="handled"
        >
          <Text selectable={true}>{gameId}</Text>
          <Text>Name</Text>
          <TextInput style={styles.input} onChangeText={setCreateName}/>
          <Button title="Create Game" onPress={createGame}/>
          <Text>Name</Text>
          <TextInput style={styles.input} onChangeText={setJoinName}/>
          <Text>GameId</Text>
          <TextInput style={styles.input} onChangeText={setJoinGameId}/>
          <Button title="Join Game" onPress={joinGame}/>
          <StatusBar style="auto" />
        </ScrollView>
      </TouchableWithoutFeedback>
    </KeyboardAvoidingView>
  );
}


function Square({ color }) {
  return <View style={[styles.square, { backgroundColor: color }]} />;
}

function GameBoardScreen({navigation}) {
  const squares = [];

  for (let row = 0; row < 8; row++) {
    for (let col = 0; col < 8; col++) {
      const isDark = (row + col) % 2 === 1; 
      squares.push(
        <Square
          key={`${row}-${col}`} 
          color={isDark ? "black" : "red"}
        />
      );
    }
  }

  return <View style={styles.board}>{squares}</View>;
}


const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  },
  input: {
    height: 40,
    width: 300,
    margin: 12,
    borderWidth: 1,
    padding: 10,
  },
    board: {
    flexDirection: "row",
    flexWrap: "wrap", // allows children to wrap into rows
    width: 320, // 8 * 40px squares
    height: 320,
  },
  square: {
    width: 40,
    height: 40,
  },
});
