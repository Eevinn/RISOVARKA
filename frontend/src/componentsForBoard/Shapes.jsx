import { fabric } from "fabric";

export const assignIdToShape = (shape) => {
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
		const rect = new fabric.Rect({
			top: 100,
			left: 200,
			width: 100,
			height: 60,
			fill: "#FF0000",
		});
		assignIdToShape(rect);
		canvas.add(rect);
		canvas.renderAll();
		console.log("прямоугольник", JSON.stringify(rect.toObject()));
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
		assignIdToShape(triangle);
		canvas.add(triangle);
		canvas.renderAll();
		console.log("треугольник", JSON.stringify(triangle.toObject()));
	}
};


export const addLine = (canvas) => {
	if (canvas) {
		const line = new fabric.Line([100, 100, 200, 200], {
			stroke: "#FF0000",
			strokeWidth: 4,
		});
		assignIdToShape(line);
		canvas.add(line);
		canvas.renderAll();
		console.log("линия", JSON.stringify(line.toObject()));
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
		assignIdToShape(text);
		canvas.add(text);
		canvas.renderAll();
		canvas.setActiveObject(text);
	}
};

export const deleteSelectedObject = (canvas) => {
	const activeObject = canvas.getActiveObject();
	if (activeObject) {
		canvas.remove(activeObject);
		canvas.renderAll();
	}
};


