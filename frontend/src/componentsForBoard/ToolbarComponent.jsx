import React from 'react';
import { IconButton } from 'blocksin-system';
import { SquareIcon, TriangleIcon, SlashIcon, TextIcon, StickyNoteIcon, TrashIcon, BorderBottomIcon, BorderLeftIcon } from 'sebikostudio-icons';
import { addRectangle, addTriangle, addLine, addText, deleteSelectedObject } from './Shapes.jsx';
import { addSticker } from './Sticker.jsx';
import Settings from './Settings.jsx';
import './Toolbar.scss';

const ToolbarComponent = ({ canvas, position, onChangePosition }) => {
	return (
		<div className={`toolbar-content ${position}`}>
			<IconButton
					onClick={() => onChangePosition(position === 'left' ? 'bottom' : 'left')}
					variant="ghost"
					size="medium"
			>
					{position === 'left' ? <BorderBottomIcon /> : <BorderLeftIcon />}
			</IconButton>

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

export default ToolbarComponent;
