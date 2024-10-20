import { Input } from "blocksin-system";
import React, { useState, useEffect } from "react";

function Settings({ canvas }) {
	const [selectedObject, setSelectedObject] = useState(null);
	const [color, setColor] = useState("");

	useEffect(() => {
		if (canvas) {
			canvas.on("selection:created", (event) => {
				handleObjectSelection(event.selected[0]);
				console.log("Выделен объект:", JSON.stringify(event.selected[0], null, 2));
			});

			canvas.on("selection:cleared", () => {
				setSelectedObject(null);
				clearSettings();
				console.log("Выделение снято");
			});

			canvas.on("object:modified", (event) => {
				handleObjectSelection(event.target);
				console.log("Объект изменён:", JSON.stringify(event.target, null, 2));
			});
		}
	}, [canvas]);

	const handleObjectSelection = (object) => {
		if (!object) return;
		setSelectedObject(object);
		if (object.type === "group") {
            const backgroundColor = object.item(0).fill;
            setColor(backgroundColor);
        } else {
		    setColor(object.fill || object.stroke);
		}
	};


	const clearSettings = () => {
		setColor("");
	}

	const handleColorChange = (e) => {
		const value = e.target.value;
		setColor(value);
        if (selectedObject && selectedObject.type === "group") {
            const background = selectedObject.item(0);
            background.set({ fill: value });
        } else if (selectedObject.type === "line") {
            selectedObject.set({ stroke: value });
        } else {
            selectedObject.set({ fill: value });
        }
        canvas.renderAll();
	};


	return (
		<div>
			{selectedObject && (
                <Input
                    label="Цвет объекта"
                    value={color}
                    type="color"
                    onChange={handleColorChange}
                />
			)}
		</div>
	);
}

export default Settings;