package org.openjfx.GameController;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;

import org.openjfx.GameComponent.*;
import org.openjfx.GameController.InteractionManager.InteractionHandler;
import org.openjfx.GameController.MenuManager.EndGameMenu;
import org.openjfx.GameController.MenuManager.InGameMenu;
import org.openjfx.GameController.InteractionManager.InteractionHandler;
import org.openjfx.GameController.MenuManager.ScoreMenu;
import org.openjfx.SceneryManager.Scenery;
import org.openjfx.SystemInfo;

import java.util.ArrayList;

public class GameController {
    private Scenery scenery; // scenery of the game
    private Pane gameRoot; // root of the game
    private GameComponentFactory gameComponentFactory; // Factory for components
    private ArrayList<GameComponent> gameComponents; // array list for updating components
    private InteractionHandler interactionHandler; // interaction handler for controller
    private double width; // width of the game
    private double height; // height of the game
    private Player player; // player variable for easier access
    private double speed = 15; // players speed
    private double maxSpeed = 25; // players maximum speed
    private double acceleration = 0.3; // screens acceleration todo add acceleration
    private int speedFactor = 1;
    // Necessary attiributes for changing directions with the ship
    boolean toLeft = true; // if facing left true else false. Starts with false;
    boolean startSlidingLeft = false; // slides background to left
    boolean startSlidingRight = false; // slides background to right
    double slidingLimit; // sliding limit for bacground
    double slidingCounter; // sliding counter for background
    double slidingSpeed; // sliding speed for background
    // level counter
    int level = 6; // Using mod 5  for infinite levels so need to start at 1
    int deadCounter = 0;
    //long score = 0;
    int noOfEnemies = 0;
    int selectShipNumber;
    int currentScreen = 0;
    // BooleanProperties for smoother control on ui.
    private BooleanProperty[] keyInputs = new BooleanProperty[14];

    private boolean isMenuCreated = false;
    private InGameMenu inGameMenu;
    private EndGameMenu endGameMenu;
    private Minimap minimap;


    int score = 0;
    MainGame mainGame;

    private boolean isCounterStarted = false;
    private double startTime;
    private boolean timeFreeze;

    ScoreMenu scoreMenu;

    double firstTime = 0; // hold the initial time.
    double lastTime = System.nanoTime() / 1000000000.0; // helps calculate time
    double passedTime = 0; // helps calculate fps
    double totalPassedTime = 0; // hold unprocessed time of the game.
    double totalPassedTime_2 = 0; // hold unprocessed time of the game.
    boolean slowedDownTime = false;
    /*
    For key inputs
    index — key
    0 — w
    1 — a
    2 — s
    3 — d
    4 — enter
    5 — space
    6 — q
    7 — e
    8 — h
    9 — j
    10 — k
    11 — l
     */

    /**
     * Constructor of GameController, GameController is the function for
     * controlling and creating levels
     * @param root root is the pane of the game we created
     * @param width width of the screen
     * @param height height o the screen
     * @param mainGame maingame is the component that creates the scenery and launches the game
     */
    GameController(Pane root, double width, double height, MainGame mainGame) {
        this.gameRoot = root;
        this.width = width;
        this.height = height;
        this.mainGame = mainGame;
    }

    /**
     *  createContent function is the function for creating the in-game objects such as
     *  player and enemies and hold that in an array called gamecomponents
     */
    void createContent() {
        speed = 0;
        maxSpeed = magicConverter(25); // If width = 1920 then maxSpeed = 25.
        acceleration = magicConverter(0.3);
        scenery = new Scenery(gameRoot, width, height, speed); // first create scenery
        scenery.createContent(); // create its content
        gameComponents = new ArrayList<>(); // create arraylist for gameComponents
        gameComponentFactory = new GameComponentFactory(width, height, gameComponents); // Initiate factory
        gameComponentFactory.setSelectShip(selectShipNumber);
        player = (Player) gameComponentFactory.createComponent("player"); // first game component is Player
        player.setSpeed(speed); // set speed for player.
        player.addShapes(gameRoot); // add player to root
        interactionHandler = new InteractionHandler();
        gameRoot.setTranslateX(width); // set starting camera
        slidingLimit = width - player.getWidth() * 7;
        slidingCounter = slidingLimit * -1;
        slidingSpeed = (width - player.getWidth() * 5) / 65; // some numbers yes.

        inGameMenu = new InGameMenu(scenery);
        inGameMenu.createButtons(gameRoot);

        endGameMenu = new EndGameMenu(scenery);
        endGameMenu.createButton(gameRoot);

        scoreMenu = new ScoreMenu(scenery);
        scoreMenu.createScoreScreen(gameRoot);

        isMenuCreated = true;

        minimap = new Minimap();
    }

