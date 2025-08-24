import { StatusBar } from 'expo-status-bar';
import { StyleSheet, Text, TextInput, View, Button } from 'react-native';
import Constants from "expo-constants";

const { API_HOST, WS_HOST } = Constants.expoConfig.extra;
let ws;
let host; // whatever was used to load the page
let socket;
let sessionId = null;
let gameId = null;
let numClicks = 0;
let isClickable = true;
let startX = -1;
let startY = -1;
let endX = -1;
let endY = -1;
let turnNum = 1;
let playerTurn;
let gameTurn;

export default function App() {
  connectWebSocket();
  return (
    <View style={styles.container}>
      <Text>Create or Join Game</Text>
      <Text>Name</Text>
      <TextInput style={styles.input}/>
      <Button title="Create Game" onPress={createGame()}/>
      <Text>Name</Text>
      <TextInput style={styles.input}/>
      <Text>GameId</Text>
      <TextInput style={styles.input}/>
      <Button title="Join Game" />
      <StatusBar style="auto" />
    </View>
  );
}

function connectWebSocket() {
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
          showGameBoard(gameId);
      }

      if (data.resp2) {
          if (data.resp2.game) {
              // Initial render or updates
              gameTurn = data.resp2.game.turn;
              if(playerTurn === gameTurn)
                  isClickable = true;
              else
                  isClickable = false;
              renderBoard(data.resp2.game);
          }
      }
  };

  ws.onerror = e => {
    // an error occurred
    console.log(e.message);
  };

  ws.onclose = e => {
    // connection closed
    console.log(e.code, e.reason);
  };
}

async function createGame() {
  
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
  }
});
