package gamesuite.server;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.databind.ObjectMapper;

import gamesuite.core.model.GameBoard;
import gamesuite.core.model.Player;
import gamesuite.server.control.ServerGameRepo;
import gamesuite.server.control.SocketConnectionHandler;
import gamesuite.server.control.WebSocketConfig;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

// Extend test with Mockito support
@ExtendWith(MockitoExtension.class)
@SpringBootTest
class SocketConnectionHandlerTest {

	@Mock
	private WebSocketSession mockSession;

	@Mock
	private ServerGameRepo mockRepo;

	@InjectMocks
	private SocketConnectionHandler handler;

	private String game;
	private Set<String> sessions;
	private Set<String> games;
	private Set<String> responses;

	//private WebSocketConfig config;

	@BeforeEach
	void setUp() {
		//handler = new SocketConnectionHandler();
		games = Collections.synchronizedSet(new HashSet<>());
		game = null;
		sessions = Collections.synchronizedSet(new HashSet<>());
		responses = Collections.synchronizedSet(new HashSet<>());

		try {
			doAnswer(invocation -> {
				TextMessage msg = invocation.getArgument(0);
				responses.add(msg.getPayload());

				 try {
	
					ObjectMapper mapper = new ObjectMapper();
					Map<String, Object> resp = mapper.readValue(msg.getPayload(), Map.class);

					assertTrue(resp.containsKey("gameCreatedResponse") || resp.containsKey("gameNotCreatedError"));
					
					synchronized(this) {

						if(resp.containsKey("gameCreatedResponse")) {
							Map<String, Object> resp2 = (Map<String, Object>) resp.get("gameCreatedResponse");
							game = (String) resp2.get("gameId");
							assertEquals(game, "created");
							games.add(game);
						} 
					}
		
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


					return null;
				}).when(mockSession).sendMessage(any());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//config = new WebSocketConfig(handler);
		}

	@Test
	void testCreateGame() throws Exception {

		//Arrange
		lenient().when(mockRepo.hasUserSession(any())).thenAnswer(invocation -> {
			String id = invocation.getArgument(0);
			if(sessions.contains(id))
				return true;
			return false;
		});


		lenient().when(mockRepo.createGame(any(), any(), any())).thenAnswer(invocation -> {
			Player p1 = invocation.getArgument(0);
			GameBoard board = invocation.getArgument(1);
			String sessionId = invocation.getArgument(2);

			if(!sessions.contains(sessionId)) {
				sessions.add(sessionId);
				return "created";
			}
			return "not created";
		});

		String message = """
				{
					"createGameRequest": {
						"name": "Bob"
					}
				}
				""";

		//Act

		Thread t1 = new Thread(() -> {
			runCreateGameTest(message);
		});

		Thread t2 = new Thread(() -> {
			runCreateGameTest(message);
		});

		Thread t3 = new Thread(() -> {
			runCreateGameTest(message);
		});

		Thread t4 = new Thread(() -> {
			runCreateGameTest(message);
		});

		Thread t5 = new Thread(() -> {
			runCreateGameTest(message);
		});

		t1.start();
		t2.start();
		t3.start();
		t4.start();
		t5.start();
		
		t1.join();
		t2.join();
		t3.join();
		t4.join();
		t5.join();
		assertEquals(1, games.size());
	}


	private void runCreateGameTest(String message) {
		ArgumentCaptor<TextMessage> captor = ArgumentCaptor.forClass(TextMessage.class);
		try {
			handler.handleMessage(mockSession, new TextMessage(message));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
