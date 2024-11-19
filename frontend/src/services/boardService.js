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
    const canvasData = JSON.stringify(canvas.toJSON());
    console.log((canvasData));
    await axios.post(`${API_URL}/message`, { text: canvasData });
    console.log((canvasData));

};
export const  saveCanvasState23 = async (canvas) => {
  try {
    const response = await axios.get(`${API_URL}/message/10`);
    const canvasData = response.data.text; // Предполагаем, что данные находятся в поле 'text'
    // Парсим строку JSON обратно в объект
    const canvasJSON = JSON.parse(canvasData);
    console.log((canvasData))
    // Загружаем состояние холста
    canvas.loadFromJSON(canvasJSON, () => {      // Рендерим холст после загрузки
    canvas.renderAll();    });
    console.log((canvasData))
  } catch (error) {
    console.error('Ошибка при загрузке состояния холста:', error);  }
};
