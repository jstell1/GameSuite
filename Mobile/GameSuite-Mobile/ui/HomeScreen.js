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
import { FlatList } from 'react-native-web';


export default function HomeScreen({navigation, route}) {
 
  const { currGameId } = useContext(GameContext);
  const {createName, setCreateName} = useContext(GameContext);
//   useEffect(() => {
//   const unsubscribe = subscribe('gameReadyResponse', (gameId) => {
//     setGameId(gameId)
//   });
//   return unsubscribe;
// }, []);
  //console.log(currGameId);
  
  const getGamesList = async () => {
    if(!createName) { Alert.alert("Must have name!"); return; }
    navigation.navigate("GamesScreen");
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
            <Button title="Choose Game" onPress={getGamesList}/>
            <StatusBar style="auto" />
          </ScrollView>
       
     
    </SafeAreaView>
  );

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