    /**
     * handles interaction such as collisions of bullets
     */
    void updateInteraction() {
        //update interaction
        interactionHandler.handleInteraction(gameRoot, player);
    }

    /**
     *  update game is the function for updating the game's components every time in the game
     *  the arraylist called gamecomponents is accessed here in order to control every in game object
     *  the in-game components also got removed from game here
     * @param fps is the frame per second of the launched game
     */
    void updateGame(int fps) {
        if(timeFreeze){
            firstTime = System.nanoTime() / 1000000000.0; // get time
            passedTime = firstTime - lastTime; // calculate passedTime
            lastTime = firstTime; // reset last time.
            totalPassedTime += passedTime; // calculate total passed time
            totalPassedTime_2 += passedTime;
            if (totalPassedTime > 0.5) { // if 1 second is passed
                totalPassedTime = 0; // reset timer
                slowedDownTime =true;
            }
            if(totalPassedTime_2 > 5.0) {
                totalPassedTime_2 = 0;
                totalPassedTime = 0;
                timeFreeze = false;
            }
        } else
            slowedDownTime = false;
        if (currentScreen == 0) {
            // update game components
            if(!timeFreeze || slowedDownTime) {
                int size = gameComponents.size();
                for (int i = 0; i < size; i++) { // for every component in gameComponents.
                    if (gameComponents.get(i) instanceof Player) { // if its an instance class of Player.
                        ((Player) gameComponents.get(i)).movePlayer(keyInputs, gameComponentFactory, this); // update it.
                        if (player.isDead()) { // if player is dead.
                            gameComponents.remove(i--); // remove it from components.
                            size -= 1; // decrease size.
                            player.die(); // kill it, remove it from root.
                            setCurrentScreen(3);
                        }
                    } else if (gameComponents.get(i) instanceof PlayerBullet) { // else if its an instance class of PlayerBullet.
                        PlayerBullet playerBullet = (PlayerBullet) gameComponents.get(i); // cast it to a temporary variable.
                        playerBullet.movePlayerBullet(); // update it.
                        // If its outside of the current camera/root location.
                        if (playerBullet.getX() > (gameRoot.getTranslateX() * -1) + width + playerBullet.getWidth() ||
                                playerBullet.getX() < (gameRoot.getTranslateX() * -1) - playerBullet.getWidth()) {
                            gameComponents.remove(i--); // remove it from gameComponents.
                            size -= 1; // decrease size.
                            playerBullet.die(); // kill it, remove it from root.
                        } else if (playerBullet.isDead()) {
                            gameComponents.remove(i--); // remove it from gameComponents.
                            size -= 1; // decrease size.
                            playerBullet.die(); // kill it, remove it from root.
                        }
                    } else if (gameComponents.get(i) instanceof EnemySelfDestruct) { // else if its an instance class of EmenyType1.
                        EnemySelfDestruct enemySelfDestruct = ((EnemySelfDestruct) gameComponents.get(i));
                        enemySelfDestruct.updateSelfDestruct(); // update it.
                        if (enemySelfDestruct.isDead()) { // if enemyType1 is dead.
                            gameComponents.remove(i--); // remove it from components.
                            size -= 1; // decrease size.
                            enemySelfDestruct.die(); // kill it, remove it from root.
                        }
                    } else if (gameComponents.get(i) instanceof Shield) {
                        Shield shield = ((Shield) gameComponents.get(i));
                        shield.moveShield(player);
                        if (shield.isDead()) {
                            gameComponents.remove(i--); // remove it from components.
                            size -= 1; // decrease size.
                            shield.die(); // kill it, remove it from root.
                        }
                    } else if (gameComponents.get(i) instanceof Barrier) {
                        Barrier item = ((Barrier) gameComponents.get(i));
                        item.moveBarrier(scenery);
                    } else if (gameComponents.get(i) instanceof Bomb) {
                        Bomb bomb = ((Bomb) gameComponents.get(i));
                        bomb.moveBomb();
                        if (bomb.isDead()) {
                            gameComponents.remove(i--);
                            size -= 1;
                            bomb.explode(gameComponentFactory);
                            bomb.die();
                        }
                    } else if (gameComponents.get(i) instanceof Collectible) {
                        Collectible item = ((Collectible) gameComponents.get(i));
                        item.moveCollectible();
                        if (item.isDead()) {
                            gameComponents.remove(i--);
                            size -= 1;
                            item.die();
                        }
                    } else if (gameComponents.get(i) instanceof Melee) {
                        Melee melee = ((Melee) gameComponents.get(i));
                        melee.moveMelee(player);
                        if (melee.isDead()) {
                            gameComponents.remove(i--);
                            size -= 1;
                            melee.die();
                        }
                    } else if (gameComponents.get(i) instanceof Atlas) { // else if its an instance class of EmenyType1.
                        Atlas atlas = ((Atlas) gameComponents.get(i));
                        atlas.moveAtlas(gameComponentFactory, gameRoot, player, keyInputs[1].get(), speedFactor); // update it.
                        if (atlas.isDead()) { // if enemyType1 is dead.
                            gameComponents.remove(i--); // remove it from components.
                            size -= 1; // decrease size.
                            atlas.die(); // kill it, remove it from root.
                            deadCounter++;
                            player.setScore(player.getScore() + 100);
                        }
                    } else if (gameComponents.get(i) instanceof Dodger) { // else if its an instance class of EmenyType1.
                        Dodger dodger = ((Dodger) gameComponents.get(i));
                        dodger.update(gameComponentFactory, gameRoot, player, keyInputs[1].get(), speedFactor); // update it.
                        if (dodger.isDead()) { // if enemyType1 is dead.
                            gameComponents.remove(i--); // remove it from components.
                            size -= 1; // decrease size.
                            dodger.die(); // kill it, remove it from root.
                            deadCounter++;
                            player.setScore(player.getScore() + 100);
                        }
                    } else if (gameComponents.get(i) instanceof Dividus) { // else if its an instance class of EmenyType1.
                        Dividus dividus = ((Dividus) gameComponents.get(i));
                        dividus.update(gameComponentFactory, gameRoot, player, keyInputs[1].get(), speedFactor); // update it.
                        if (dividus.isDead()) { // if enemyType1 is dead.
                            gameComponents.remove(i--); // remove it from components.
                            size -= 1; // decrease size.
                            dividus.die(); // kill it, remove it from root.
                            deadCounter++;
                            player.setScore(player.getScore() + 100);
                        }
                    } else if (gameComponents.get(i) instanceof Dienamite) { // else if its an instance class of EmenyType1.
                        Dienamite dienamite = ((Dienamite) gameComponents.get(i));
                        dienamite.moveDienamite(gameComponentFactory, gameRoot, player, keyInputs[1].get(), speedFactor); // update it.
                        if (dienamite.isDead()) { // if enemyType1 is dead.
                            gameComponents.remove(i--); // remove it from components.
                            size -= 1; // decrease size.
                            dienamite.die(); // kill it, remove it from root.
                            deadCounter++;
                            player.setScore(player.getScore() + 100);
                        }
                    } else if (gameComponents.get(i) instanceof DivingWind) { // else if its an instance class of EmenyType1.
                        DivingWind divingWind = ((DivingWind) gameComponents.get(i));
                        divingWind.moveDivingWind(gameComponentFactory, gameRoot, player, keyInputs[1].get(), speedFactor); // update it.
                        if (divingWind.isDead()) { // if enemyType1 is dead.
                            gameComponents.remove(i--); // remove it from components.
                            size -= 1; // decrease size.
                            divingWind.die(); // kill it, remove it from root.
                            deadCounter++;
                            player.setScore(player.getScore() + 100);
                        }
                    } else if (gameComponents.get(i) instanceof LaserBullet) { // else if its an instance class of EnemyBulletType1.
                        LaserBullet laserBullet = (LaserBullet) gameComponents.get(i); // cast it to a temporary variable.
                        laserBullet.updateLaserBullet(); // update it.
                        // if its not in the boundaries of camera/root remove it.
                        // first check for X then check for Y.
                        if (laserBullet.getX() > (gameRoot.getTranslateX() * -1) + width + laserBullet.getWidth() || laserBullet.getX() < (gameRoot.getTranslateX() * -1) + laserBullet.getWidth()) {
                            gameComponents.remove(i--); // remove it from components and decrease i.
                            size -= 1; // decrease size.
                            laserBullet.die(); // kill it, remove it from root.
                        } else if (laserBullet.getY() >= gameRoot.getHeight() + laserBullet.getWidth() || laserBullet.getY() < 0 - laserBullet.getWidth()) {
                            gameComponents.remove(i--); // remove it from components and decrease i.
                            size -= 1; // decrease size.
                            laserBullet.die(); // kill it, remove it from root.
                        }
                        if (laserBullet.isDead()) {
                            gameComponents.remove(i--);
                            size -= 1;
                            laserBullet.die();
                        }
                    } else if (gameComponents.get(i) instanceof GuidedBullet) { // else if its an instance class of GuidedBullet
                        GuidedBullet guidedBullet = (GuidedBullet) gameComponents.get(i); // cast it to a temporary variable.
                        guidedBullet.moveGuidedBullet(player); // update it.
                        // if its not in the boundaries of camera/root remove it.
                        // first check for X then check for Y.
                        if (guidedBullet.getX() > (gameRoot.getTranslateX() * -1) + width + guidedBullet.getWidth() + magicConverter(width / 2) || guidedBullet.getX() < (gameRoot.getTranslateX() * -1) + guidedBullet.getWidth() - magicConverter(width / 2)) {
                            gameComponents.remove(i--); // remove it from components and decrease i.
                            size -= 1; // decrease size.
                            guidedBullet.die(); // kill it, remove it from root.
                        } else if (guidedBullet.getY() >= gameRoot.getHeight() + guidedBullet.getWidth() || guidedBullet.getY() < 0 - guidedBullet.getWidth()) {
                            gameComponents.remove(i--); // remove it from components and decrease i.
                            size -= 1; // decrease size.
                            guidedBullet.die(); // kill it, remove it from root.
                        }
                        if (guidedBullet.isDead()) {
                            gameComponents.remove(i--);
                            size -= 1;
                            guidedBullet.die();
                        }
                    } else if (gameComponents.get(i) instanceof SpeedRunner) { // else if its an instance class of EmenyType1.
                        SpeedRunner speedRunner = ((SpeedRunner) gameComponents.get(i));
                        speedRunner.moveSpeedRunner(gameComponentFactory, gameRoot, player, keyInputs[1].get(), speedFactor); // update it.
                        if (speedRunner.isDead()) { // if enemyType1 is dead.
                            gameComponents.remove(i--); // remove it from components.
                            size -= 1; // decrease size.
                            speedRunner.die(); // kill it, remove it from root.
                            deadCounter++;
                            player.setScore(player.getScore() + 100);
                        }
                    } else if (gameComponents.get(i) instanceof GuidedRocket) {
                        GuidedRocket gRocket = (GuidedRocket) gameComponents.get(i);
                        gRocket.chooseTarget(this.gameComponents);
                        gRocket.moveGuidedRocket();
                        if (gRocket.getY() >= gameRoot.getHeight() + gRocket.getWidth() || gRocket.getY() < 0 - gRocket.getWidth()) {
                            gameComponents.remove(i--); // remove it from components and decrease i.
                            size -= 1; // decrease size.
                            gRocket.die(); // kill it, remove it from root.
                        }
                        if (gRocket.isDead()) {
                            gameComponents.remove(i--);
                            size -= 1;
                            gRocket.die();
                        }
                    } else if (gameComponents.get(i) instanceof Boss) { // else if its an instance class of EmenyType1.
                        Boss boss = ((Boss) gameComponents.get(i));
                        boss.moveBoss(gameComponentFactory, gameRoot, player, keyInputs[1].get(), speedFactor); // update it.
                        if (boss.isDead()) { // if enemyType1 is dead.
                            gameComponents.remove(i--); // remove it from components.
                            size -= 1; // decrease size.
                            boss.die(); // kill it, remove it from root.
                            deadCounter++;
                            player.setScore(player.getScore() + 500);
                        }
                    } else if (gameComponents.get(i) instanceof Civilian) {
                        Civilian civilian = ((Civilian) gameComponents.get(i));
                        civilian.moveCivilian(gameComponentFactory, gameRoot, player, keyInputs[1].get(), speedFactor);
                        if (civilian.isDead()) {
                            gameComponents.remove(i--); // remove it from components.
                            size -= 1; // decrease size.
                            civilian.die(); // kill it, remove it from root.
                            player.setScore(player.getScore() - 200);
                        }
                        if (civilian.isSaved()) {
                            gameComponents.remove(i--); // remove it from components.
                            size -= 1;
                            civilian.die(); // kill it, remove it from root.
                            player.setScore(player.getScore() + 300);
                        }
                    } else if (gameComponents.get(i) instanceof Explosion) { // else if its an instance class of EmenyType1.
                        Explosion explosion = ((Explosion) gameComponents.get(i));
                        explosion.updateExplosion(); // update it.
                        if (explosion.isDead()) { // if enemyType1 is dead.
                            gameComponents.remove(i--); // remove it from components.
                            size -= 1; // decrease size.
                            explosion.die(); // kill it, remove it from root.
                        }
                    }
                }
                if(slowedDownTime) {
                    timeFreeze = true;
                }
                slowedDownTime = false;
            } else {
                player.movePlayer(keyInputs, gameComponentFactory, this); // update it.
                if (player.isDead()) { // if player is dead.
                    player.die(); // kill it, remove it from root.
                    setCurrentScreen(3);
                }
            }
            if (startSlidingLeft) { // if the background sliding left
                if (slidingCounter < 0) {// until sliding limit is reached or hits the player to screen limit
                    gameRoot.setTranslateX(gameRoot.getTranslateX() + slidingSpeed); // change background with sliding speed
                    scenery.slideScenery(true, slidingSpeed);
                    slidingCounter += slidingSpeed;
                } else {
                    startSlidingLeft = false; // finish the execution when the limit is reached.
                }
            }
            if (startSlidingRight) { // if the background sliding right
                if (slidingLimit * -1 != slidingCounter) {// until counter hits the 0
                    gameRoot.setTranslateX(gameRoot.getTranslateX() - slidingSpeed); // change background with sliding speed
                    scenery.slideScenery(false, slidingSpeed);
                    slidingCounter -= slidingSpeed;
                } else {
                    startSlidingRight = false; // finish the execution when the limit is reached.
                }
            }
        } else if (currentScreen == 1) { // STOP, SHOW MENU

            inGameMenu.displayMenu(gameRoot);
            if (keyInputs[3].get()) { // D pressed
                inGameMenu.changeActiveButton(1);
            }
            if (keyInputs[1].get()) { // A pressed
                inGameMenu.changeActiveButton(0);
            }
        } else if (currentScreen == 2) { // STOP, SHOW SCORE
            scoreMenu.displayScoreScreen(gameRoot, (int) player.getScore());

        } else if (currentScreen == 3) { // Game end
            endGameMenu.displayMenu(gameRoot);

        } else if (currentScreen == 4) {
            minimap.display(gameRoot);
        }

        createLevel();
        // update root
        if (keyInputs[3].get() && currentScreen == 0) { // if the key D pressed
            //handle the acceleration with scenery!
            if (speed < maxSpeed)
                speed += acceleration;
            if (speed < 0)
                speed += acceleration;
            //check for sliding
            if (!toLeft) { // if it was not toLeft, change camera and bring it to limit x.
                startSlidingRight = true;
                startSlidingLeft = false;
                toLeft = true;
            }
        }
        if (keyInputs[1].get() && currentScreen == 0) { // if the key A pressed
            //handle the acceleration with scenery!
            if (speed > -1 * maxSpeed)
                speed -= acceleration;
            if (speed > 0)
                speed -= acceleration;
            //check for sliding
            if (toLeft) { // if it was toLeft, change camera and bring it to limit x.
                startSlidingLeft = true;
                startSlidingRight = false;
                toLeft = false;
            }
        }
        if (keyInputs[4].get()) { // if enter is pressed.
            //todo
            if (currentScreen == 1) {
                if (inGameMenu.getActiveButton() == 0) {
                    currentScreen = 0;
                    inGameMenu.hideMenu();
                } else if (inGameMenu.getActiveButton() == 1) {
                    mainGame.backToMainMenu();
                }
            } else if (currentScreen == 3) {
                mainGame.backToMainMenu();
            }
        }
        if (keyInputs[12].get()) { // if ESC is pressed.
            if (currentScreen == 0) {
                setCurrentScreen(1);
                keyInputs[12].set(false);
            } else if (currentScreen == 1) {
                setCurrentScreen(0);
                keyInputs[12].set(false);
            }
        }
        if (keyInputs[13].get()){ // if TAB is pressed
            if (currentScreen == 0){
                setCurrentScreen(4);
                minimap.createMinimap(gameRoot, gameComponents, gameRoot.getTranslateX(), scenery.getWidth(), scenery.getHeight());
                keyInputs[13].set(false);
            } else if (currentScreen == 4){
                setCurrentScreen(0);
                keyInputs[13].set(false);
            }
        }
        if (!keyInputs[1].get() && !keyInputs[3].get() && currentScreen == 0) { // if the movement keys not pressed
            //handle the acceleration
            if (speed > 0) {
                speed -= acceleration;
                if (speed < 0)
                    speed = 0;
            } else {
                speed += acceleration;
                if (speed > 0)
                    speed = 0;
            }
        }
        if (currentScreen == 0) {
            gameRoot.setTranslateX(gameRoot.getTranslateX() - speed);
            // update scenery
            scenery.update(keyInputs, player, fps, speed);
        }
    }


