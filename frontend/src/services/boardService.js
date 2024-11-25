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

export const saveCanvasState = async(canvas) => {
	const canvasData = JSON.stringify(canvas.toObject());
	await axios.post(`${API_URL}/message`, { text: canvasData, id:23});
};

export const sendObjectToBackend = async (object) => {
	try {
		const objectData = object.toObject();
		const payload = {
			id: objectData.id,
			text: JSON.stringify(objectData)
		};
		await axios.post(`${API_URL}/message`, payload);
		console.log("Объект успешно отправлен на сервер:", payload);
	}
};


export const setupCanvasForObjectTracking = (canvas) => {
	if (!canvas) return;
	canvas.on("object:added", (e) => {
		const addedObject = e.target;
		if (addedObject) {
			sendObjectToBackend(addedObject);
		}
	});
	console.log("Настройка отслеживания объектов завершена");
};

