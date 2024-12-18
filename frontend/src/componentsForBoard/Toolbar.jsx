import React from 'react';
import { IconButton } from 'blocksin-system';
import { SquareIcon, TriangleIcon, SlashIcon, TextIcon, StickyNoteIcon, UndoIcon, ReloadIcon, TrashIcon } from 'sebikostudio-icons';
import { addRectangle, addTriangle, addLine, addText, deleteSelectedObject } from '../componentsForBoard/Shapes.jsx';
import { addSticker } from './Sticker.jsx';
import Settings from './Settings.jsx';
import './Toolbar.scss';

const Toolbar = ({ canvas, position }) => {

	return (
		<div className={`toolbar-content ${position}`}>
			<Settings canvas={canvas} />
			<IconButton onClick={() => addRectangle(canvas)} variant="ghost" size="medium">
				<SquareIcon />
			</IconButton>
			<IconButton onClick={() => addTriangle(canvas)} variant="ghost" size="medium">
				<TriangleIcon />
			</IconButton>
			<IconButton onClick={() => addLine(canvas)} variant="ghost" size="medium">
				<SlashIcon />
			</IconButton>
			<IconButton onClick={() => addText(canvas)} variant="ghost" size="medium">
				<TextIcon />
			</IconButton>
			<IconButton onClick={() => addSticker(canvas)} variant="ghost" size="medium">
				<StickyNoteIcon />
			</IconButton>
			<IconButton onClick={() => deleteSelectedObject(canvas)} variant="ghost" size="medium">
				<TrashIcon />
			</IconButton>
		</div>
	);
};

export default Toolbar;
