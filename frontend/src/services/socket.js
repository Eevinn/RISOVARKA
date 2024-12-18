import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

let stompClient = null;

export const connectWebSocket = (boardId, onShapeMessageReceived, onSystemMessageReceived) => {
	const socket = new SockJS('http://localhost:8080/ws-board');
	stompClient = new Client({
		webSocketFactory: () => socket,
		reconnectDelay: 5000,
		debug: (str) => {
			console.log(str);
		},
		onConnect: () => {
			console.log('Connected to WebSocket');
			stompClient.subscribe(`/topic/board/${boardId}/shape`, (message) => {
				const shapeMessage = JSON.parse(message.body);
				if (shapeMessage.action === 'system') {
					onSystemMessageReceived(shapeMessage.message);
				} else {
					onShapeMessageReceived(shapeMessage);
				}
			});
		},
		onStompError: (frame) => {
			console.error('Broker reported error: ' + frame.headers['message']);
			console.error('Additional details: ' + frame.body);
		}
	});

	stompClient.activate();
};

export const sendShapeMessage = (boardId, action, shape) => {
	if (stompClient && stompClient.active) {
		const message = {
			action: action,
			shape: shape
		};
		stompClient.publish({
			destination: `/app/board/${boardId}/shape`,
			body: JSON.stringify(message)
		});
	}
};

export const disconnectWebSocket = () => {
	if (stompClient) {
		stompClient.deactivate();
	}
};
