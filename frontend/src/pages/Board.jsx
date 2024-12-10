import React, { useState, useRef, useEffect, useCallback } from 'react';
import './Board.scss';
import { fabric } from 'fabric';
import IconButton from '@mui/material/IconButton';
import SaveIcon from '@mui/icons-material/Save';
import ExitToAppIcon from '@mui/icons-material/ExitToApp';
import { Button, Form, Input } from 'semantic-ui-react';
import ToolbarComponent from '../componentsForBoard/ToolbarComponent.jsx';
import { updateBoard, getBoard } from '../services/boardService.js';
import { getShapesByBoard, addShape, updateShape, deleteShape } from '../services/shapeService.js';
import { useParams, useNavigate } from 'react-router-dom';
import { connectWebSocket, sendShapeMessage, disconnectWebSocket } from '../services/socket.js';
import { v4 as uuidv4 } from 'uuid';

function Board() {
	const { id } = useParams();
	const navigate = useNavigate();
	const canvasRef = useRef(null);
	const canvasInstanceRef = useRef(null);
	const [boardName, setBoardName] = useState("");
	const [toolbarPosition, setToolbarPosition] = useState("left");
	const undoStackRef = useRef([]);
	const clipboardRef = useRef([]);
	const [canUndo, setCanUndo] = useState(false);
	const isUndoRedo = useRef(false);
	const [isCanvasReady, setIsCanvasReady] = useState(false);
	const shapeMap = useRef(new Map());
	const isRemoteUpdate = useRef(false);

	const handleIncomingShapeMessage = (shapeMessage) => {
		const { action, shape } = shapeMessage;
		console.log("WHAT?:", action, shape);
		const canvas = canvasInstanceRef.current;
		if (!canvas) return;
		isRemoteUpdate.current = true;
		switch(action) {
			case 'create':
				fabric.util.enlivenObjects([JSON.parse(shape.shape)], function(enlivenedObjects) {
					enlivenedObjects.forEach((enlivenedObject) => {
						enlivenedObject.set('id', shape.id);
						canvas.add(enlivenedObject);
						canvas.renderAll();
						shapeMap.current.set(shape.id, enlivenedObject);
					});
				});
				break;
			case 'update':
				const targetObject = shapeMap.current.get(shape.id);
				if (targetObject) {
					canvas.remove(targetObject);
				}
				fabric.util.enlivenObjects([JSON.parse(shape.shape)], function(enlivenedObjects) {
					enlivenedObjects.forEach((enlivenedObject) => {
						enlivenedObject.set('id', shape.id);
						canvas.add(enlivenedObject);
						canvas.renderAll();
						shapeMap.current.set(shape.id, enlivenedObject);
					});
				});
				break;
			case 'delete':
				const objToDelete = shapeMap.current.get(shape.id);
				if (objToDelete) {
					canvas.remove(objToDelete);
					canvas.renderAll();
					shapeMap.current.delete(shape.id);
				}
				break;
		}
		isRemoteUpdate.current = false;
	};

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
			const initialState = initCanvas.toJSON();
			undoStackRef.current = [initialState];
			setCanUndo(false);
			initCanvas.boardId = id;

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

			setIsCanvasReady(true);

			initCanvas.on('object:added', saveState);
			initCanvas.on('object:modified', saveState);
			initCanvas.on('object:removed', saveState);

			initCanvas.on('object:added', handleAdd);
			initCanvas.on('object:modified', handleModify);
			initCanvas.on('object:removed', handleRemove);

			return () => {
				initCanvas.dispose();
			};
		}
	}, [id]);

	useEffect(() => {
		const loadBoardAndConnectWebSocket = async () => {
			const board = await getBoard(id);
			setBoardName(board.name);
			await loadBoard();
			connectWebSocket(id, handleIncomingShapeMessage);
			
		};
		loadBoardAndConnectWebSocket();
		return () => {
			disconnectWebSocket();
		};
	}, [id]);

	const loadBoard = async () => {
		const board = await getBoard(id);
		setBoardName(board.name);
		const canvasData = board.text;
		const canvasJSON = JSON.parse(canvasData);
		if (canvasInstanceRef.current) {
			canvasInstanceRef.current.loadFromJSON(canvasJSON, () => {
				canvasInstanceRef.current.renderAll();
				console.log('Доска загружена');
			});
		}
	};

	const handleAdd = async (e) => {
		if (isRemoteUpdate.current) return;
		const obj = e.target;
		if (!obj.id) {
			return;
		}
        console.log(obj.id)
		const shapeData = JSON.stringify(obj.toJSON(['id']));
		const shape = {
            id: obj.id,
			shape: shapeData,
			board: { id: parseInt(id) }
		};
        //sendShapeMessage(parseInt(id),'create',shape);
		//await addShape(shape);
	};

	const handleModify = async (e) => {
			if (isRemoteUpdate.current) return;
			const obj = e.target;
			if (!obj.id) {
				return;
			}
			const shapeData = JSON.stringify(obj.toJSON(['id']));
			const shape = {
                id: obj.id,
				shape: shapeData,
				board: { id: parseInt(id) }
			};
			try {
                sendShapeMessage(parseInt(id),'update',shape)
                console.log(parseInt(id));
				//await updateShape(shape);
			} catch (error) {
				console.error('Ошибка при обновлении фигуры:', error);
			}
	};

	const handleRemove1 = async (e) => {
		if (isRemoteUpdate.current) return;
		const obj = e.target;
		if (!obj.id) {
			return;
		}
		const shapeId = obj.id;
		try {
			await deleteShape(shapeId);
			//shapeMap.current.delete(shapeId);
		} catch (error) {
			console.error('Ошибка при удалении фигуры:', error);
			alert('Не удалось удалить фигуру.');
		}
	};
    	const handleRemove = async (e) => {
    			if (isRemoteUpdate.current) return;
    			const obj = e.target;
    			if (!obj.id) {
    				return;
    			}
    			const shapeData = JSON.stringify(obj.toJSON(['id']));
    			const shape = {
                    id: obj.id,
    				shape: shapeData,
    				board: { id: parseInt(id) }
    			};
    			try {
                    sendShapeMessage(parseInt(id),'delete',shape)
                    console.log(parseInt(id));
    				//await updateShape(shape);
    			} catch (error) {
    							console.error('Ошибка при удалении фигуры:', error);
                    			alert('Не удалось удалить фигуру.');
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

	const handleSave = async () => {
		const canvas = canvasInstanceRef.current;
		const canvasData = JSON.stringify(canvas.toObject());
		await updateBoard(id, boardName, canvasData);
		console.log("Доска сохранена")
	};

	const handleBoardNameChange = (e) => {
		setBoardName(e.target.value);
	};

	const handleGoToAccount = () => {
		window.location.href = 'http://localhost:8080/account';
	};

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

			<div className='settings-wrapper'>
				<Form>
					<label>Название доски:</label>
					<Input
						value={boardName}
						onChange={handleBoardNameChange}
					/>
				</Form>

				<IconButton onClick={handleSave}>
					<SaveIcon />
				</IconButton>
				<IconButton onClick={handleGoToAccount}>
					<ExitToAppIcon />
				</IconButton>
			</div>
		</div>
	);
}

export default Board;