    /**
     * this function is just a macro or creating different types of enemies at the same time
     * we used this function for holding the number of total  enemies in the level
     * @param atlasNumber number of enemy type atlas
     * @param dodgernumber number of enemy type dodger
     * @param dividusNumber number of enemy type dividus
     * @param dienamiteNumber number of enemy type dienamite
     * @param speedRunnerNumber number of enemy type speedrunner
     * @param divingWindNumber number of enemy type divingwind
     * @param bossNumber number of boss
     * @return sum of parameters
     */
    public int createEnemies(int atlasNumber, int dodgernumber, int dividusNumber, int dienamiteNumber, int speedRunnerNumber, int divingWindNumber, int bossNumber) {
        for (int i = 0; i < atlasNumber; i++) {
            Atlas atlas = (Atlas) gameComponentFactory.createComponent("atlas");
            atlas.addShapes(gameRoot);
        }

        for (int i = 0; i < dodgernumber; i++) {
            Dodger dodger = (Dodger) gameComponentFactory.createComponent("dodger");
            dodger.addShapes(gameRoot);
        }

        for (int i = 0; i < dividusNumber; i++) {
            Dividus dividus = (Dividus) gameComponentFactory.createComponent("dividus");
            dividus.addShapes(gameRoot);
        }

        for (int i = 0; i < dienamiteNumber; i++) {
            Dienamite dienamite = (Dienamite) gameComponentFactory.createComponent("dienamite");
            dienamite.addShapes(gameRoot);
        }

        for (int i = 0; i < divingWindNumber; i++) {
            DivingWind divingWind = (DivingWind) gameComponentFactory.createComponent("divingWind");
            divingWind.addShapes(gameRoot);
        }
        for (int i = 0; i < speedRunnerNumber; i++) {
            SpeedRunner speedRunner = (SpeedRunner) gameComponentFactory.createComponent("speedRunner");
            speedRunner.addShapes(gameRoot);
        }
        for (int i = 0; i < divingWindNumber; i++) {
            DivingWind divingWind = (DivingWind) gameComponentFactory.createComponent("divingWind");
            divingWind.addShapes(gameRoot);
        }
        for (int i = 0; i < bossNumber; i++) {
            Boss boss = (Boss) gameComponentFactory.createComponent("boss");
            boss.addShapes(gameRoot);
        }

        return (atlasNumber + dodgernumber + (dividusNumber* 3) + dienamiteNumber + speedRunnerNumber + divingWindNumber + (bossNumber*30) );
    }

