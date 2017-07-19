package othello;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import org.controlsfx.control.StatusBar;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


/**
 * A simple GUI for {@link Game}.
 *
 * @author yolo
 */
public class GUI extends Application
{

    static final String TITLE = "Othello";

    static final String VERSION = "beta1";

    static final String EMAIL = "myEmail[at]provider.com";

    static final String URL = "myRepository.github.com";

    static final String YEAR = "2016";

    static final String AUTHOR = "author";

    private static final long TIMER_WAIT = 100L;

    private static final String PATH_ICON = "icon.png";

    // private static final String PATH_SOUND_TAP = "othello/tap.mp3";

    private static final String PATH_SOUND_TAP = "res\\tap.mp3";

    /**
     * The Othello instance.
     */
    private Game game = new Game();

    /**
     * The current difficulty for player dark.
     */
    private int difficultyDark = 0;

    /**
     * The current difficulty for player light.
     */
    private int difficultyLight = 0;

    /**
     * The tiles of the board.
     */
    private Circle[][] tiles;

    /**
     * Stores the colors used for filling pieces.
     */
    private HashMap<Player, Color> colorsFill;

    /**
     * Stores the colors used for strokes of the pieces.
     */
    private HashMap<Player, Color> colorsStroke;

    /**
     * Text field, which displays the current score of player dark.
     */
    private Text scoreDark;

    /**
     * Text field, which displays the current score of player light.
     */
    private Text scoreLight;

    /**
     * Status bar at the bottom of the window, showing some useful information.
     */
    private StatusBar statusBar;

    /**
     * Menu item for undoing the last move.
     */
    private MenuItem undo;

    /**
     * Menu item for redoing the last undone move.
     */
    private MenuItem redo;
    
    private AI ai = new AI();

    ArrayList<Entry<String, Integer>> difficulties;

    HashMap<Integer, String> difficultyMap;

    /**
     * The application icon.
     */
    private Image icon = new Image(PATH_ICON);

    private Media tap = new Media(new File(PATH_SOUND_TAP).toURI().toString());

    private boolean sound = true;


    // AudioClip tap = new AudioClip(getClass().getResource(PATH_SOUND_TAP).toString());

    /**
     * Refreshes the whole GUI.
     */
    private void refresh()
    {
        // paint all tiles
        for (int x = 0; x < Game.DIM; x++)
        {
            for (int y = 0; y < Game.DIM; y++)
            {
                tiles[x][y].setFill(colorsFill.get(game.getTile(x, y)));
                tiles[x][y].setStroke(colorsStroke.get(game.getTile(x, y)));
            }
        }

        // refresh scoreboard
        scoreDark.setText(Integer.toString(game.getScore(Player.DARK)));
        scoreLight.setText(Integer.toString(game.getScore(Player.LIGHT)));

        // refresh status bar text
        if (game.isGameOver())
        {
            statusBar.setText("Game Over!");
        }
        else
        {
            statusBar.setText("It's " + (game.getCurrentPlayer() == Player.LIGHT ? "Light" : "Dark") + "'s turn.");
        }

        // enable / disable menu item 'undo'
        undo.setDisable(!game.isAnyUndoLeft());

        // enable / disable menu item 'redo'
        redo.setDisable(!game.isAnyRedoLeft());
    }


    /**
     * Initializes the color maps.
     */
    private void initColorMaps()
    {
        colorsFill = new HashMap<Player, Color>();
        colorsFill.put(Player.NONE, Color.TRANSPARENT);
        colorsFill.put(Player.LIGHT, Color.WHITE);
        colorsFill.put(Player.DARK, Color.BLACK);

        colorsStroke = new HashMap<Player, Color>();
        colorsStroke.put(Player.NONE, Color.TRANSPARENT);
        colorsStroke.put(Player.LIGHT, Color.BLACK);
        colorsStroke.put(Player.DARK, Color.BLACK);
    }


    private void initDifficulties()
    {
        difficultyMap = new HashMap<Integer, String>();
        difficultyMap.put(0, "off");
        difficultyMap.put(1, "Apprentice");
        difficultyMap.put(2, "Easy");
        difficultyMap.put(3, "Medium");
        difficultyMap.put(4, "Hard");
        difficultyMap.put(5, "Insane");
    }


    /**
     * Initializes the menu bar.
     */
    private MenuBar initMenuBar()
    {
        MenuItem about = new MenuItem("A_bout");

        about.setOnAction(e -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setGraphic(new ImageView(icon));
            alert.initStyle(StageStyle.UTILITY);
            alert.setTitle("About");
            alert.setHeaderText(TITLE + " " + VERSION + " by " + AUTHOR);
            alert.setContentText("Email: " + EMAIL + '\n' + "Repository: " + URL);
            alert.showAndWait();
        });

        Menu menuHelp = new Menu("_Help", null, about);

