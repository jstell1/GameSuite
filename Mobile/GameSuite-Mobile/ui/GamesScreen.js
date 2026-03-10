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
import { GameContext, API_HOST, WS_HOST, gamesListURI } from '../Global';


export default function GamesScreen({navigation}) {

  const [gamesList, setGamesList] = useState([]);
  const [ selectedIndex, setSelectedIndex ] = useState(null);
  const { setGameChoice } = useContext(GameContext);
  
  useEffect(() => {getGames()}, []);

  const getGames = async () => {
    //console.log("getting the games list");
    //console.log(gamesListURI);
     fetch(gamesListURI)
      .then(resp => resp.json())
      .then(data =>
        {
      //    console.log(data);
          if(data)
            setGamesList(data);
        }
      );
  }

  const getList = async () => {
    if(selectedIndex === null) { Alert.alert("Choose game!"); return;}
    setGameChoice(gamesList[selectedIndex]);
    navigation.navigate("ActiveGamesScreen");
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
        <Button title="Get List" onPress={getList}/>
        <Button title="Refresh" onPress={getGames} />
      </SafeAreaView>
    );
}