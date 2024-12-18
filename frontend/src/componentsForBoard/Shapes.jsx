import { fabric } from "fabric";
import { sendShapeMessage } from '../services/socket.js';


let currentId = parseInt(localStorage.getItem('currentId')) || 1;
export const saveCurrentId = () => {
	localStorage.setItem('currentId', currentId);
};
export const assignSequentialIdToShape = (shape) => {
	shape.id = currentId++;
	saveCurrentId();
	shape.toObject = (function (toObject) {
		return function () {
			return {
					...toObject.call(this),
					id: this.id,
			};
		};
	})(shape.toObject);
	return shape;
};


export const addRectangle = (canvas) => {
	if (canvas) {
		const rect = new fabric.Rect({
			top: 100,
			left: 200,
			width: 100,
			height: 60,
			fill: "#FF0000",
		});
        assignSequentialIdToShape(rect)
		canvas.add(rect);
		canvas.renderAll();
		const shapeData = JSON.stringify(rect.toJSON(['id']));
		const shape = {
            id: rect.id,
			shape: shapeData,
			board: { id: parseInt(canvas.boardId) }
		};
        console.log(rect.id);
        console.log(shape);
		//sendShapeMessage(canvas.boardId, 'create', shape);
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
        assignSequentialIdToShape(triangle)
		canvas.add(triangle);
		canvas.renderAll();
		const shapeData = JSON.stringify(triangle.toJSON(['id']));
		const shape = {
            id: triangle.id,
			shape: shapeData,
			board: { id: parseInt(canvas.boardId) }
		};
		//sendShapeMessage(canvas.boardId, 'create', shape);
	}
};


export const addLine = (canvas) => {
	if (canvas) {
		const line = new fabric.Line([100, 100, 200, 200], {
			stroke: "#FF0000",
			strokeWidth: 4,
		});
        assignSequentialIdToShape(line)
		canvas.add(line);
		canvas.renderAll();
		const shapeData = JSON.stringify(line.toJSON(['id']));
		const shape = {
            id: line.id,
			shape: shapeData,
			board: { id: parseInt(canvas.boardId) }
		};
		//sendShapeMessage(canvas.boardId, 'create', shape);
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
        assignSequentialIdToShape(text)
		canvas.add(text);
		canvas.renderAll();
		canvas.setActiveObject(text);
		const shapeData = JSON.stringify(text.toJSON(['id']));
		const shape = {
            id: text.id,
			shape: shapeData,
			board: { id: parseInt(canvas.boardId) }
		};
		//sendShapeMessage(canvas.boardId, 'create', shape);
	}
};

export const deleteSelectedObject = (canvas) => {
	const activeObject = canvas.getActiveObject();
	if (activeObject) {
		canvas.remove(activeObject);
		canvas.renderAll();
	}
};


