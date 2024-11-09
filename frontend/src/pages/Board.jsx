import React, { useState, useRef, useEffect } from 'react';
import './Board.scss';
import { Canvas } from "fabric";
import { IconButton, Button } from 'blocksin-system';
import { SquareIcon, TriangleIcon, SlashIcon, TextIcon, StickyNoteIcon } from 'sebikostudio-icons';
import Settings from '../componentsForBoard/Settings.jsx';
import CanvasSettings from '../componentsForBoard/CanvasSettings.jsx';
import { addRectangle, addTriangle, addLine, addText, addSticker } from '../componentsForBoard/Shapes.jsx'
import { saveNameBoard, saveCanvasState } from '../services/boardService.js';
import axios from "axios";

function Board() {
	const canvasRef = useRef(null);
	const canvasInstanceRef = useRef(null)
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
			canvasInstanceRef.current = initCanvas;
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
	

	const handleSaveCanvas2 = async () => {
		try {
			await saveCanvasState32324(canvas);
			console.log('Состояние доски сохранено.');
		} catch (error) {
			console.error('Ошибка при сохранении состояния доски:', error);
		}
	};
   const handleSaveCanvas = async () => {
       try {
           const response = await axios.get(`http://localhost:8080/message/3`);
           const canvasData = response.data.text;
           console.log('Полученные данные:', canvasData);
           console.log('Тип canvasData:', typeof canvasData);

           const canvasJSON = JSON.parse(canvasData);
           console.log('canvasJSON:', canvasJSON);
           console.log('Тип canvasJSON:', typeof canvasJSON);

           console.log('canvasJSON.objects:', canvasJSON.objects);

           if (canvasInstanceRef.current) {
               canvasInstanceRef.current.loadFromJSON(canvasJSON, () => {
                   canvasInstanceRef.current.renderAll();
                   console.log('Состояние доски загружено.');
               });
           } else {
               console.error('Холст не инициализирован.');
           }
       } catch (error) {
           console.error('Ошибка при загрузке состояния доски:', error);
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
                <IconButton onClick={() => addText(canvas)} variant="ghost" size="medium">
                    <TextIcon />
                </IconButton>
                <IconButton onClick={() => addSticker(canvas)} variant="ghost" size="medium">
                    <StickyNoteIcon />
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

                <Button onClick={handleSaveCanvas}>
                    Сохранить доску
                </Button>

			</div>
		</div>
	);
}

export default Board;
