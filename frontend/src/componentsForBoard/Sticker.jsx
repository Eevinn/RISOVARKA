import { fabric } from "fabric";

const sizeOfSticker = (text, rect) => {
	if (text.width > rect.width - 10) {
		rect.set({ width: text.width + 20 });
	}

	if (text.height > rect.height - 10) {
		rect.set({ height: text.height + 20 });
	}

	text.set({
		left: rect.left + 10,
		top: rect.top + 10,
		width: rect.width - 20,
	});

	rect.setCoords();
};

export const addSticker = (canvas) => {
	if (canvas) {
		const padding = 10;

		const stickerBackground = new fabric.Rect({
			width: 200,
			height: 150,
			fill: "#FF0000",
		});

		const stickerText = new fabric.Textbox("txt", {
			width: 180,
			fontSize: 24,
			left: 10,
			top: 10,
			fill: "black",
			textAlign: "center",
			editable: true,
			lockScalingX: true,
			lockScalingY: true,
			hasControls: false,
		});

		const sticker = new fabric.Group([stickerBackground, stickerText], {
			left: 100,
			top: 100,
			lockScalingX: true,
			lockScalingY: true,
			hasControls: false,
		});

		canvas.add(sticker);
		canvas.renderAll();
		canvas.setActiveObject(sticker);

		sticker.on('mousedblclick', () => {
			stickerText.enterEditing();
			canvas.setActiveObject(stickerText);
			stickerText.selectAll();
		});

		stickerText.on('editing:exited', () => {
			sizeOfSticker(stickerText, stickerBackground);
			canvas.renderAll();
		});

		stickerText.on('changed', () => {
			sizeOfSticker(stickerText, stickerBackground);
			canvas.renderAll();
		});
	}
};
