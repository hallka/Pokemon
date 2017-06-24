package sample;

/**
 * Created by you on 2017/05/25.
 */

abstract class Item {
    private String name;
    private int eff;
    private String type;
    private int cost;

    public Item(String name, int eff, int cost){
        setName(name);
        setEff(eff);
        setCost(cost);
    }

    public Item(String name, String type, int cost){
        setName(name);
        setType(type);
        setCost(cost);
    }

    private void setName(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    private void setEff(int eff) {
        this.eff = eff;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public int getEff() {
        return eff;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getCost() {
        return cost;
    }

    public void use(Pokemon p){}
}

class Heal extends Item{
    public Heal(String name, int eff, int cost){
        super(name, eff, cost);
    }

    public void use(Pokemon p){
        p.setHP(p.getHP() + getEff());
    }
}

class Buff extends Item{
    public Buff(String name, int eff, int cost){super(name, eff, cost);}

    public void use(Pokemon p){
        p.setATK(p.getATK() + getEff());
    }
}

class Meta extends Item{
    public Meta(String name, String type, int cost){
        super(name, type, cost);
    }

    public void use(Pokemon p){
        p.setType(getType());
    }
}

class Mazai extends Heal{
    public Mazai(){
        super("Mazai", 30, 2);
    }
}

class Aburasoba extends Buff{
    public Aburasoba(){
        super("Aburasoba", 10, 4);
    }
}

class Coffee extends Meta{
    public Coffee(){
        super("Coffee", "BLACK", 6);
    }
}