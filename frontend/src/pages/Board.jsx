// src/pages/Board.jsx
import React, { useState, useRef, useEffect, useCallback } from 'react';
import './Board.scss';
import { fabric } from 'fabric';
import IconButton from '@mui/material/IconButton';
import SaveIcon from '@mui/icons-material/Save';
import ExitToAppIcon from '@mui/icons-material/ExitToApp';
import { Input } from 'semantic-ui-react';
import ToolbarComponent from '../componentsForBoard/ToolbarComponent.jsx';
import { updateBoard, getBoard } from '../services/boardService.js';
import { connectWebSocket, sendShapeMessage, disconnectWebSocket } from '../services/socket.js';
import Grid from '../componentsForBoard/Grid.jsx';
import { useParams, useNavigate } from 'react-router-dom';

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
  const [isBoardLoaded, setIsBoardLoaded] = useState(false);
  const shapeMap = useRef(new Map());
  const isRemoteUpdate = useRef(false);

  const handleIncomingShapeMessage = (shapeMessage) => {
    const { action, shape } = shapeMessage;
    console.log("Incoming Shape Message:", action, shape);
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
      default:
        console.warn("Unknown action:", action);
    }
    isRemoteUpdate.current = false;
  };

  useEffect(() => {
    if (canvasRef.current) {
      const initCanvas = new fabric.Canvas(canvasRef.current, {
        width: window.innerWidth,
        height: window.innerHeight,
        backgroundColor: "#ffffff",
      });
      canvasInstanceRef.current = initCanvas;
      initCanvas.renderAll();
      setIsCanvasReady(true);
      const initialState = initCanvas.toJSON(['objects']);
      undoStackRef.current = [initialState];
      setCanUndo(false);
      initCanvas.boardId = id;

      const saveState = () => {
        if (!isUndoRedo.current) {
          const currentState = initCanvas.toJSON(['objects']);
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



    
      initCanvas.on('object:added', handleAdd);
      initCanvas.on('object:modified', handleModify);
      initCanvas.on('object:removed', handleRemove);

      // Инициализация панорамирования
      const handlePan = (opt) => {
        const evt = opt.e;
        if (evt.button === 2) { // ПКМ
          initCanvas.isPanning = true;
          initCanvas.lastPosX = evt.clientX;
          initCanvas.lastPosY = evt.clientY;
          initCanvas.selection = false;
          initCanvas.discardActiveObject();
          initCanvas.renderAll();
          console.log("Board: Panning started");
        }
      };

      const handlePanMove = (opt) => {
        if (initCanvas.isPanning) {
          const evt = opt.e;
          const deltaX = evt.clientX - initCanvas.lastPosX;
          const deltaY = evt.clientY - initCanvas.lastPosY;
          initCanvas.lastPosX = evt.clientX;
          initCanvas.lastPosY = evt.clientY;

          const vpt = fabricCanvas.viewportTransform;
          if (vpt) {
            // Создаём новый массив вместо изменения существующего
            const newVpt = vpt.slice(); // или [...vpt]
            newVpt[4] += deltaX;
            newVpt[5] += deltaY;
            try {
              fabricCanvas.setViewportTransform(newVpt);
              fabricCanvas.renderAll();
              console.log(`Board: Panned to (${newVpt[4]}, ${newVpt[5]})`);
            } catch (error) {
              console.error("Board: Error in onMouseMove setViewportTransform", error);
            }
          }
        }
      };

      const handlePanEnd = () => {
        if (initCanvas.isPanning) {
          initCanvas.isPanning = false;
          initCanvas.selection = true;
          console.log("Board: Panning ended");
        }
      };

      return () => {
        initCanvas.dispose();
      };
    }
  }, [id]);

  // Загрузка доски и подключение WebSocket
  useEffect(() => {
    const loadBoardAndConnectWebSocket = async () => {
      try {
        const board = await getBoard(id);
        setBoardName(board.name);
        await loadBoard();
        connectWebSocket(id, handleIncomingShapeMessage);
        setIsBoardLoaded(true);
        console.log("Board: Loaded and WebSocket connected");
      } catch (error) {
        console.error("Board: Error loading board and connecting WebSocket", error);
      }
    };
    loadBoardAndConnectWebSocket();
    return () => {
      disconnectWebSocket();
    };
  }, [id]);

  // Функция загрузки состояния доски
  const loadBoard = async () => {
    try {
      const board = await getBoard(id);
      setBoardName(board.name);
      const canvasData = board.text;
      const canvasJSON = JSON.parse(canvasData);
      if (canvasInstanceRef.current) {
        return new Promise((resolve, reject) => {
          canvasInstanceRef.current.loadFromJSON(canvasJSON, () => {
            // Сбрасываем viewportTransform после загрузки состояния
            canvasInstanceRef.current.setViewportTransform([1, 0, 0, 1, 0, 0]);
            canvasInstanceRef.current.renderAll();
            console.log('Board: Canvas state loaded');
            resolve();
          }, function(o, object) {
            console.log("Board: Loading object:", object);
          });
        });
      } else {
        return Promise.reject("Board: Canvas instance not initialized");
      }
    } catch (error) {
      console.error("Board: Error loading board", error);
      throw error;
    }
  };

  // Обработчики добавления, изменения и удаления фигур
  const handleAdd = async (e) => {
    if (isRemoteUpdate.current) return;
    const obj = e.target;
    if (!obj.id) {
      return;
    }
    console.log(`Board: Object added with id ${obj.id}`);
    const shapeData = JSON.stringify(obj.toJSON(['id']));
    const shape = {
      id: obj.id,
      shape: shapeData,
      board: { id: parseInt(id) }
    };
  };

  const handleModify = async (e) => {
    if (isRemoteUpdate.current) return;
    const obj = e.target;
    if (!obj.id) {
      return;
    }
    console.log(`Board: Object modified with id ${obj.id}`);
    const shapeData = JSON.stringify(obj.toJSON(['id']));
    const shape = {
      id: obj.id,
      shape: shapeData,
      board: { id: parseInt(id) }
    };
    try {
      sendShapeMessage(parseInt(id), 'update', shape);
      console.log(`Board: Sent update for object id ${obj.id}`);
    } catch (error) {
      console.error('Board: Error updating shape:', error);
    }
  };

  const handleRemove = async (e) => {
    if (isRemoteUpdate.current) return;
    const obj = e.target;
    if (!obj.id) {
      return;
    }
    console.log(`Board: Object removed with id ${obj.id}`);
    const shapeData = JSON.stringify(obj.toJSON(['id']));
    const shape = {
      id: obj.id,
      shape: shapeData,
      board: { id: parseInt(id) }
    };
    try {
      sendShapeMessage(parseInt(id), 'delete', shape);
      console.log(`Board: Sent delete for object id ${obj.id}`);
    } catch (error) {
      console.error('Board: Error deleting shape:', error);
    }
  };

  // Обработчики Undo, Copy, Paste
  const handleUndo = useCallback(() => {
    const canvas = canvasInstanceRef.current;
    if (undoStackRef.current.length > 1 && canvas) {
      isUndoRedo.current = true;
      undoStackRef.current.pop();
      const previousState = undoStackRef.current[undoStackRef.current.length - 1];
      canvas.loadFromJSON(previousState, () => {
        // Сбрасываем viewportTransform после загрузки предыдущего состояния
        canvas.setViewportTransform([1, 0, 0, 1, 0, 0]);
        canvas.renderAll();
        isUndoRedo.current = false;
        setCanUndo(undoStackRef.current.length > 1);
        console.log("Board: Undo performed");
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
          } else {
            reject("Board: Cloning object failed");
          }
        });
      });
    }))
    .then(clonedObjects => {
      clipboardRef.current = clonedObjects.filter(obj => obj !== undefined);
      console.log("Board: Objects copied to clipboard");
    })
    .catch(error => {
      console.error("Board: Error copying objects:", error);
    });
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
          } else {
            reject("Board: Cloning object failed during paste");
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
      console.log("Board: Objects pasted from clipboard");
    })
    .catch(error => {
      console.error("Board: Error pasting objects:", error);
    });
  }, []);

  // Обработчик клавиатурных сокращений
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
    try {
      const canvas = canvasInstanceRef.current;
      const canvasData = JSON.stringify(canvas.toObject(['objects']));
      await updateBoard(id, boardName, canvasData);
      console.log("Board: Board saved");
    } catch (error) {
      console.error("Board: Error saving board:", error);
    }
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
      {isCanvasReady && canvasInstanceRef.current && isBoardLoaded && (
        <Grid fabricCanvas={canvasInstanceRef.current} />
      )}
      <div className={`toolbar-wrapper ${toolbarPosition}`}>
        <ToolbarComponent
          canvas={canvasInstanceRef.current}
          position={toolbarPosition}
          onChangePosition={setToolbarPosition}
        />
      </div>

      <div className='settings-wrapper'>
        <div className="settings-container">
          <label className="settings-label">Название доски:</label>
          <Input
            value={boardName}
            onChange={handleBoardNameChange}
            className="board-name-input"
          />
          <IconButton onClick={handleSave} className="settings-icon save-icon">
            <SaveIcon />
          </IconButton>
          <IconButton onClick={handleGoToAccount} className="settings-icon account-icon">
            <ExitToAppIcon />
          </IconButton>
        </div>
      </div>
    </div>
  );
}

export default Board;