    /**
     * function for creating the civilians
     * @param civilianNumber is the number of civilians
     */
    public void createCivilians(int civilianNumber) {
        for (int i = 0; i < civilianNumber; i++) {
            Civilian civilian = (Civilian) gameComponentFactory.createComponent("civilian");
            civilian.addShapes(gameRoot);
        }
    }

    /**
     * that is the function creates levels each time
     * theorethecially 2 billion levels can be generated
     * in every four level boss appears
     */
    public void createLevel() {
        int atlasNumber = 2;
        int dodgerNumber = 2;
        int dividusNumber = 2;
        int dienamiteNumber = 2;
        int speedRunnerNumber = 2;
        int bossNumber = 1;
        int divingWindNumber = 2;
        int civilianNumber = 5;
        int levelMod = level % 5;

        if (isCounterStarted){
            double currentTime = System.nanoTime() / 1000000000.0 - startTime;
            if (currentTime >= 4) {
                setCurrentScreen(0);
                isCounterStarted = false;
            }
        }

        if (levelMod == 1) {
            if (noOfEnemies == 0) {
                //noOfEnemies = createEnemies(atlasNumber * levelMod ,dodgerNumber * levelMod ,dividusNumber * levelMod ,dienamiteNumber * levelMod,speedRunnerNumber * levelMod, divingWindNumber * levelMod , 0);
                noOfEnemies = createEnemies(0, 0, 0, 0, 0, 0, 1);
                createCivilians(civilianNumber);
            }
            if (noOfEnemies == deadCounter) {
                System.out.println("Level1 cleared !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1");
                level = level + 1;
                deadCounter = 0;
                noOfEnemies = 0;
                levelMod = level % 5;

                if (!isCounterStarted) {
                    startTime = System.nanoTime() / 1000000000.0;
                    isCounterStarted = true;
                    setCurrentScreen(2);
                }

            }

        } else if (levelMod == 2) {
            if (noOfEnemies == 0) {
                noOfEnemies = createEnemies(0, 0, 0, 0, 20, 0, 0);
                createCivilians(civilianNumber);
                System.out.println("level2 entered ");
            }
                
            if (noOfEnemies == deadCounter) {
                level = level + 1;
                deadCounter = 0;
                noOfEnemies = 0;
                levelMod = level % 5;

                if (!isCounterStarted) {
                    startTime = System.nanoTime() / 1000000000.0;
                    isCounterStarted = true;
                    setCurrentScreen(2);
                }
            }
        } else if (levelMod == 3) {
            if (noOfEnemies == 0) {
                noOfEnemies = createEnemies(0, 20, 20, 0, 0, 0, 0);
                System.out.println("level3 entered ");
                createCivilians(civilianNumber);
            }
            if (noOfEnemies == deadCounter) {
                level = level + 1;
                deadCounter = 0;
                noOfEnemies = 0;
                levelMod = level % 5;

                if (!isCounterStarted) {
                    startTime = System.nanoTime() / 1000000000.0;
                    isCounterStarted = true;
                    setCurrentScreen(2);
                }
            }
        } else if (levelMod == 4) {
            if (noOfEnemies == 0) {
                noOfEnemies = createEnemies(20, 0, 0, 20, 0, 20, 0);
                System.out.println("level4 entered ");
                createCivilians(civilianNumber);
            }
            if (noOfEnemies == deadCounter) {
                level = level + 1;
                noOfEnemies = 0;
                speedFactor++;
                levelMod = level % 5;

                if (!isCounterStarted) {
                    startTime = System.nanoTime() / 1000000000.0;
                    isCounterStarted = true;
                    setCurrentScreen(2);
                }
            }
        } else  if (levelMod == 0) {
            if (noOfEnemies == 0) {
                //noOfEnemies = createEnemies(atlasNumber * levelMod ,dodgerNumber * levelMod ,dividusNumber * levelMod ,dienamiteNumber * levelMod,speedRunnerNumber * levelMod, divingWindNumber * levelMod , 0);
                noOfEnemies = createEnemies(0, 0, 0, 0, 0, 0, 1);
                createCivilians(civilianNumber);
            }
            if (noOfEnemies == deadCounter) {
                System.out.println("Level1 cleared !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1");
                level = level + 1;
                deadCounter = 0;
                noOfEnemies = 0;
                levelMod = level % 5;

                if (!isCounterStarted) {
                    startTime = System.nanoTime() / 1000000000.0;
                    isCounterStarted = true;
                    setCurrentScreen(2);
                }

            }

        }
    }

