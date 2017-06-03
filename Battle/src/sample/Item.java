package sample;

/**
 * Created by you on 2017/05/25.
 */

abstract class Item {
    private String name;
    private int eff;
    public Item(String name, int eff){
        setName(name);
        setEff(eff);
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setEff(int eff) {
        this.eff = eff;
    }

    public int getEff() {
        return eff;
    }

    public void use(Pokemon p){}
}

class Heal extends Item{
    public Heal(String name, int eff){
        super(name, eff);
    }

    public void use(Pokemon p){
        p.setHP(p.getHP() + getEff());
    }
}