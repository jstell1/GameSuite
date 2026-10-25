import {useContext, useEffect, useRef, useState} from "react";
import {API_HOST, GameContext} from "../../Global";
import {Button, Image, Dimensions, Platform, Pressable, StyleSheet, Text, View, Modal, FlatList} from "react-native";



const { width } = Dimensions.get('window');
const BOARD_SIZE = Platform.OS === "web" ? Math.min(width * 0.8, 480) : width;
const SCALE = BOARD_SIZE / 768;
const TOP_BORDER_RATIO = 30/ 768;
const BORDER = 58*SCALE;
const TOP_BORDER = 55*SCALE;
const INNER_SIZE = BOARD_SIZE - 2*BORDER;
const squareSize = 82*SCALE;

export default function StaticGameBoard({navigation, route}) {
    const { teamNames } = useContext(GameContext);
    const {gameBoard, setGameBoard } = useContext(GameContext);
    const { resetSocket} = useContext(GameContext);
    const [board,setBoard] = useState(null); //() => initBoardData(gameBoard));
    const [highlights, setHighlights] = useState([]);
    const [numClicks, setNumClicks] = useState(0);
    const [start, setStart] = useState(null);
    const {game, setGame} = useContext(GameContext);
    const { isClickable, setIsClickable } = useContext(GameContext);
    const {playerTurn} = useContext(GameContext);
    const {gameTurn } = useContext(GameContext); //string for displaying on screen
    const { ws } = useContext(GameContext);
    const { currGameId, setCurrGameId } = useContext(GameContext);
    const { gameUI, setGameUI } = useContext(GameContext);
    const { selectedIndex, setSelectedIndex} = useContext(GameContext);
    const { gameChoice, setGameChoice } = useContext(GameContext);
    const [ boardUI, setBoardUI ] = useState("");
    const team1 = useRef(new Map());
    const team2= useRef(new Map());
    const { promoteList, setPromoteList } = useContext(GameContext);
    const { gameId} = useContext(GameContext);
    const over = useRef(false);

    useEffect(() => {
        if(!game) return;
        console.log("curr game turn: " + game.turn);

        if(game.turn === playerTurn.current) {
            console.log("my turn");
            setIsClickable(true);
        }
    }, [playerTurn, game]);

    async function getBoardDef() {
        const resp = await fetch(`${API_HOST}/boardDef/${gameChoice}Board.json`);
        //c//onsole.log(resp);
        //console.log(resp.json());
        return resp.json();
    }

    useEffect(() => {
        async function start() {
            console.log(gameBoard);
            if(!gameBoard || over.current) return;
            over.current = true;
            console.log("not supposed to be here if null");

            const boardDef = await getBoardDef();
            setBoardUI(`${API_HOST}/${boardDef.board}`);
            const tmp1 = boardDef.team1.map(piece => [piece.name, `${API_HOST}/${piece.path}`]);
            const tmp2 = boardDef.team2.map(piece => [piece.name, `${API_HOST}/${piece.path}`]);
            team1.current = new Map(tmp1);
            console.log(team1);
            team2.current = new Map(tmp2);
            console.log(team2);
            setBoard(() => initBoardData(gameBoard));
        }
        start();
    }, [gameBoard]);


    useEffect(() => {
        navigation.setOptions({
            headerLeft: () => (
                <Button
                    title="Home"
                    onPress={async () => {
                        await ws.current.close();

                        //await resetSocket();
                        await navigation.popToTop();
                        playerTurn.current = 0;
                        setGame(null);
                        setGameBoard(null);
                        setGameUI("");
                        setSelectedIndex(null);
                        setGameChoice("");
                        setBoardUI("");
                        team1.current = new Map();
                        team2.current = new Map();

                        setCurrGameId("Create or Join Game");
                        over.current = false;
                    }}
                />
            )
        });
    }, [navigation, resetSocket]);

    useEffect(() => {
        //console.log("updating board");
        if (game?.changedPos) {
            applyChanges(game.changedPos);
        }
    }, [game]);



    function applyChanges(changedPos) {
        if(!board) return;
        console.log("applying changes");
        const newBoard = board.map(row => [...row]);
        changedPos.forEach((pos) => {
            if(pos.piece !== null) {
                let color;
                const team = pos.piece.team;
                const type = pos.piece.type;
                if(pos.piece.team === teamNames[1])
                    color = team2.current.get(type);
                else
                    color = team1.current.get(type);

                newBoard[pos.x][pos.y] = color;
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
                let name = tmp != null ? tmp["team"] : null;
                let type = tmp != null ? tmp["type"]: null;
                if(name != null && name === teamNames[0])
                    piece = team1.current.get(type);
                else if(name != null && name === teamNames[1])
                    piece = team2.current.get(type);

                rowArr.push(piece);
            }
            arr.push(rowArr);
        }
        return arr;
    }

    async function handlePress(row, col) {
        console.log("in handlePress");
        if(isClickable === false) return;
        console.log("isClickable");
        if (numClicks === 0) {
            setStart({ row, col });
            setHighlights([{ row, col }]);
            setNumClicks(1);
        } else if (numClicks === 1) {
            const end = { row, col };

            //console.log("Move:", start, "->", end);
            let movMessage;
            //console.log(currGameId);
            try {

                movMessage = {
                    moveRequest: {
                        gameId: currGameId,
                        move: {
                            startX: highlights[0].row,
                            startY: highlights[0].col,
                            endX: end.row,
                            endY: end.col
                        }
                    }
                };
            } catch (error) {
                console.log(error);
            }
            //console.log(movMessage);
            //console.log("right after");
            setHighlights([]);
            setNumClicks(0);
            setStart(null);
            setIsClickable(false);
            //console.log("sending");
            ws.current.send(JSON.stringify(movMessage));
            // console.log("sent");
        }
    }
    return (
        <View style={styles.container}>
            <Modal
                transparent
                animationType="slide"
                visible={promoteList !== null}
                onRequestClose={() => setPromoteList(null)}
            >
                <View style={styles.backdrop}>
                    <FlatList
                        style={{ flexGrow: 0, width: '100%' }}
                        contentContainerStyle={{ alignItems: 'center' }}
                        data={promoteList}
                        keyExtractor={item => item}
                        renderItem={({ item }) => (
                            <Pressable
                                style={styles.button}
                                onPress={() => {
                                    const payload = {
                                        promoteChoice: {
                                            gameId: currGameId,
                                            choice: item
                                        }
                                    };
                                    ws.current.send(JSON.stringify(payload));
                                    setPromoteList(null);
                                }}
                            >
                                <Text>{item}</Text>
                            </Pressable>
                        )}
                    />
                </View>
            </Modal>

            <Text>{gameTurn}</Text>
            <View style={{width: BOARD_SIZE, height: BOARD_SIZE}}>

                <Image
                    style={{width: BOARD_SIZE, height: BOARD_SIZE}}
                    source={{
                        uri:boardUI
                    }}
                ></Image>

                <View style={styles.board}>
                    {board ? board.map((rowArr, row) =>
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
                    ): <Text>Loading...</Text>}
                </View>
            </View>


        </View>
    );
}

function Square({ row, col, piece, onPress, highlighted }) {

    return (
        <Pressable
            onPress={() => onPress(row, col)}
            style={[
                styles.square,
                {left: col*squareSize, top:row*squareSize},
                highlighted && styles.yellow,
            ]}
        >
            {piece && (
                <Image
                    source={{uri: piece}}
                    style={{
                        position: "absolute",
                        left: 3*SCALE,
                        top: 5*SCALE,
                         width: 70*SCALE,
                        height: 70*SCALE
                    }}
                    resizeMode="contain"
                ></Image>
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
    backdrop: {
        flex: 1,
        backgroundColor: 'rgba(0, 0, 0, 0.5)',  // dims the whole screen
        alignItems: 'center',
        justifyContent: 'center',
    },
    button: {
        backgroundColor: 'rgba(255, 255, 255, 0.92)',  // slightly translucent
        borderRadius: 12,
        padding: 20,
        width: 240,
        marginBottom: 10,
        alignItems: 'center',
    },
    input: {
        height: 40,
        width: 300,
        margin: 12,
        borderWidth: 1,
        padding: 10,
    },
    board: {
        position: "absolute",
        left: BORDER,
        top: TOP_BORDER,
        //aspectRatio: 1,
        width: 8*squareSize,
        height: 8*squareSize
    },
    square: {
        position: "absolute",
        height: squareSize,
        width: squareSize
    },
    dark: { backgroundColor: "saddlebrown" },
    light: { backgroundColor: "lightgray" },
    yellow: { backgroundColor: "rgba(255,255,0,0.35)" },
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