        // =========================================================================================
        // =========================================================================================
        // =========================================================================================

        ToggleGroup toggleDifficultyLight = new ToggleGroup();

        Menu menuDifficultyLight = new Menu("_Light Player");

        for (int i = 0; i < difficultyMap.size(); i++)
        {
            RadioMenuItem item = new RadioMenuItem(difficultyMap.get(i));
            item.setUserData(i);
            item.setToggleGroup(toggleDifficultyLight);
            menuDifficultyLight.getItems().add(item);
        }

        ((RadioMenuItem)menuDifficultyLight.getItems().get(difficultyDark)).setSelected(true);

        toggleDifficultyLight.selectedToggleProperty().addListener(new ChangeListener<Toggle>()
        {
            public void changed(ObservableValue< ? extends Toggle> ov, Toggle oldToggle, Toggle newToggle)
            {

                if (newToggle != null)
                {
                    difficultyLight = (Integer)((RadioMenuItem)newToggle).getUserData();
                    runDelayed(() -> doAiMove());
                }
            }
        });

        menuDifficultyLight.getItems().add(1, new SeparatorMenuItem());

        ToggleGroup toggleDifficultyDark = new ToggleGroup();

        Menu menuDifficultyDark = new Menu("_Dark Player");

        // RadioMenuItem offDark = new RadioMenuItem("off");
        // offDark.setToggleGroup(toggleDifficultyDark);
        // difficultyDark.getItems().add(offDark);
        //
        // difficultyDark.getItems().add(new SeparatorMenuItem());

        for (int i = 0; i < difficultyMap.size(); i++)
        {
            RadioMenuItem item = new RadioMenuItem(difficultyMap.get(i));
            item.setUserData(i);
            item.setToggleGroup(toggleDifficultyDark);
            menuDifficultyDark.getItems().add(item);
        }

        ((RadioMenuItem)menuDifficultyDark.getItems().get(difficultyDark)).setSelected(true);

        toggleDifficultyDark.selectedToggleProperty().addListener(new ChangeListener<Toggle>()
        {
            public void changed(ObservableValue< ? extends Toggle> ov, Toggle oldToggle, Toggle newToggle)
            {

                if (newToggle != null)
                {
                    // difficultyDark = (Integer)toggleDifficulty.getSelectedToggle().getUserData();
                    difficultyDark = (Integer)((RadioMenuItem)newToggle).getUserData();
                    runDelayed(() -> doAiMove());
                    // System.out.println("1");
                }

                // if (oldToggle != null && (Integer)oldToggle.getUserData() == 0)
                // {
                // System.out.println("2");
                // doAiDarkMoveIfPossible();
                // }
            }
        });

        // toggleDifficultyDark.selectToggle((RadioMenuItem)menuDifficultyDark.getItems().get(0));;
        //
        menuDifficultyDark.getItems().add(1, new SeparatorMenuItem());

        Menu menuAI = new Menu("_A.I.", null, menuDifficultyLight, menuDifficultyDark);

        // =========================================================================================
        // =========================================================================================
        // =========================================================================================

        RadioMenuItem toggleSound = new RadioMenuItem("_Sound");
        toggleSound.setSelected(true);
        toggleSound.selectedProperty().addListener(new ChangeListener<Boolean>()
        {
            @Override
            public void changed(ObservableValue< ? extends Boolean> ov, Boolean oldVal, Boolean newVal)
            {
                sound = newVal;
            }
        });

        Menu menuOption = new Menu("_Options", null, toggleSound);

        // =========================================================================================
        // =========================================================================================
        // =========================================================================================

