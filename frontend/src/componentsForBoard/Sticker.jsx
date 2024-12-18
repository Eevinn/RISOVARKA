import { fabric } from "fabric";
import { sendShapeMessage } from '../services/socket.js';
import { assignSequentialIdToShape } from "./Shapes.jsx";


const debounce = (func, delay) => {
	let timeout;
	return (...args) => {
		clearTimeout(timeout);
		timeout = setTimeout(() => func(...args), delay);
	};
};

const sizeOfSticker = (text, rect, group) => {
	const padding = 20;
	const newWidth = Math.max(text.width + padding * 2, 100);
	const newHeight = Math.max(text.height + padding * 2, 50);

	rect.set({
		width: newWidth,
		height: newHeight,
	});

	text.set({
		left: rect.left + padding,
		top: rect.top + padding,
		width: newWidth - padding * 2,
		textAlign: "center",
	});

	group.set({
		width: newWidth,
		height: newHeight,
	});

	rect.setCoords();
	text.setCoords();
	group.setCoords();
};

export const addSticker = (canvas) => {
	const stickerBackground = new fabric.Rect({
		width: 100,
		height: 50,
		fill: "#FFEB3B",
		hasControls: false,
		shadow: {
			color: 'rgba(0,0,0,0.3)',
			blur: 10,
			offsetX: 2,
			offsetY: 2,
		},
	});

	const stickerText = new fabric.Textbox("txt", {
		width: 100,
		fontSize: 20,
		fill: "black",
		textAlign: "center",
		editable: true,
		hasControls: false,
	});

	const sticker = new fabric.Group([stickerBackground, stickerText], {
		left: 100,
		top: 100,
		hasControls: false,
		lockScalingFlip: true,
		hasControls: false,
	});

	assignSequentialIdToShape(sticker);
	canvas.add(sticker);
	canvas.renderAll();
	canvas.setActiveObject(sticker);
	const saveSticker = debounce(() => {
		const shapeData = JSON.stringify(sticker.toJSON(['id']));
		const shape = {
			id: sticker.id,
			shape: shapeData,
			board: { id: parseInt(canvas.boardId) },
		};
		sendShapeMessage(canvas.boardId, 'update', shape);
	}, 1000);

	sticker.on('mousedblclick', () => {
		stickerText.enterEditing();
		canvas.setActiveObject(stickerText);
		stickerText.selectAll();
		canvas.renderAll();
	});

	stickerText.on('changed', () => {
		sizeOfSticker(stickerText, stickerBackground, sticker);
		canvas.renderAll();
	});

	stickerText.on('editing:exited', () => {
		sizeOfSticker(stickerText, stickerBackground, sticker);
		canvas.renderAll();
		saveSticker();
	});

	sticker.on('modified', () => {
		canvas.renderAll();
		saveSticker();
	});

	const shapeData = JSON.stringify(sticker.toJSON(['id']));
	const shape = {
		id: sticker.id,
		shape: shapeData,
		board: { id: parseInt(canvas.boardId) },
	};
	sendShapeMessage(canvas.boardId, 'create', shape);
};