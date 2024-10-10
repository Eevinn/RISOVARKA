import React, { useState, useEffect } from "react";
import { Input } from "blocksin-system";
import "../pages/Board.scss";

function CanvasSettings({ canvas }) {
	const [canvasColor, setCanvasColor] = useState("#ffffff");

	useEffect(() => {
		if (canvas) {
			canvas.set("backgroundColor", canvasColor);
			canvas.renderAll();
		}
	}, [canvasColor, canvas])

	const handleColorChange = (e) => {
		const value = e.target.value;
		setCanvasColor(value);
	};

	return (
		<div>
			<Input
				label="Цвет доски"
				value={canvasColor}
				onChange={handleColorChange}
				type="color" 
			/>
		</div>
	);
}

export default CanvasSettings;