        undo = new MenuItem("_Undo");
        undo.setAccelerator(KeyCombination.keyCombination("Ctrl+Z"));
        undo.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                if (game.undo())
                {
                    refresh();
                }
            }
        });

        redo = new MenuItem("_Redo");
        redo.setAccelerator(KeyCombination.keyCombination("Ctrl+Y"));
        redo.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                if (game.redo())
                {
                    refresh();
                }
            }
        });

        Menu menuEdit = new Menu("_Edit", null, undo, redo);

        // =========================================================================================
        // =========================================================================================
        // =========================================================================================

        MenuItem restart = new MenuItem("_New Game");
        restart.setAccelerator(KeyCombination.keyCombination("F2"));
        restart.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                game = new Game();
                refresh();
            }
        });

        MenuItem exit = new MenuItem("E_xit");
        exit.setAccelerator(KeyCombination.keyCombination("Alt+F4"));
        exit.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                Platform.exit();
            }
        });

        Menu menuFile = new Menu("_File", null, restart, new SeparatorMenuItem(), exit);

        // =========================================================================================
        // =========================================================================================
        // =========================================================================================

        MenuBar ret = new MenuBar(menuFile, menuEdit, menuOption, menuAI, menuHelp);
        ret.setUseSystemMenuBar(true);
        ret.useSystemMenuBarProperty().set(true);
        return ret;
    }


    /**
     * Initializes the board.
     */
    private GridPane initBoard()
    {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(5.0));
        grid.setAlignment(Pos.CENTER);
        grid.setGridLinesVisible(true);
        tiles = new Circle[Game.DIM][Game.DIM];

        for (int x = 0; x < Game.DIM; x++)
        {
            for (int y = 0; y < Game.DIM; y++)
            {
                final int xPos = x;
                final int yPos = y;
                Circle c = new Circle(20.0);
                c.setOnMouseClicked(new EventHandler<MouseEvent>()
                {
                    @Override
                    public void handle(MouseEvent event)
                    {
                        if (game.put(xPos, yPos))
                        {
                            if (sound)
                            {
                                new MediaPlayer(tap).play();
                            }
                            refresh();
                            runDelayed(() -> doAiMove());
                        }
                    }
                });
                grid.add(c, x, y);
                tiles[x][y] = c;
                GridPane.setMargin(c, new Insets(5.0));
            }
        }
        return grid;
    }


    /**
     * Initializes the score board.
     */
    private GridPane initScoreBoard()
    {
        GridPane scoreBoard = new GridPane();
        scoreBoard.setPrefWidth(200);

        scoreDark = new Text(Integer.toString(game.getScore(Player.DARK)));
        scoreDark.setFill(Color.WHITE);
        scoreDark.setFont(Font.font("Verdana", FontWeight.BOLD, FontPosture.REGULAR, 40.0));
        BorderPane box1 = new BorderPane(scoreDark);
        box1.setPrefSize(800.0, 50.0);
        box1.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
        box1.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        scoreBoard.add(box1, 0, 0);

        scoreLight = new Text(Integer.toString(game.getScore(Player.LIGHT)));
        scoreLight.setFill(Color.BLACK);
        scoreLight.setFont(Font.font("Verdana", FontWeight.BOLD, FontPosture.REGULAR, 40.0));
        BorderPane box2 = new BorderPane(scoreLight);
        box2.setPrefSize(800.0, 50.0);
        box2.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
        box2.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        scoreBoard.add(box2, 1, 0);

        return scoreBoard;
    }

    // void doAiLightMoveIfPossible()
    // {
    // if (!game.isGameOver() && (difficultyLight > 0) && (game.getCurrentPlayer() == Player.LIGHT))
    // {
    // game = ai.move(game);
    // refresh();
    // switch (game.getCurrentPlayer())
    // {
    // case LIGHT:
    // doAiLightMoveIfPossible();
    // break;
    // case DARK:
    // doAiDarkMoveIfPossible();
    // break;
    // default:
    // throw new IllegalArgumentException();
    // }
    // }
    // }


    // void doAiDarkMoveIfPossible()
    // {
    // if (!game.isGameOver() && (difficultyDark > 0) && (game.getCurrentPlayer() == Player.DARK))
    // {
    // game = ai.move(game);
    // refresh();
    // switch (game.getCurrentPlayer())
    // {
    // case LIGHT:
    // doAiLightMoveIfPossible();
    // break;
    // case DARK:
    // doAiDarkMoveIfPossible();
    // break;
    // default:
    // throw new IllegalArgumentException();
    // }
    // }
    // }

    private void runDelayed(Runnable runnable)
    {
        new Timer().schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                Platform.runLater(runnable);
            }
        }, TIMER_WAIT);
    }


    private void doAiMove()
    {
        if (game.isGameOver())
        {
            return;
        }
        switch (game.getCurrentPlayer())
        {
            case LIGHT:
                if (difficultyLight > 0)
                {
                    game = ai.move(game, difficultyLight);
                    if (sound)
                    {
                        new MediaPlayer(tap).play();
                    }
                    refresh();
                    runDelayed(() -> doAiMove());
                }
                break;
            case DARK:
                if (difficultyDark > 0)
                {
                    game = ai.move(game, difficultyDark);
                    if (sound)
                    {
                        new MediaPlayer(tap).play();
                    }
                    refresh();
                    runDelayed(() -> doAiMove());
                }
                break;
            default:
                throw new IllegalArgumentException();
        }
    }


    @Override
    public void start(Stage primaryStage) throws Exception
    {
        initColorMaps();
        initDifficulties();

        VBox vBox = new VBox(initScoreBoard(), initBoard());
        statusBar = new StatusBar();

        BorderPane border = new BorderPane(vBox);
        border.setTop(initMenuBar());
        border.setBottom(statusBar);

        Scene scene = new Scene(border);
        primaryStage.setTitle("Othello");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(icon);
        refresh();
        primaryStage.show();
    }


    /**
     * Runs the application.
     *
     * @param args unused
     */
    public static void main(String[] args)
    {
        launch(args);
    }

}
