package sample;

import javafx.scene.image.Image;

/**
 * Created by you on 2017/05/25.
 */
abstract class Pokemon {
    private String name;
    private int HP;
    private int ATK;
    private int AGI;
    private int COST;
    private int refresh = 10000;
    private boolean alive = true;
    private String type;
    private Image image;
    private Item item = null;
    private static Type typemap = new Type();

    public Pokemon(String name, int HP, int ATK, int AGI, int COST,  String type, Image image){
        setStatus(HP, ATK, AGI, COST, type);
        setName(name);
        setImage(image);
    }

    private void setName(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    private void setStatus(int HP, int ATK, int AGI, int COST, String type){
        this.HP = HP;
        this.ATK = ATK;
        this.AGI = AGI;
        this.COST = COST;
        this.type = type;
    }

    public void setHP(int HP) {
        this.HP = HP;
    }

    public int getHP(){
        return this.HP;
    }

    public void setATK(int ATK) {
        this.ATK = ATK;
    }

    public int getATK(){
        return this.ATK;
    }

    public void setAGI(int AGI) {
        this.AGI = AGI;
    }

    public int getAGI(){
        return this.AGI;
    }

    public int getCOST(){
        return this.COST;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType(){
        return this.type;
    }

    private void setImage(Image image){
        this.image = image;
    }

    public Image getImage(){
        return this.image;
    }

    public void setItem(Item item){
        this.item = item;
    }

    public Item getItem(){
        return this.item;
    }

    public void setRefresh(int refresh) {
        this.refresh = refresh;
    }

    public boolean isRefreshed() {
        return (refresh>=10000);
    }

    public int getRefresh() {
        return refresh;
    }

    public void Dead() {
        this.alive = false;
    }

    public boolean isAlive(){
        return alive;
    }

    public int attack(Pokemon p){
        return (int) (getATK() * this.typemap.typeaff(this.getType(),p.getType()));
    }

    public void damage(int damage){
        setHP(getHP()-damage);
        if(getHP() <= 0) Dead();
    }

    public void useItem(){
        getItem().use(this);
        setItem(null);
    }

}

class MHM extends Pokemon {
    public MHM (){
        super("MentalHealthMan",100, 10, 30, 5, "BLACK", new Image("sample/mental_health_man.png"));
    }
}

class MNM extends Pokemon {
    public MNM (){
        super("MusicNoriNoriMan",50, 20, 60, 3, "WHITE", new Image("sample/music_norinori_man.png"));
    }
}

class darkelf extends Pokemon {
    public darkelf (){
        super("DarkElf", 40, 15, 60, 4, "BLACK", new Image("sample/fantasy_dark_elf.png"));
    }
}

class succubus extends Pokemon {
    public succubus (){
        super("Succubus",70, 2, 25, 4, "RED", new Image("sample/fantasy_succubus.png"));
    }
}

class KM extends Pokemon {
    public KM (){
        super("KenkenMan",50, 10, 5, 1, "GREEN", new Image("sample/kataashidachi_man.png"));
    }
}

class MT extends Pokemon {
    public MT (){
        super("ThunderMagician",60, 90, 2, 8, "GREEN", new Image("sample/mahoutsukai_thunder.png"));
    }
}

class seiryu extends Pokemon {
    public seiryu (){
        super("BlueDragon",100, 80, 10, 18, "BLUE", new Image("sample/fantasy_seiryu.png"));
    }
}
class unicorn extends Pokemon {
    public unicorn (){
        super("Unicorn",60, 35, 4, 8, "WHITE", new Image("sample/fantasy_unicorn.png"));
    }
}

class dragon extends Pokemon {
    public dragon (){
        super("RedDragon",150, 20, 5, 8, "RED", new Image("sample/fantasy_dragon.png"));
    }
}

class yamata extends Pokemon {
    public yamata (){
        super("YamataNoOrothi",40, 80, 4, 10, "BLUE", new Image("sample/fantasy_yamata.png"));
    }
}
