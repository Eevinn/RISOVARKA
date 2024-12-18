import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Board from './pages/Board';

const App = () => {
	return (
		<Router>
			<Routes>
                <Route path="/:id" element={<Board />} />
			</Routes>
		</Router>
	);
}

export default App;
