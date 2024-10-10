import React, { useState, useRef, useEffect } from 'react';
import './Board.scss';
import { Canvas } from "fabric";
import { IconButton } from 'blocksin-system';
import { SquareIcon, TriangleIcon, SlashIcon } from 'sebikostudio-icons';
import Settings from '../componentsForBoard/Settings.jsx';
import CanvasSettings from '../componentsForBoard/CanvasSettings.jsx';
import { addRectangle, addTriangle, addLine } from '../componentsForBoard/Shapes.jsx'
import { saveNameBoard } from '../services/boardService.js'; 

function Board() {
	const canvasRef = useRef(null);
	const [canvas, setCanvas] = useState(null);
	const [boardName, setBoardName] = useState("");


	useEffect(() => {
		if (canvasRef.current) {
			const initCanvas = new Canvas(canvasRef.current, {
				width: window.innerWidth,
				height: window.innerHeight,
			});
			initCanvas.backgroundColor = "#ffffff";
			initCanvas.renderAll();
			setCanvas(initCanvas);
			return () => {
				initCanvas.dispose();
			};
		}
	}, []);

	const handleChange = (e) => {
		setBoardName(e.target.value);
	};


	const handleSubmit = async (e) => {
		e.preventDefault();
		try {
			const response = await saveNameBoard(boardName);
			console.log('Доска успешно сохранена:', response);
		} catch (error) {
			console.error('Ошибка при сохранении доски:', error);
		}
	};
	

	return (
		<div className='board'>
			<canvas id='canvas' ref={canvasRef} />
			<div className='settings-wrapper darkmode'>
				<Settings canvas={canvas} />
				<CanvasSettings canvas={canvas} />
				<IconButton onClick={() => addRectangle(canvas)} variant="ghost" size="medium">
					<SquareIcon />
				</IconButton>
				<IconButton onClick={() => addTriangle(canvas)} variant="ghost" size="medium">
					<TriangleIcon />
				</IconButton>
				<IconButton onClick={() => addLine(canvas)} variant="ghost" size="medium">
					<SlashIcon />
				</IconButton>
				<form className='name-of-board' onSubmit={handleSubmit}>
					<label>
						Название доски:
						<input 
								type="text" 
								value={boardName} 
								onChange={handleChange} 
								placeholder="Введите название" 
						/>
					</label>
					<input type="submit" value="Сохранить" />
				</form>

			</div>
		</div>
	);
}

export default Board;
