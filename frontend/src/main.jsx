import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import Board from './pages/Board.jsx'

createRoot(document.getElementById('root')).render(
	<StrictMode>
		<Board />
	</StrictMode>,
)
