package gamehub.snake.model;

import java.nio.file.Path;

import gamehub.model.GameRecord;

public class SnakeGameRecord extends GameRecord {
    @Override
    protected void save() {
        try {
            ensureParentDir();
            Path filePath = getFilePath();


        }
    }
}
