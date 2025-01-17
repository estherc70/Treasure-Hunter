/**
 * The Town Class is where it all happens.
 * The Town is designed to manage all the things a Hunter can do in town.
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */

public class Town {
    // instance variables
    private Hunter hunter;
    private Shop shop;
    private Terrain terrain;
    private String printMessage;
    private boolean toughTown;
    private boolean hasDig;
    private boolean searched;
    private String treasure;
    private boolean easyTown;
    private boolean isEasyMode;


    /**
     * The Town Constructor takes in a shop and the surrounding terrain, but leaves the hunter as null until one arrives.
     *
     * @param shop The town's shoppe.
     * @param toughness The surrounding terrain.
     */
    public Town(Shop shop, double toughness) {
        this.shop = shop;
        this.terrain = getNewTerrain();
        searched = false;
        treasure = "";
        // the hunter gets set using the hunterArrives method, which
        // gets called from a client class
        hunter = null;
        printMessage = "";

        // higher toughness = more likely to be a tough town
        toughTown = (Math.random() < toughness);
        easyTown = (Math.random() < toughness);
        if (toughness == 0.25) {
            isEasyMode = true;
        } else {
            isEasyMode = false;
        }
        hasDig = false;
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public String getLatestNews() {
        return printMessage;
    }

    /**
     * Assigns an object to the Hunter in town.
     *
     * @param hunter The arriving Hunter.
     */
    public void hunterArrives(Hunter hunter) {
        this.hunter = hunter;
        printMessage = "Welcome to town, " + hunter.getHunterName() + ".";
        if (toughTown) {
            printMessage += "\nIt's pretty rough around here, so watch yourself.";
        } else {
            printMessage += "\nWe're just a sleepy little town with mild mannered folk.";
        }
    }

    /**
     * Handles the action of the Hunter leaving the town.
     *
     * @return true if the Hunter was able to leave town.
     */
    public boolean leaveTown() {
        boolean canLeaveTown = terrain.canCrossTerrain(hunter);
        if (canLeaveTown) {
            String item = terrain.getNeededItem();
            printMessage = "You used your " + item + " to cross the " + terrain.getTerrainName() + ".";
            if (checkItemBreak()) {
                hunter.removeItemFromKit(item);
                printMessage += "\nUnfortunately, you lost your " + item;
            }
            return true;
        }
        printMessage = "You can't leave town, " + hunter.getHunterName() + ". You don't have a " + terrain.getNeededItem() + ".";
        return false;
    }

    public void digForGold() {
        if ((hunter.hasItemInKit("shovel")) && (!hasDig)) {
            if (((int) (Math.random() * 2) + 1) == 1) {
                int digGold = (int) (Math.random() * 20) + 1;
                hunter.changeGold(digGold);
                printMessage = ("You dug up " + Colors.YELLOW + digGold + " gold!" + Colors.RESET);
            } else {
                printMessage = ("You dug but only found dirt");
            }
            hasDig = true;
        } else if (hasDig){
            printMessage = ("You already dug for gold in this town.");
        } else {
            printMessage = ("You can't dig for gold without a shovel");
        }
    }

    /**
     * Handles calling the enter method on shop whenever the user wants to access the shop.
     *
     * @param choice If the user wants to buy or sell items at the shop.
     */
    public void enterShop(String choice) {
        printMessage = shop.enter(hunter, choice);
    }

    /**
     * Gives the hunter a chance to fight for some gold.<p>
     * The chances of finding a fight and winning the gold are based on the toughness of the town.<p>
     * The tougher the town, the easier it is to find a fight, and the harder it is to win one.
     */
    public void lookForTrouble() {
        double noTroubleChance;
        if (toughTown) {
            noTroubleChance = 0.60;
        } else if (easyTown){
            noTroubleChance = 0.100;
        } else {
            noTroubleChance = 0.30;
        }
        if (Math.random() > noTroubleChance) {
            printMessage = Colors.BLUE + "You couldn't find any trouble" + Colors.RESET;
        } else {
            printMessage = Colors.RED + "You want trouble, stranger!  You got it!\nOof! Umph! Ow!\n" + Colors.RESET;
            int goldDiff = (int) (Math.random() * 10) + 1;
            if (((Math.random() > noTroubleChance) || (hunter.hasItemInKit("sword"))) || (hunter.getKit().length == 8)) {
                if ((hunter.hasItemInKit("sword"))) {
                    printMessage += Colors.RED + "The brawler, seeing your sword, realizes he picked a losing fight and gives you his gold." + Colors.RESET;
                }
                printMessage += Colors.RED + "Okay, stranger! You proved yer mettle. Here, take my gold." + Colors.RESET;
                printMessage += Colors.RED + "\nYou won the brawl and receive " + Colors.YELLOW + goldDiff + Colors.RED + " gold." + Colors.RESET ;
                hunter.changeGold(goldDiff);
            } else {
                printMessage += Colors.RED + "That'll teach you to go lookin' fer trouble in MY town! Now pay up!"+ Colors.RESET;
                printMessage += Colors.RED + "\nYou lost the brawl and pay " + Colors.YELLOW + goldDiff + " gold." + Colors.RESET;
                hunter.changeGold(-goldDiff);
            }
        }
    }

    public String infoString() {
        return "This nice little town is surrounded by " + terrain.getTerrainName() + ".";
    }

    /**
     * Determines the surrounding terrain for a town, and the item needed in order to cross that terrain.
     *
     * @return A Terrain object.
     */
    private Terrain getNewTerrain() {
        double rnd =  (int) (Math.random() * 120);
        if (rnd < 20) {
            return new Terrain("Mountains", "Rope");
        } else if (rnd < 40) {
            return new Terrain("Ocean", "Boat");
        } else if (rnd < 60) {
            return new Terrain("Plains", "Horse");
        } else if (rnd < 80) {
            return new Terrain("Desert", "Water");
        } else if (rnd < 100){
            return new Terrain("Jungle", "Machete");
        } else {
            return new Terrain("Marsh", "Boots");
        }
    }

    /**
     * Determines whether a used item has broken.
     *
     * @return true if the item broke.
     */
    private boolean checkItemBreak() {
        double rand = Math.random();
        if (isEasyMode) {
            return false;
        } else {
            return (rand < 0.5);
        }
    }

    /**
     *
     * @return the random treasure that's generated as well as add the treasure to the treasure list
     *
     */

    public String getTreasure() {
        double rnd = (int) (Math.random() * 40);
        if (rnd < 10) {
            treasure = "crown";
            return "You found a crown!";
        }
        else if (rnd < 20) {
            treasure = "trophy";
            return "You found a trophy!";
        }
        else if (rnd < 30) {
            treasure = "gem";
            return "You found a gem!";
        }
        else {
            return "You found dust! Useless!";
        }

    }

    /**
     *
     * @return returns the latest treasure that was found
     */
    public String getCurrentTreasure() {
        return treasure;
    }


    /**
     *
     * @return the boolean searched
     */

    public boolean getSearched() {
        return searched;
    }

    /**
     *
     * @param bool new boolean to be set to search
     */
    public void setSearched(boolean bool) {
        searched = bool;
    }


}