import axios from "axios";

const API_URL = 'http://localhost:8080/shapes';

export const getShapesByBoard = async (boardId) => {
	const response = await axios.get(`${API_URL}/board/${boardId}`, {
		withCredentials: true
	});
	return response.data;
};

export const addShape = async (shape) => {
	const response = await axios.post(`${API_URL}`, shape, {
		headers: {
			'Content-Type': 'application/json'
		},
		withCredentials: true
	});
	return response.data;
};

export const updateShape = async (shape, id) => {
	const response = await axios.put(`${API_URL}/${id}`, shape, {
		headers: {
			'Content-Type': 'application/json'
		},
		withCredentials: true
	});
	return response.data;
};

export const deleteShape = async (id) => {
	const response = await axios.delete(`${API_URL}/${id}`, {
		withCredentials: true
	});
	return response.data;
};
