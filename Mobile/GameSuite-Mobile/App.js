import { StatusBar } from 'expo-status-bar';
import { useEffect, useState, createContext, useContext } from 'react';
import { 
  KeyboardAvoidingView, TouchableWithoutFeedback, 
  Keyboard, Alert, 
  StyleSheet, Text, 
  TextInput, ScrollView, 
  View, Button,
  Pressable, Dimensions } from 'react-native';
import Constants from "expo-constants";
import { NavigationContainer } from "@react-navigation/native";
import { createNativeStackNavigator } from "@react-navigation/native-stack";

const GameContext = createContext();
const { width } = Dimensions.get('window');
const squareSize = width / 8;
const { API_HOST, WS_HOST } = Constants.expoConfig.extra;
let ws;
let sessionId = null;
let isClickable = true;
let turnNum = 1;
let playerTurn;
let gameTurn;
let name;
let game;
let currGameId;

const Stack = createNativeStackNavigator();

export default function App() {

  const [game, setGame] = useState(null);

  return (
    <GameContext.Provider value={{ game, setGame }}>
      <NavigationContainer>
        <Stack.Navigator>
          <Stack.Screen name="Home" component={HomeScreen} />
          <Stack.Screen name="GameBoard" component={GameBoardScreen} />
        </Stack.Navigator>
      </NavigationContainer>
    </GameContext.Provider>
  );
}



function HomeScreen({navigation}) {
  
  const { setGame } = useContext(GameContext);
  const [createName, setCreateName] = useState("");
  const [joinName, setJoinName] = useState("");
  const [joinGameId, setJoinGameId] = useState("");
  const [gameId, setGameId] = useState("Create or Join Game");
  useEffect(() => {connectWebSocket()}, []);

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
            id = data.resp1.gameId;
            currGameId = data.resp1.gameId;
            navigation.navigate("GameBoard",
              {id, sessionId, name}
            );
        }

        if (data.resp2) {
            if (data.resp2.game) {
                gameTurn = data.resp2.game.turn;
                if(playerTurn === gameTurn)
                    isClickable = true;
                else
                    isClickable = false;
                setGame(data.resp2.game);
            }
        }
    };

    ws.onerror = e => { console.log(e.message); };

    ws.onclose = e => { console.log(e.code, e.reason); };
  }

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
    name = createName;
    playerTurn = 1;
    //Alert.alert(gameId);
  }

  const joinGame = async () => {
    if(!joinName || !joinGameId) {
      Alert.alert("Must have name and game id!");
      return;
    }
    name = joinName;
    
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


function Square({ row, col, piece, onPress, highlighted }) {
  const isDark = (row + col) % 2 === 1;

  return (
    <Pressable
      onPress={() => onPress(row, col)}
      style={[
        styles.square,
        isDark ? styles.dark : styles.light,
        highlighted && styles.yellow,
      ]}
    >
      {piece && (
        <View
          style={[
            styles.piece,
            piece.color === "red" ? styles.red : styles.black,
            piece.type === "K" && styles.king,
          ]}
        />
      )}
    </Pressable>
  );
}

function GameBoardScreen({navigation, route}) {
  const { game } = useContext(GameContext);
  const [board, setBoard] = useState(() => initBoardData());
  const [highlights, setHighlights] = useState([]);
  const [numClicks, setNumClicks] = useState(0);
  const [start, setStart] = useState(null);
  const {gameId, sessionId, name} = route.params;

  useEffect(() => {
    if (game?.changedPos) {
      applyChanges(game.changedPos);
    }
  }, [game]);

  function applyChanges(changedPos) {
    const newBoard = board.map(row => [...row]);
    changedPos.forEach((pos) => {
      if(pos.piece !== null) {
        let color;
        if(pos.piece.name === "R") 
          color = "red";
        else
          color = "black"
        
        newBoard[pos.x][pos.y] = {color: color, type: pos.piece.type};
      } else {
        newBoard[pos.x][pos.y] = null;
      }
    });
    setBoard(newBoard);
  }

  function initBoardData() {
    const arr = [];
    for (let row = 0; row < 8; row++) {
      const rowArr = [];
      for (let col = 0; col < 8; col++) {
        let piece = null;
        if (row < 3 && (row + col) % 2 === 1) 
          piece = { color: "black", type: "C" };
        else if (row > 4 && (row + col) % 2 === 1) 
          piece = { color: "red", type: "C" };
        rowArr.push(piece);
      }
      arr.push(rowArr);
    }
    return arr;
  }

  async function handlePress(row, col) {
    if(isClickable === false) return;
    if (numClicks === 0) {
      setStart({ row, col });
      setHighlights([{ row, col }]);
      setNumClicks(1);
    } else if (numClicks === 1) {
      const end = { row, col };
      
      //console.log("Move:", start, "->", end);
      const movMessage = {
        gameId: currGameId,
        sessionId: sessionId,
        move: {
          startX: highlights[0].row,
          startY: highlights[0].col,
          endX: end.row,
          endY: end.col
        }
      }
      setHighlights([]);
      setNumClicks(0);
      setStart(null);
      isClickable = false;
      ws.send(JSON.stringify(movMessage));
    }
  }

  return (
    <View style={styles.container}>
      <View style={styles.board}>
        {board.map((rowArr, row) =>
          rowArr.map((piece, col) => {
            const highlighted = highlights.some(h => h.row === row && h.col === col);
            return ( 
              <Square
                key={`${row}-${col}`}
                row={row}
                col={col}
                piece={piece}
                onPress={handlePress}
                highlighted={highlighted}
              />
            );
          })
        )}
      </View>
    </View>
  );
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
    width: '100%',
    aspectRatio: 1,
    flexDirection: "row",
    flexWrap: "wrap",
  },
  square: {
    height: squareSize,
    width: squareSize,
    alignItems: "center",
    justifyContent: "center",
  },
  dark: { backgroundColor: "saddlebrown" },
  light: { backgroundColor: "lightgray" },
  yellow: { backgroundColor: "yellow" },
  piece: {
    width: "80%",
    height: "80%",
    borderRadius: 50,
  },
  red: { backgroundColor: "red" },
  black: { backgroundColor: "black" },
  king: {
    borderWidth: 2,
    borderColor: "gold",
  },
});
