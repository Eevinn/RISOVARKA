import axios from "axios";

const API_URL = 'http://localhost:8080/board';

export const getBoard = async (id) => {
	const response = await axios.get(`${API_URL}/${id}`, {
		withCredentials: true
	});
	return response.data;
};

export const createBoard = async (name, text) => {
	const response = await axios.post(API_URL, { name, text }, {
		headers: {
			'Content-Type': 'application/json'
		},
		withCredentials: true
	});
	return response.data;
};

export const updateBoard = async (id, name, text) => {
	const response = await axios.put(`${API_URL}/${id}`, { name, text }, {
		headers: {
			'Content-Type': 'application/json'
		},
		withCredentials: true
	});
	return response.data;
};
