import React, { useState, useRef, useEffect, useCallback } from 'react';
import './Board.scss';
import { fabric } from 'fabric';
import { IconButton } from 'blocksin-system';
import { FloppyDiskIcon, DownloadIcon } from 'sebikostudio-icons';
import Toolbar from '../componentsForBoard/Toolbar.jsx';
import { saveNameBoard, saveCanvasState } from '../services/boardService.js';
import axios from 'axios';

function Board() {
	const canvasRef = useRef(null);
	const canvasInstanceRef = useRef(null);
	const [boardName, setBoardName] = useState("");
	const [toolbarPosition, setToolbarPosition] = useState("bottom");
	const undoStackRef = useRef([]);
	const clipboardRef = useRef([]);
	const [canUndo, setCanUndo] = useState(false);
	const isUndoRedo = useRef(false);

	useEffect(() => {
		if (canvasRef.current) {
			const initCanvas = new fabric.Canvas(canvasRef.current, {
					width: window.innerWidth,
					height: window.innerHeight,
					backgroundColor: "#ffffff",
					selection: true,
			});
			initCanvas.renderAll();
			canvasInstanceRef.current = initCanvas;
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

			initCanvas.on('object:added', saveState);
			initCanvas.on('object:modified', saveState);
			initCanvas.on('object:removed', saveState);

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
			console.log('Название доски сохранено:', response);
		} catch (error) {
			console.error('Ошибка при сохранении названия доски:', error);
		}
	};

	const handleSaveCanvas = async () => {
		try {
			await saveCanvasState(canvas);
			console.log('Состояние доски сохранено.');
		} catch (error) {
			console.error('Ошибка при сохранении состояния доски:', error);
		}
	};

	const handleLoadCanvas = async () => {
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

	const changeToolbarPosition = (position) => {
		setToolbarPosition(position);
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
				<Toolbar
					canvas={canvasInstanceRef.current}
					position={toolbarPosition}
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

				<div className="toolbar-position">
					<select onChange={(e) => changeToolbarPosition(e.target.value)}>
						<option value="bottom">Снизу</option>
						<option value="left">Слева</option>
					</select>
				</div>
			</div>
		</div>
	);
}

export default Board;