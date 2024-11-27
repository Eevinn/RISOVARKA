import { fabric } from "fabric";
import { sendShapeMessage } from '../services/socket.js';


export const addRectangle = (canvas) => {
	if (canvas) {
		const rect = new fabric.Rect({
			top: 100,
			left: 200,
			width: 100,
			height: 60,
			fill: "#FF0000",
		});
		canvas.add(rect);
		canvas.renderAll();
		const shapeData = JSON.stringify(rect.toJSON(['id']));
		const shape = {
			shape: shapeData,
			board: { id: parseInt(canvas.boardId) }
		};
		sendShapeMessage(canvas.boardId, 'create', shape);
	}
};


export const addTriangle = (canvas) => {
	if (canvas) {
		const triangle = new fabric.Triangle({
			top: 200,
			left: 100,
			width: 100,
			height: 100,
			fill: "#FF0000",
		});
		canvas.add(triangle);
		canvas.renderAll();
		const shapeData = JSON.stringify(triangle.toJSON(['id']));
		const shape = {
			shape: shapeData,
			board: { id: parseInt(canvas.boardId) }
		};
		sendShapeMessage(canvas.boardId, 'create', shape);
	}
};


export const addLine = (canvas) => {
	if (canvas) {
		const line = new fabric.Line([100, 100, 200, 200], {
			stroke: "#FF0000",
			strokeWidth: 4,
		});
		canvas.add(line);
		canvas.renderAll();
		const shapeData = JSON.stringify(line.toJSON(['id']));
		const shape = {
			shape: shapeData,
			board: { id: parseInt(canvas.boardId) }
		};
		sendShapeMessage(canvas.boardId, 'create', shape);
	}
};


export const addText = (canvas) => {
	if (canvas) {
		const text = new fabric.Textbox("txt", {
			left: 100,
			top: 100,
			width: 200,
			height: 50,
			fontSize: 24,
			fill: "black",
			editable: true,
			textAlign: "center",
		});
		canvas.add(text);
		canvas.renderAll();
		canvas.setActiveObject(text);
		const shapeData = JSON.stringify(text.toJSON(['id']));
		const shape = {
			shape: shapeData,
			board: { id: parseInt(canvas.boardId) }
		};
		sendShapeMessage(canvas.boardId, 'create', shape);
	}
};

export const deleteSelectedObject = (canvas) => {
	const activeObject = canvas.getActiveObject();
	if (activeObject) {
		canvas.remove(activeObject);
		canvas.renderAll();
	}
};


