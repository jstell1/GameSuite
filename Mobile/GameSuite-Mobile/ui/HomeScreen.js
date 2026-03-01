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


export default function HomeScreen({navigation, route}) {
  const {createName, setCreateName} = useContext(GameContext);
  const {joinName, setJoinName} = useContext(GameContext);
  const {joinGameId, setJoinGameId} = useContext(GameContext);
  const { currGameId }  = useContext(GameContext);
  const { ws } = useContext(GameContext);
  const { gameBoard } = useContext(GameContext);

//   useEffect(() => {
//   const unsubscribe = subscribe('gameReadyResponse', (gameId) => {
//     setGameId(gameId)
//   });
//   return unsubscribe;
// }, []);

  useEffect(() => {
    if(!gameBoard) return;
    navigation.navigate("GameBoard");
  }, [gameBoard, navigation]);

  const createGame = async () => {
    if(!createName) { Alert.alert("Must have name!"); return; }

    let msg = {
      "createGameRequest": {
          "game": "Checkers",
          "name": createName
      }
    }
    ws.current.send(JSON.stringify(msg));
    
  }

  const joinGame = async () => {
    if(!joinName || !joinGameId) {
      Alert.alert("Must have name and game id!");
      return;
    }
    
    let payload = {
        "joinGameRequest": {
            "name": joinName,
            "gameId": joinGameId
        }
    }
    ws.current.send(JSON.stringify(payload));
  }

  return (

    <SafeAreaView style={{flex: 1}}>
      
       
        <ScrollView 
            contentContainerStyle={styles.container} 
            keyboardShouldPersistTaps="handled"
          >
            <Text selectable={true}>{currGameId}</Text>
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
       
     
    </SafeAreaView>
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
});
