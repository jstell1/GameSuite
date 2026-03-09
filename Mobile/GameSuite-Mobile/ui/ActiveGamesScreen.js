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

    useEffect(() => {getGames()}, [gameChoice]);

    const getGames = async () => {
         if(gameChoice === null) return;
         console.log("getting list");
        fetch(gamesListURI + '/' + gameChoice)
          .then(resp => resp.json())
          .then(data =>
            {
              console.log(data);
              if(data)
                setGamesList(data);
            }
          );
        
    }

    useEffect(() => {
        const unsubscribe = navigation.addListener("beforeRemove", async (e) => {

            if (game === null || !useIsFocused()) return; // normal back behavior

            try {
                ws.current?.close();

                await resetSocket();

                playerTurn.current = 0;
                setGame(null);
                setGameBoard(null);
                setCurrGameId("Create or Join Game");

            } catch (err) {
                console.log("cleanup error:", err);
            }
  });

  return unsubscribe;
}, [navigation, game]);

    useEffect(() => {
        if(!gameBoard) return;
        navigation.navigate("GameBoard");
    }, [gameBoard, navigation]);
    
    const createGame = async () => {
    if(!createName) { Alert.alert("Must have name!"); return; }
    if(game !== null) { Alert.alert("Already created game"); return; }

    let msg = {
        "createGameRequest": {
            "game": gameChoice,
            "name": createName
        }
    }
    ws.current.send(JSON.stringify(msg));
    
    }

    const joinGame = async () => {
        if(game !== null) {
            Alert.alert("Already created game"); return;
        }
        if(!createName || !gamesList || selectedIndex === null) {
        Alert.alert("Must have name and game id!");
        return;
        }
        
        let payload = {
            "joinGameRequest": {
                "name": createName,
                "gameId": gamesList[selectedIndex]
            }
        }
        ws.current.send(JSON.stringify(payload));
    }
      
    return (
      <SafeAreaView style={{ flex: 1 }}>
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
        <Button title="Create Game" onPress={createGame}/>
        <Button title="Join Game" onPress={joinGame}/>
        <Button title="Refresh" onPress={getGames} />
      </SafeAreaView>
    );


}