import { Rect, Triangle, Line } from "fabric";

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
