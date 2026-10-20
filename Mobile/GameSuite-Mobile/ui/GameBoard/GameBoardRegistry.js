import {useContext} from "react";
import {GameContext} from "../../Global";
import GameBoardPanel from "./GameBoardPanel";
import { useEffect, useState, useContext } from 'react';

export default function GameBoardRegistry({navigation, route}) {
    const { gameUI } = useContext(GameContext);
    const { gameBoard, setGameboard } = useContext(GameContext);
    const { game, setGame} = useContext(GameContext);

    const registry = {
            GameBoardPanel: GameBoardPanel
    };

    useEffect(() => {
        navigation.navigate(registry[gameUI]);
        //setGame(payload.gameState);
    }, [gameUI, gameBoard, navigation]);

}