    /**
     *  macro for hotkeys in the game
     * @param scene is the scene of the in-game
     */
    public void setButtonHandler(Scene scene) {
        for (int i = 0; i < keyInputs.length; i++)
            keyInputs[i] = new SimpleBooleanProperty();
        scene.setOnKeyPressed(e -> {
            if ((e.getCode() == KeyCode.W) || (e.getCode() == KeyCode.UP)) {
                keyInputs[0].set(true);
            }
            if ((e.getCode() == KeyCode.S) || (e.getCode() == KeyCode.DOWN)) {
                keyInputs[2].set(true);
            }
            if ((e.getCode() == KeyCode.A) || (e.getCode() == KeyCode.LEFT)) {
                keyInputs[1].set(true);
            }
            if ((e.getCode() == KeyCode.D) || (e.getCode() == KeyCode.RIGHT)) {
                keyInputs[3].set(true);
            }
            if (e.getCode() == KeyCode.ENTER) {
                keyInputs[4].set(true);
            }
            if (e.getCode() == KeyCode.SPACE) {
                keyInputs[5].set(true);
            }
            if (e.getCode() == KeyCode.Q) {
                keyInputs[6].set(true);
            }
            if (e.getCode() == KeyCode.E) {
                keyInputs[7].set(true);
            }
            if (e.getCode() == KeyCode.H) {
                keyInputs[8].set(true);
            }
            if (e.getCode() == KeyCode.J) {
                keyInputs[9].set(true);
            }
            if (e.getCode() == KeyCode.K) {
                keyInputs[10].set(true);
            }
            if (e.getCode() == KeyCode.L) {
                keyInputs[11].set(true);
            }
            if (e.getCode() == KeyCode.TAB) {
                keyInputs[13].set(true);
            }
        });
        scene.setOnKeyReleased(e -> {
            if ((e.getCode() == KeyCode.W) || (e.getCode() == KeyCode.UP)) {
                keyInputs[0].set(false);
            }
            if ((e.getCode() == KeyCode.S) || (e.getCode() == KeyCode.DOWN)) {
                keyInputs[2].set(false);
            }
            if ((e.getCode() == KeyCode.A) || (e.getCode() == KeyCode.LEFT)) {
                keyInputs[1].set(false);
            }
            if ((e.getCode() == KeyCode.D) || (e.getCode() == KeyCode.RIGHT)) {
                keyInputs[3].set(false);
            }
            if (e.getCode() == KeyCode.ENTER) {
                keyInputs[4].set(false);
            }
            if (e.getCode() == KeyCode.SPACE) {
                keyInputs[5].set(false);
            }
            if (e.getCode() == KeyCode.Q) {
                keyInputs[6].set(false);
            }
            if (e.getCode() == KeyCode.E) {
                keyInputs[7].set(false);
            }
            if (e.getCode() == KeyCode.H) {
                keyInputs[8].set(false);
            }
            if (e.getCode() == KeyCode.J) {
                keyInputs[9].set(false);
            }
            if (e.getCode() == KeyCode.K) {
                keyInputs[10].set(false);
            }
            if (e.getCode() == KeyCode.L) {
                keyInputs[11].set(false);
            }
            if (e.getCode() == KeyCode.ESCAPE) {
                keyInputs[12].set(true);
            }
            if (e.getCode() == KeyCode.TAB) {
                keyInputs[13].set(false);
            }
        });
    }

