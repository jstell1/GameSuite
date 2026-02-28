import { StatusBar } from 'expo-status-bar';
import { useEffect, useState, createContext, useContext } from 'react';
import { 
  KeyboardAvoidingView, TouchableWithoutFeedback, 
  Keyboard, Alert, 
  StyleSheet, Text, 
  TextInput, ScrollView, 
  View, Button,
  Pressable, Dimensions,
  Platform } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import Constants from "expo-constants";
import { NavigationContainer } from "@react-navigation/native";
import { createNativeStackNavigator } from "@react-navigation/native-stack";
import { GameContext, API_HOST, WS_HOST } from '../Global';

const { width } = Dimensions.get('window');
const squareSize = width / 8;

export default function GameBoardScreen({navigation, route}) {
  const {gameBoard } = route.params;
  const { resetSocket} = useContext(GameContext);
  const [board,setBoard] = useState(null); //() => initBoardData(gameBoard));
  const [highlights, setHighlights] = useState([]);
  const [numClicks, setNumClicks] = useState(0);
  const [start, setStart] = useState(null);
  
  const [game, setGame] = useState(null);
  
  const { isClickable, setIsClickable } = useContext(GameContext);
  const { playerTurn } = useContext(GameContext);
  const {gameTurn } = useContext(GameContext); //string for displaying on screen

  useEffect(() => {
    if(game.turn === playerTurn.current) {
      setIsClickable(true);
    }
  }, [playerTurn, game]);

  useEffect(() => {
    setBoard(() => initBoardData(gameBoard));
  }, [gameBoard]);

  //setBoard(() => initBoardData());
  
  useEffect(() => {
    navigation.setOptions({
      headerLeft: () => (
        <Button
          title="Home"
          onPress={async () => {
            await ws.close();
            
            await resetSocket();
            await navigation.pop();
            playerTurn.current = 0;
            //setGame(null);
          }}
        />
      )
    });
  }, [navigation, resetSocket]);

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

  function initBoardData(gameBoard) {
    const arr = [];
    for (let row = 0; row < 8; row++) {
      const rowArr = [];
      for (let col = 0; col < 8; col++) {
        let piece = null;
        let tmp = gameBoard[row][col]["piece"];
        let name = tmp != null ? tmp["name"] : null;

        if(name != null && name === "B")
          piece = { color: "black", type: "C" };
        else if(name != null && name === "R")
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
        moveRequest: {
          gameId: currGameId,
          move: {
            startX: highlights[0].row,
            startY: highlights[0].col,
            endX: end.row,
            endY: end.col
          }
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
      <Text>{gameTurn}</Text>
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
