import { StatusBar } from 'expo-status-bar';
import { useEffect, useState, createContext, useContext, useRef } from 'react';
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
import { NavigationContainer, useIsFocused } from "@react-navigation/native";
import { createNativeStackNavigator } from "@react-navigation/native-stack";
import { GameContext, API_HOST, WS_HOST, gamesListURI } from '../Global';
import { FlatList } from 'react-native-web';




export default function ActiveGamesScreen({navigation, route}) {
    const { gameChoice } = useContext(GameContext);
    const { playerTurn } = useContext(GameContext);
    const { setCurrGameId }  = useContext(GameContext);
    const { ws } = useContext(GameContext);
    const { gameBoard, setGameBoard } = useContext(GameContext);
    const [ gamesList, setGamesList ] = useState([]);
     const [ selectedIndex, setSelectedIndex ] = useState(null);
    const { createName } = useContext(GameContext);
    const { game, setGame } = useContext(GameContext);
    const { resetSocket } = useContext(GameContext);
    const { sessionId } = useContext(GameContext);

    useEffect(() => {getGames()}, [gameChoice]);

    const getGames = async () => {
         if(gameChoice === null) return;
         //console.log("getting list");
        fetch(gamesListURI + '/' + gameChoice)
          .then(resp => resp.json())
          .then(data =>
            {
           //   console.log(data);
              if(data)
                setGamesList(data);
            }
          );
        
    }

    useEffect(() => {
        const unsubscribe = navigation.addListener("beforeRemove", async (e) => {
            if (ws.current?.readyState !== WebSocket.OPEN) return; // normal back behavior

            try {
                ws.current?.close();

                //await resetSocket();

                playerTurn.current = 0;
                setGame(null);
                setGameBoard(null);
                setCurrGameId("Create or Join Game");

            } catch (err) {
                console.log("cleanup error:", err);
            }
  });

  return unsubscribe;
}, [navigation]);

    useEffect(() => {
        if(!gameBoard) return;
        navigation.navigate("GameBoard");
    }, [gameBoard, navigation]);
    
    const createGame = async () => {
      if(!createName) { Alert.alert("Must have name!"); return; }
      if(game !== null) { Alert.alert("Already created game"); return; }
     
      await resetSocket();
      console.log("socket setup");

      let msg = {
        "createGameRequest": {
            "game": gameChoice,
            "name": createName
        }
      }
      console.log("about to send");
      console.log(ws.current?.readyState === WebSocket.OPEN);
      ws.current.send(JSON.stringify(msg));
      console.log("sending createGameRequest");
    }

    const joinGame = async () => {
        if(game !== null) {
            Alert.alert("Already created game"); return;
        }
        if(!createName || !gamesList || selectedIndex === null) {
        Alert.alert("Must have name and game id!");
        return;
        }
        
        await resetSocket();
          let payload = {
            "joinGameRequest": {
                "name": createName,
                "gameId": gamesList[selectedIndex]
            }
        }
        console.log(payload);
        ws.current.send(JSON.stringify(payload));
    }
      
    return (
      <SafeAreaView style={{ flex: 1 }}>
        <View style={styles.webContainer}>
          <ScrollView contentContainerStyle={{ padding: 20 }}>
            {gamesList 
              ? gamesList.map((item, idx) => {
              const selected = idx === selectedIndex;
    
              return (
                <Pressable
                  key={idx}
                  onPress={() => setSelectedIndex(idx)}
                  style={{
                    padding: 12,
                    marginBottom: 6,
                    backgroundColor: selected ? "#4da6ff" : "#eee",
                    borderRadius: 6
                  }}
                >
                  <Text style={{ color: selected ? "white" : "black" }}>
                    {item}
                  </Text>
                </Pressable>
              );
            }) : <Text>Not here yet!</Text>}
          </ScrollView>
          <View style={styles.container}>
            <Pressable style={styles.button} onPress={createGame}><Text>Create Game</Text></Pressable>
            <Pressable style={styles.button} onPress={joinGame}><Text>Join Game</Text></Pressable>
            <Pressable style={styles.button} onPress={getGames}><Text>Refresh</Text></Pressable>
          </View>
        </View>
      </SafeAreaView>
    );


}



const styles = StyleSheet.create({
  container: {
    //flexDirection: "row",
    padding: 15,
    borderTopWidth: 1,
    borderColor: "black",
    backgroundColor: "white"
  },
  button: {
      padding: 15,
      marginBottom: 6,
      marginLeft: 6,
      marginRight: 6,
      backgroundColor: "#4da6ff",
      borderRadius: 16,
      alignItems: "center"
  },

   webContainer: {
    flex: 1,
    width: "100%",
    alignSelf: "center",
    ...Platform.select({
      web: {
        maxWidth: 600,   // max width only for web
          borderWidth: 1,
      borderColor: "#ccc",
      borderRadius: 8,
      backgroundColor: "white",
      marginTop: 20,
      overflow: "hidden"
      },
      default: {
        // mobile/other platforms: no max width
      }
    })
  }

});