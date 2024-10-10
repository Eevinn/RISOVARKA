import axios from "axios";

const API_URL = 'http://localhost:8080';

export const saveNameBoard = async (boardName) => {
	const response = await axios.post(`${API_URL}/message`, { text: boardName }, {
		headers: {
			'Content-Type': 'application/json'
		}
	});
	return console.log(boardName);
};