    /**
     * slides the game
     * @param toLeft slides to left
     * @param slidingSpeed sliding speed pf the slide
     */
    public void slideScenery(boolean toLeft, double slidingSpeed) {
        scenery.slideScenery(toLeft, slidingSpeed);
    }

    /**
     *a macro for getting the values for the screen resolution 1920p
     * @param wantedInteger you enter the integer you want
     * @return is the integer used in program
     */
    public double magicConverter(double wantedInteger) {
        return width / (1920 / wantedInteger);
    }

    /**
     * for starting the game from any level
     * setting the level of the game
     * setter method for integer level
     * @param levelNum the number of level you want to start
     */
    public void setLevel(int levelNum) {
        level = levelNum;
    }

    /**
     * getter method for integer level
     * @return the private integer level
     */
    public int getLevel() {
        return level;
    }

    /**
     *  causes the pause screen to appear
     * @param screenID gets the screenID
     */
    public void setCurrentScreen(int screenID) {

        if (currentScreen == 1) {
            inGameMenu.hideMenu();
        }

        if (currentScreen == 2) {
            scoreMenu.hideScoreScreen();
        }

        if (currentScreen == 4){
            minimap.hide(gameRoot);
        }

        currentScreen = screenID;
    }

    /**
     * setter for integer selectShipNumber
     * @param shipNum is the number we want to set the selectshipnNumber
     */
    public void setSelectShipNumber(int shipNum) {
        selectShipNumber = shipNum;
    }

    /**
     * getter for selectShipNumber
     * @return the private integer selectShipNumber
     */
    public int getSelectShipNumber() {
        return selectShipNumber;
    }

    public void freeze() {
        timeFreeze = true;
        lastTime = 0;
        System.out.println("time Freezed");
    }
}
