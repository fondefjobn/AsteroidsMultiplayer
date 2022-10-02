package aoop.asteroids.control.menu;

import java.util.ArrayList;
import java.util.stream.IntStream;

/**
 * This is an enum for menu items
 */
public enum MenuItem {

    NEW_GAME (true, "New Game", 0),
    JOIN (true, "Join online Game", 1),
    HOST (true, "Host online game", 2),
    SPECTATE (true, "Spectate", 3),
    HIGH_SCORES (true, "High Scores", 4),
    MAIN_MENU (false, "Main Menu", 5),
    QUIT (false, "Quit", 6);

    private final boolean isMenuItem;
    private final String title;
    private final int ID;

    /**
     * Constructor
     * @param isMenuItem if true it is not in main menu, but only in menu bar, otherwise it's in both
     * @param title of the menu item
     * @param ID of the menu item
     */
    MenuItem(boolean isMenuItem, String title, int ID) {
        this.isMenuItem = isMenuItem;
        this.title = title;
        this.ID = ID;
    }

    /**
     * @return ArrayList of all menu items
     */
    public static ArrayList<MenuItem> getAllItems() {
        ArrayList<MenuItem> items = new ArrayList<>();
        IntStream.range(0, values().length).forEach(i -> items.add(values()[i]));
        return items;
    }

    /**
     * @return ArrayList of all strictly menu items
     */
    public static ArrayList<MenuItem> getMenuItems() {
        ArrayList<MenuItem> items = new ArrayList<>();
        IntStream.range(0, values().length).forEach(i -> {
            if (values()[i].isMenuItem()) items.add(values()[i]);
        });
        return items;
    }

    /**
     * @return Number of menu items
     */
    public static int getNumberOfMenuItems() {
        return (int) IntStream.range(0, values().length).filter(i -> values()[i].isMenuItem()).count();
    }

    /**
     * fins a menu items by it's name
     * @param title of the menu item
     * @return corresponding menu item if it exist, null otherwise
     */
    public static MenuItem findMenuItem(String title) {
        if (title == null) return null;
        for (int i = 0; i < values().length; i++) {
            if (values()[i].title.equals(title)) return values()[i];
        }
        return null;
    }

    /**
     * @return false if the item is only in menu bar, true otherwise
     */
    public boolean isMenuItem() {
        return isMenuItem;
    }

    /**
     * @return title of the menu item
     */
    public String getTitle() {
        return title;
    }


    /**
     * @return ID of the menu item
     */
    public int getID() {
        return ID;
    }
}
