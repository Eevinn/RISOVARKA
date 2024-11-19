import { StrictMode } from 'react';
import React from 'react';
import { createRoot } from 'react-dom/client';
import Board from './pages/Board.jsx';
import ChatRoom from './pages/ChatRoom.jsx';

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <ChatRoom />
  </StrictMode>,
);