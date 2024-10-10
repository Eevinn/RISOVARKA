import { Input } from "blocksin-system";
import React, { useState, useEffect } from "react";

function Settings({ canvas }) {
	const [selectedObject, setSelectedObject] = useState(null);
	const [color, setColor] = useState("");

	useEffect(() => {
		if (canvas) {
			canvas.on("selection:created", (event) => {
				handleObjectSelection(event.selected[0]);
				console.log("Выделен объект:", JSON.stringify(event.selected[0]));
			});

			canvas.on("selection:cleared", () => {
				setSelectedObject(null);
				clearSettings();
				console.log("Выделение снято");
			});

			canvas.on("object:modified", (event) => {
				handleObjectSelection(event.target);
				console.log("Объект изменён:", JSON.stringify(event.target));
			});
		}
	}, [canvas]);

	const handleObjectSelection = (object) => {
		if (!object) return;

		setSelectedObject(object);
		setColor(object.fill);
		setColor(object.stroke);

		if (object.type === "rect" || object.type === "triangle") {
			setColor(object.fill);
		} else if (object.type === "line") {
			setColor(object.stroke);
		}
		console.log("Цвет объекта:", JSON.stringify(object.fill) || JSON.stringify(object.stroke));
	};


	const clearSettings = () => {
		setColor("");
	}

	const handleColorChange = (e) => {
		const value = e.target.value;
		setColor(value);
		if (selectedObject) {
			if (selectedObject.type === "rect" || selectedObject.type === "triangle") {
				selectedObject.set({ fill: value });
			} else if (selectedObject.type === "line") {
				selectedObject.set({ stroke: value });
			}
			canvas.renderAll();
		}
	};


	return (
		<div>
			{selectedObject && selectedObject.type === "rect" && (
				<>
					<Input
						label="Цвет объекта"
						value={color}
						type="color"
						onChange={handleColorChange}
					/>
				</>
			)}

			{selectedObject && selectedObject.type === "triangle" && (
				<>
					<Input
						label="Цвет объекта"
						value={color}
						type="color"
						onChange={handleColorChange}
					/>
				</>
			)}

			{selectedObject && selectedObject.type === "line" && (
				<>
					<Input
						label="Цвет объекта"
						value={color}
						type="color"
						onChange={handleColorChange}
					/>
				</>
			)}
		</div>
	);
}

export default Settings;