import React, { useEffect, useRef } from 'react';
import * as d3 from 'd3';
import PropTypes from 'prop-types';

const Grid = ({ fabricCanvas }) => {
const gridRef = useRef(null);

useEffect(() => {
	if (!fabricCanvas) return;
		const gridSize = 50;
		const width = fabricCanvas.getWidth();
		const height = fabricCanvas.getHeight();

		const svg = d3.select(gridRef.current)
		.attr("width", width)
		.attr("height", height)
		.style("position", "absolute")
		.style("top", "0")
		.style("left", "0")
		.style("pointer-events", "none")
		.style("z-index", "-1");

		for (let y = 0; y < height; y += gridSize) {
		svg.append("line")
			.attr("x1", 0)
			.attr("y1", y)
			.attr("x2", width)
			.attr("y2", y)
			.attr("stroke", "#e0e0e0")
			.attr("stroke-width", 1);
		}

		for (let x = 0; x < width; x += gridSize) {
		svg.append("line")
			.attr("x1", x)
			.attr("y1", 0)
			.attr("x2", x)
			.attr("y2", height)
			.attr("stroke", "#e0e0e0")
			.attr("stroke-width", 1);
		}

	return () => {
		d3.select(gridRef.current).selectAll("*").remove();
		};

}, [fabricCanvas]);

return <svg ref={gridRef}></svg>;
};

Grid.propTypes = {
	fabricCanvas: PropTypes.object.isRequired,
};

export default Grid;
