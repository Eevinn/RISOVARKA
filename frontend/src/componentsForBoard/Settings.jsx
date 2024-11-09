import React, { useState, useEffect } from "react";
import './Settings.scss';

function Settings({ canvas }) {
	const [selectedObject, setSelectedObject] = useState(null);
	const [color, setColor] = useState("");
	const [isDropdownOpen, setIsDropdownOpen] = useState(false);

	const colors = [
		{ name: "red", value: "#FF0000" },
		{ name: "orange", value: "#FFA500" },
		{ name: "blue", value: "#00BFFF" },
		{ name: "yellow", value: "#FFFF00" },
		{ name: "green", value: "#008000" },
		{ name: "purple", value: "#800080" },
		{ name: "beige", value: "#F5F5DC" },
	];

	useEffect(() => {
		if (canvas) {
			canvas.on("selection:created", (event) => {
				handleObjectSelection(event.selected[0]);
			});

			canvas.on("selection:updated", (event) => {
				handleObjectSelection(event.selected[0]);
			});

			canvas.on("selection:cleared", () => {
				setSelectedObject(null);
				clearSettings();
			});

			canvas.on("object:modified", (event) => {
				handleObjectSelection(event.target);
			});
		}
	}, [canvas]);

	const handleObjectSelection = (object) => {
		if (!object) return;
		setSelectedObject(object);
		if (object.type === "group") {
			setColor(object.item(0).fill);
		} else {
			setColor(object.fill || object.stroke);
		}
	};

	const clearSettings = () => {
		setColor("");
	};

	const handleColorChange = (value) => {
		setColor(value);

		if (selectedObject) {
			if (selectedObject.type === "group") {
				const background = selectedObject.item(0);
				background.set({ fill: value });
			} else if (selectedObject.type === "line") {
				selectedObject.set({ stroke: value });
			} else {
				selectedObject.set({ fill: value });
			}
			canvas.renderAll();
		}
	};

	const toggleDropdown = () => {
		setIsDropdownOpen(!isDropdownOpen);
	};

	return (
		<div>
			{selectedObject && (
				<div>
					<div className="color-selector" onClick={toggleDropdown}>
						<div className="color-circle selected-color" style={{ backgroundColor: color }}></div>
						{isDropdownOpen && (
							<div className="dropdown">
								{colors.map((colorOption) => (
									<div
										key={colorOption.value}
										className={`color-circle ${color === colorOption.value ? 'selected' : ''}`}
										style={{ backgroundColor: colorOption.value }}
										onClick={() => handleColorChange(colorOption.value)}
									></div>
								))}
							</div>
						)}
					</div>
				</div>
			)}
		</div>
	);
}

export default Settings;
