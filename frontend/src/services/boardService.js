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
	await axios.post(`${API_URL}/message`, { text: canvasData });
};

export const sendObjectToBackend = async (object) => {
	try {
		const objectData = object.toObject(); // Преобразуем объект в формат JSON
		const payload = {
			id: objectData.id, // Передаём id объекта
			text: JSON.stringify(objectData) // Передаём объект в текстовом формате
		};

		await axios.post(`${API_URL}/message`, payload); // Отправляем данные на сервер
		console.log("Объект успешно отправлен на сервер:", payload);
	} catch (error) {
		console.error("Ошибка при отправке объекта на сервер:", error);
	}
};

export const putObjectToBackend = async (object) => {
	try {
		const objectData = object.toObject(); // Преобразуем объект в формат JSON
		const id = objectData.id
		console.log(`${API_URL}/message/${id}`, { text: JSON.stringify(objectData) })

		await axios.put(`${API_URL}/message/${id}`, { text: JSON.stringify(objectData) }); // Отправляем данные на сервер
		console.log("Объект успешно отправлен на сервер:", JSON.stringify(objectData)	);
	} catch (error) {
		console.error("Ошибка при отправке объекта на сервер:", JSON.stringify(objectData));
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

