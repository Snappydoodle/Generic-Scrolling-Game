public class Laser{
    //probably could've put all of this code into Game.java since everything here is static, but it would've been way too complicated/confusing
    private static int msElapsed;
    private static int state;//0 = idle, 1 = charging, 2 = shooting, 3 = going away
    private static int row;
    private static Grid grid;
    private static final int HELPER_LENGTH = 1250;
    private static final int LASER_LENGTH = 2500;

    public Laser(){
        msElapsed = 0;
        state = 0;
    }
    public static void updateLaser(int ms, Grid initGrid){
        grid = initGrid;
        if (state != 0){
            msElapsed += ms;
            if (msElapsed >= HELPER_LENGTH && msElapsed < LASER_LENGTH){
                state = 2;
            }
            else if (msElapsed >= LASER_LENGTH){
                state = 3;   
            }

            if (state == 1){//repeadetly toggles color of helper to give flashing effect
                if (msElapsed % 100 == 0){
                    if (msElapsed % 200 == 0){
                        updateColor(new Color(0, 0, 0));
                    }
                    else{
                        updateColor(new Color(75, 0, 0));
                    }
                }
            }
            else if (state == 2){
                updateColor(new Color(0, 0, 0));
                grid.setImage(new Location (row, grid.getNumCols() - 1), "Laser.gif");
                for (int i = 0; i < grid.getNumCols(); i++){
                    if (grid.getImage(new Location (row, i)) == "Laser.gif"){
                        if (i > 0){//checks if image is not on left side of screen
                            grid.setImage(new Location (row, i - 1), "Laser.gif"); //adds new image if not already on left side of screen
                        }
                    }
                }
            }
            else if (state == 3){//deletes the rightmost image of the laser. the rest of the lasers individually delete themselves when they detect the image to the right of them = null
                grid.setImage(new Location (row, grid.getNumCols() - 1), null);
                for (int i = 0; i < grid.getNumCols() - 1; i++){
                    if (grid.getImage(new Location (row, i + 1)) == null){
                        grid.setImage(new Location (row, i), null); 
                        if (i <= 0){//checks if image is not on left side of screen
                            state = 0;
                            //System.out.println(state);
                        }
                    }
                }
            }
            /*
            if (grid.getImage(new Location (row, 0)) == "Player1.gif" || grid.getImage(new Location (row, 0)) == "Player2.gif" || grid.getImage(new Location (row, 0)) == "Player3.gif"){
                grid.setImage(new Location (row, 0), null); 
            }
            */
            
        }
    }
    public static void updateColor(Color color){
        for (int i = 0; i < grid.getNumCols(); i++){
            grid.setColor(new Location(row, i),color);
        }
        
    }
    public static int getState(){
        return state;
    }
    public static void setState(int initState){
        state = initState;
    }
    public static void createLaser(int initRow){
        row = initRow;
        msElapsed = 0;
        state = 1;

    }
}