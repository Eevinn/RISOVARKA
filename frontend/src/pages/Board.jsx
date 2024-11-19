import React, { useState, useRef, useEffect, useCallback } from 'react';
import './Board.scss';
import { fabric } from 'fabric';
import { IconButton } from '@mui/material';
import { FloppyDiskIcon, DownloadIcon, SquareIcon } from 'sebikostudio-icons';
import ToolbarComponent from '../componentsForBoard/ToolbarComponent.jsx';
import { saveNameBoard, saveCanvasState, setupCanvasForObjectTracking, sendObjectToBackend } from '../services/boardService.js';
import axios from 'axios';


function Board() {
	const canvasRef = useRef(null);
	const canvasInstanceRef = useRef(null);
	const [boardName, setBoardName] = useState("");
	const [toolbarPosition, setToolbarPosition] = useState("left");
	const undoStackRef = useRef([]);
	const clipboardRef = useRef([]);
	const [canUndo, setCanUndo] = useState(false);
	const isUndoRedo = useRef(false);
	const [isCanvasReady, setIsCanvasReady] = useState(false);

	useEffect(() => {
		if (canvasRef.current) {
			const initCanvas = new fabric.Canvas(canvasRef.current, {
				width: window.innerWidth,
				height: window.innerHeight,
				backgroundColor: "#ffffff",
				selection: true,
			});
			canvasInstanceRef.current = initCanvas;
			initCanvas.renderAll();
			setIsCanvasReady(true);
			setupCanvasForObjectTracking(initCanvas);
			const initialState = initCanvas.toJSON();
			undoStackRef.current = [initialState];
			setCanUndo(false);

			const saveState = () => {
				if (!isUndoRedo.current) {
					const currentState = initCanvas.toJSON();
					undoStackRef.current.push(currentState);
					if (undoStackRef.current.length > 50) {
						undoStackRef.current.shift();
					}
					setCanUndo(undoStackRef.current.length > 1);
				}
			};

			const handleObjectAdded = (e) => {
				const addedObject = e.target;
				if (addedObject && !addedObject._fromServer) { // Проверяем, что объект не был загружен с сервера
					console.log("Добавление объекта:", addedObject);
					canvasInstanceRef.current.renderAll(); // Обязательно перерисовываем канвас
					sendObjectToBackend(addedObject);
				}
			};

			const handleObjectModified = (e) => {
				const modifiedObject = e.target;
				if (modifiedObject) {
					console.log("Изменение объекта:", modifiedObject);
					canvasInstanceRef.current.renderAll(); // Обязательно перерисовываем канвас
					sendObjectToBackend(modifiedObject);
				}
			};


			initCanvas.on("object:added", handleObjectAdded);
			initCanvas.on("object:modified", handleObjectModified);
			setIsCanvasReady(true);

			initCanvas.on('object:added', saveState);
			initCanvas.on('object:modified', saveState);
			initCanvas.on('object:removed', saveState);

			return () => {
				initCanvas.off("object:added", handleObjectAdded);
				initCanvas.off("object:modified", handleObjectModified);
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
			console.log('Название доски сохранено:', response);
		} catch (error) {
			console.error('Ошибка при сохранении названия доски:', error);
		}
	};

	const handleSaveCanvas = async () => {
		try {
			const canvas = canvasInstanceRef.current;
			await saveCanvasState(canvas);
			console.log('Состояние доски сохранено.');
		} catch (error) {
			console.error('Ошибка при сохранении состояния доски:', error);
		}
	};

	const handleLoadCanvas = async () => {
		try {
			const response = await axios.get(`http://localhost:8080/message/24`);
			const canvasData = response.data.text;
			const canvasJSON = JSON.parse(canvasData);
			if (canvasInstanceRef.current) {
				canvasInstanceRef.current.loadFromJSON(canvasJSON, () => {
					canvasInstanceRef.current.renderAll();
					console.log('Доска загружена');
				});
			}
		} catch (error) {
			console.error('Ошибка при загрузке доски: ', error);
		}
	};

	const handleLoadSingleObject = async () => {
		try {
			const response = await axios.get(`http://localhost:8080/message/1732038235684`);
			const objectData = response.data.text;
			const parsedObject = JSON.parse(objectData);

			console.log("Полученный объект:", parsedObject);
			if (canvasInstanceRef.current) {
				let fabricObject;
				switch (parsedObject.type) {
						case "rect":
							fabricObject = new fabric.Rect(parsedObject);
							break;
						case "triangle":
							fabricObject = new fabric.Triangle(parsedObject);
							break;
						case "line":
							fabricObject = new fabric.Line(parsedObject.points, parsedObject);
							break;
						case "textbox":
						case "text":
							fabricObject = new fabric.Textbox(parsedObject.text, parsedObject);
							break;
						default:
							console.error("Неизвестный тип объекта:", parsedObject.type);
							return;
				}

				fabricObject.id = parsedObject.id;
				fabricObject._fromServer = true;
				canvasInstanceRef.current.add(fabricObject);
				canvasInstanceRef.current.renderAll();
				console.log("Объект добавлен на канвас.");
			}
		} catch (error) {
			console.error("Ошибка при загрузке объекта с сервера:", error);
		}
	};



	const handleUndo = useCallback(() => {
		const canvas = canvasInstanceRef.current;
		if (undoStackRef.current.length > 1 && canvas) {
			isUndoRedo.current = true;
			undoStackRef.current.pop();
			const previousState = undoStackRef.current[undoStackRef.current.length - 1];
			canvas.loadFromJSON(previousState, () => {
				canvas.renderAll();
				isUndoRedo.current = false;
				setCanUndo(undoStackRef.current.length > 1);
			});
		}
	}, []);

	const handleCopy = useCallback(() => {
		const canvas = canvasInstanceRef.current;
		const activeObjects = canvas.getActiveObjects();
		Promise.all(activeObjects.map(obj => {
			return new Promise((resolve, reject) => {
				obj.clone(clonedObj => {
					if (clonedObj) {
						resolve(clonedObj);
					}
				});
			});
		}))
		.then(clonedObjects => {
			clipboardRef.current = clonedObjects.filter(obj => obj !== undefined);
		})
	}, []);

	const handlePaste = useCallback(() => {
		const canvas = canvasInstanceRef.current;
		const clipboard = clipboardRef.current;
		const validClipboard = clipboard.filter(obj => obj !== undefined && typeof obj.clone === 'function');
		Promise.all(validClipboard.map(obj => {
			return new Promise((resolve, reject) => {
				obj.clone(clonedObj => {
					if (clonedObj) {
						resolve(clonedObj);
					}
				});
			});
		}))
		.then(pastedObjects => {
			const validPastedObjects = pastedObjects.filter(obj => obj !== undefined);
			validPastedObjects.forEach(obj => {
				obj.set({
					left: obj.left + 10,
					top: obj.top + 10,
					evented: true,
				});
				canvas.add(obj);
				canvas.setActiveObject(obj);
			});
			canvas.renderAll();
		})
	}, []);

	useEffect(() => {
		const handleKeyDown = (e) => {
			if (e.ctrlKey && e.key === 'z') {
				e.preventDefault();
				handleUndo();
			}
			if (e.ctrlKey && e.key === 'c') {
				e.preventDefault();
				handleCopy();
			}
			if (e.ctrlKey && e.key === 'v') {
				e.preventDefault();
				handlePaste();
			}
		};
		window.addEventListener('keydown', handleKeyDown);
		return () => {
			window.removeEventListener('keydown', handleKeyDown);
		};
	}, [handleUndo, handleCopy, handlePaste]);

	return (
		<div className='board'>
			<canvas id='canvas' ref={canvasRef} />
			<div className={`toolbar-wrapper ${toolbarPosition}`}>
				<ToolbarComponent
					canvas={canvasInstanceRef.current}
					position={toolbarPosition}
					onChangePosition={setToolbarPosition}
				/>
			</div>

			<div className='settings-wrapper darkmode'>
				<form className='name-of-board' onSubmit={handleSubmit}>
					<label>
						Название доски:
						<input
							type="text"
							value={boardName}
							onChange={handleChange}
							placeholder="Доска №1"
						/>
					</label>
					<input type="submit" value="Сохранить" />
				</form>

				<IconButton onClick={handleSaveCanvas} variant="ghost" size="medium">
					<FloppyDiskIcon />
				</IconButton>
				<IconButton onClick={handleLoadCanvas} variant="ghost" size="medium">
					<DownloadIcon />
				</IconButton>
				<IconButton onClick={handleLoadSingleObject} variant="ghost" size="medium">
					<SquareIcon />
				</IconButton>
			</div>
		</div>
	);
}

export default Board;