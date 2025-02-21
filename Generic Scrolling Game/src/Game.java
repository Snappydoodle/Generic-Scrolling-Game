public class Game //http://nifty.stanford.edu/2011/feinberg-generic-scrolling-game/assignment.html
{//sorry if this code is a bit unreadable, i wrote this like a year ago in a different IDE, so I had to move all the files into here and make sure everything worked
  private Grid grid;
  private int userRow;
  private int userCol;
  private int msElapsed;
  private int timesGet;
  private int timesAvoid;
  private double spawnMultiplier;
  private String playerFrame; //keeps track of what frame of animation the player is at
  private int invFramesLeft;
  private int scrollRate;
  private int bulletCooldownLeft;
  private boolean gameStarted;
  
  private final int LIVES = 3;
  private final int INV_FRAMES = 1500;
  private final double SPAWN_MULTIPLIER_RATE = 40000;
  private final int BULLET_COOLDOWN = 5000;
  
  public Game()
  {
      gameStarted = false;
      Laser.setState(0);
    grid = new Grid(5, 10);
    userRow = 0;
    userCol = 1;
    msElapsed = 0;
    timesGet = 0;
    timesAvoid = 0;
    invFramesLeft = 0;
    spawnMultiplier = 0.5;
    scrollRate = 350;
    bulletCooldownLeft = 0;
    playerFrame = "Player1.gif";
    updateTitle();
    grid.setImage(new Location(userRow, userCol), playerFrame);
    while (grid.checkLastKeyPressed() == -1){
        //System.out.println(grid.checkLastKeyPressed());
       grid.pause(50); //not exactly sure why this completely prevents play() from running but ok
    }
    gameStarted = true;
  }
  
  public void play()
  {
    while (!isGameOver())
    {
      grid.pause(50);
      handleKeyPress();
      if (spawnMultiplier > .75){
          scrollRate = 300;
      }
      else if (spawnMultiplier > 1.25){
          scrollRate = 250;
      }
      else if (spawnMultiplier > 1.5){
          scrollRate = 200;
      }
      else if (spawnMultiplier > 2){
          scrollRate = 150;
      }
      if (msElapsed % scrollRate == 0)
      {
        handleCollision(new Location(userRow, userCol + 1));
        scrollLeft();
        populateRightEdge();
      }
      if (msElapsed % 100 == 0){
          if (msElapsed % 200 == 0){
              if (invFramesLeft > 0){
                playerFrame = "Player3.gif";
              }
              else{
                playerFrame = "Player2.gif";
              }
          }
          else{
              playerFrame = "Player1.gif";
          }
          grid.setImage(new Location(userRow, userCol), playerFrame);
      }
      if (invFramesLeft > 0){
          invFramesLeft -= 50;
      }
      if (bulletCooldownLeft > 0){
          bulletCooldownLeft -= 50;
      }
      Laser.updateLaser(50, grid);
      spawnMultiplier = .5 + (msElapsed / SPAWN_MULTIPLIER_RATE);
      //System.out.println(timesAvoid);
      updateTitle();
      msElapsed += 50;

    }
    grid.pause(1000);
    System.out.println("sdflk");
    while (grid.checkLastKeyPressed() == -1){
       grid.pause(50); //not exactly sure why this completely prevents play() from running but ok
    }
    grid.frame.setVisible(false);
    Game game = new Game();
    game.play();
  }
  
  public void handleKeyPress()
  {
      grid.setImage(new Location(userRow, userCol), null);//deletes last image
      int key = grid.checkLastKeyPressed();
      if (key == 38 || key == 87){
          if (userRow > 0){
            handleCollision(new Location(userRow - 1, userCol));
            userRow--;
          }
          else{
            handleCollision(new Location(grid.getNumRows() - 1, userCol));
            userRow = grid.getNumRows() - 1;//if player on top row teleport to bottom
          }
      }
      else if (key == 40 || key == 83){
          if (userRow < grid.getNumRows() - 1){
            handleCollision(new Location(userRow + 1, userCol));
            userRow++;
          }
          else{
            handleCollision(new Location(0, userCol));
            userRow = 0;//if player on bottom row teleport to top
          }
      }
      else if (key == 32 && bulletCooldownLeft <= 0){//32 = space
            String image1 = grid.getImage(new Location(userRow, userCol + 1));
            String image2 = grid.getImage(new Location(userRow, userCol + 2));
            if (image1 == "Asteroid1.gif" || image1 == "Asteroid2.gif" || image1 == "Orbs.gif"){//checks if there is an asteriod directly in front of the spaceship
                grid.setImage(new Location(userRow, userCol + 1), "Explosion.gif");
            }
            else if (image2 == "Asteroid1.gif" || image2 == "Asteroid2.gif" || image2 == "Orbs.gif"){
                grid.setImage(new Location(userRow, userCol + 2), "Explosion.gif");
            }
            else{
                grid.setImage(new Location(userRow, userCol + 1), "Bullet.gif");
            }
            
            bulletCooldownLeft = BULLET_COOLDOWN;
      }
      grid.setImage(new Location(userRow, userCol), playerFrame);
  }
  
  public void populateRightEdge()
  {
      
      if (Math.random() < .25 * spawnMultiplier){
        grid.setImage(new Location((int) (Math.random() * grid.getNumRows()), grid.getNumCols() - 1), "Asteroid1.gif");
      }
      if (Math.random() < .25 * spawnMultiplier){
        grid.setImage(new Location((int) (Math.random() * grid.getNumRows()), grid.getNumCols() - 1), "Asteroid2.gif");
      }
      if (Math.random() < .05 * spawnMultiplier && Laser.getState() == 0){
              Laser.createLaser((int) (Math.random() * grid.getNumRows()));
    }           //System.out.println((int) (Math.random() * grid.getNumRows()));
      if (Math.random() < .125){
              grid.setImage(new Location((int) (Math.random() * grid.getNumRows()), grid.getNumCols() - 1), "Orbs.gif");
    }
  }
  
  public void scrollLeft()
  {
    for (int i = 0; i < grid.getNumRows(); i++){//calls scrollRow for every row
        scrollRow(i);
    }
  }
  //old conditional (stupidly long): if (image != null && image != "Bullet.gif" && image != "Explosion.gif" && image != "Laser.gif" && image != "Player1.gif" && image != "Player2.gif" && image != "Player3.gif")
  public void scrollRow(int row){
      for (int i = 0; i < grid.getNumCols(); i++){
          String image = grid.getImage(new Location (row, i));
          if (image == "Asteroid1.gif" || image == "Asteroid2.gif" || image == "Orbs.gif" || image == "Explosion.gif"){
              grid.setImage(new Location (row, i), null);//deletes old image
              if (i > 0 && image != "Explosion.gif"){//checks if image is not on left side of screen or an explosion (which doesn't scroll)
                    grid.setImage(new Location (row, i - 1), image); //adds new image if not already on left 
                }
              }
        }
          /*
         else if (grid.getImage(new Location (row, i)) == "get.gif"){
              grid.setImage(new Location (row, i), null);//deletes old image
              if (i > 0){
                grid.setImage(new Location (row, i - 1), "get.gif"); //adds new image if not already on left side of screen
              }
          }
          */
      
      for (int i = grid.getNumCols() - 1; i >= 0; i--){//scrolls in opposite direction for bullet because bullet scrolls to the right
          String image = grid.getImage(new Location (row, i));
          String image1; 
          String image2;
          boolean isImageDestroyable = false;
          boolean isImage1Destroyable = false;
          boolean isImage2Destroyable = false;//this massive block of code basically checks if the image 1 or 2 tiles ahead of the  bullet is a destroyable object, and if so, destroys it and replaces it with an explosion 
          if (image == "Asteroid1.gif" || image == "Asteroid2.gif" || image == "Orbs.gif"){
                  isImageDestroyable = true;
              }
          if (i < grid.getNumCols() - 2){
              image1 = grid.getImage(new Location (row, i + 1));
              if (image1 == "Asteroid1.gif" || image1 == "Asteroid2.gif" || image1 == "Orbs.gif"){
                  isImage1Destroyable = true;
              }
          }
          else{
              image1 = null;
          }
          if (i < grid.getNumCols() - 3){
              image2 = grid.getImage(new Location (row, i + 2));
              if (image2 == "Asteroid1.gif" || image2 == "Asteroid2.gif" || image2 == "Orbs.gif"){
                  isImage2Destroyable = true;
              }
          }
          else{
              image2 = null;
          }

          if (image == "Bullet.gif"){
              grid.setImage(new Location (row, i), null);//deletes old image
              if (i < grid.getNumCols() - 1){//checks if image is not on right side of screen
                if (isImageDestroyable){
                     grid.setImage(new Location (row, i), "Explosion.gif");
                }
                else if (isImage1Destroyable){
                    grid.setImage(new Location (row, i + 1), "Explosion.gif");
                }
                else if (isImage2Destroyable){
                    grid.setImage(new Location (row, i + 2), null);
                    grid.setImage(new Location (row, i + 1), "Explosion.gif");
                }
                else{
                    grid.setImage(new Location (row, i + 1), image);
                }
              }
          }
      }
  }

  public void handleCollision(Location loc)
  {
      String locImage = grid.getImage(loc); //gets the filename of the image
      if ((locImage == "Asteroid1.gif" || locImage == "Asteroid2.gif" || locImage == "Laser.gif") && invFramesLeft <= 0){
          timesAvoid ++;
          invFramesLeft = INV_FRAMES;
      }
      if (locImage == "Orbs.gif"){
          timesGet ++;
      }
  }
  
  public int getScore()
  {
    return timesGet;
  }
  
  public void updateTitle()
  {
    if (gameStarted){
        String bulletCooldownText = "BULLET READY";
        if (bulletCooldownLeft > 0){
        bulletCooldownText = "BULLET ON COOLDOWN";
        }
        if (isGameOver()){
         grid.setTitle("Game Over! Score: " + getScore());
        }
        else{
        grid.setTitle("Score:  " + getScore() + " Lives: " + (LIVES - timesAvoid) + " " + bulletCooldownText);
        }
    }
    else{
        grid.setTitle("Press Any Key to Start!");
    }
  }
  
  public boolean isGameOver()
  {
    return (timesAvoid >= LIVES);
  }
  
  public static void test()
  {
    Game game = new Game();
    game.play();
  }
  
  public static void main(String[] args)
  {
      
      //System.out.println("sldkjfdsf");
    test();
  }
}