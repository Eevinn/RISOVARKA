package backend.services;

import backend.model.Board;
import backend.repo.BoardRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BoardService {

    @Autowired
    private BoardRepo boardRepo;

    public void updateCanvas(String boardId, String canvasData) {
        Optional<Board> optionalBoard = boardRepo.findById(Integer.parseInt(boardId));
        if (optionalBoard.isPresent()) {
            Board board = optionalBoard.get();
            board.setText(canvasData);
            boardRepo.save(board);
        }
    }

    public String getCanvasData(String boardId) {
        Optional<Board> optionalBoard = boardRepo.findById(Integer.parseInt(boardId));
        return optionalBoard.map(Board::getText).orElse("{}");
    }

    public void deleteBoard(int boardId) {
        boardRepo.deleteById(boardId);
    }
}
