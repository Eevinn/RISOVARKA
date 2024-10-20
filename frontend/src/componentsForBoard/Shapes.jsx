import { Rect, Triangle, Line, Textbox, Group } from "fabric";


export const assignIdToShape = (shape, prefix) => {
	shape.id = `${new Date().getTime()}`;
	shape.toObject = (function(toObject) {
		return function() {
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
		const rect = new Rect({
			top: 100,
			left: 200,
			width: 100,
			height: 60,
			fill: "#42D884",
		});
		assignIdToShape(rect);
		canvas.add(rect);
		console.log("прямоугольник", JSON.stringify(rect.toObject()));
	}
};


export const addTriangle = (canvas) => {
	if (canvas) {
		const triangle = new Triangle({
			top: 200,
			left: 100,
			width: 100,
			height: 100,
			fill: "#42D884",
		});
		assignIdToShape(triangle);
		canvas.add(triangle);
		console.log("треугольник", JSON.stringify(triangle.toObject()));
	}
};


export const addLine = (canvas) => {
	if (canvas) {
		const line = new Line([100, 100, 200, 200], {
			stroke: "#42D884",
			strokeWidth: 4,
		});
		assignIdToShape(line);
		canvas.add(line);
		console.log("линия", JSON.stringify(line.toObject()));
	}
};


export const addText = (canvas) => {
    if (canvas) {
        const text = new Textbox("", {
            top: 300,
            left: 100,
            width: 200,
            height: 50,
            fontSize: 20,
            fill: "#42D884",
            textAlign: "center",
            editable: true,
        });
        assignIdToShape(text);
        canvas.add(text);
        canvas.setActiveObject(text);
    }
};


const sizeOfSticker = (text, rect) => {
    if (text.width > rect.width - 20) {
        rect.set({ width: text.width + 20 });
    }
    if (text.height > rect.height - 20) {
        rect.set({ height: text.width + 20 });
    }

    rect.setCoords();
};

export const addSticker = (canvas) => {
    if (canvas) {
        const stickerBackground = new Rect ({
            width: 200,
            height: 150,
            fill: "yellow",
            rx: 20,
            ry: 20,
        });

        const stickerText = new Textbox ("txt", {
           width: 180,
           fontSize: 24,
           left: 10,
           top: 10,
           fill: "black",
           textAlign: "center",
           editable: true,
        });

        const sticker = new Group ([stickerBackground, stickerText], {
           left: 100,
           top: 100,
        });

        canvas.add(sticker);
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