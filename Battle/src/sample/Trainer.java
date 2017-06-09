package sample;

/**
 * Created by you on 2017/05/25.
 */

public class Trainer {
    private Boolean win = false;
    private Boolean rotable = true;
    private Pokemon left;
    private Pokemon center;
    private Pokemon right;

    public Trainer(Pokemon left, Pokemon center, Pokemon right){
        setCenter(center);
        setLeft(left);
        setRight(right);
    }

    public void setCenter(Pokemon center) {
        this.center = center;
    }

    public void setLeft(Pokemon left) {
        this.left = left;
    }

    public void setRight(Pokemon right) {
        this.right = right;
    }

    public Pokemon getCenter() {
        return center;
    }

    public Pokemon getLeft() {
        return left;
    }

    public Pokemon getRight() {
        return right;
    }


    public void setWin(){
        this.win = true;
    }

    public Boolean isWin() {
        return win;
    }


    public void setRotable(Boolean rotable) {
        this.rotable = rotable;
    }

    public Boolean isRotable() {
        return rotable;
    }


    public Boolean checkItem(){
        return (getCenter().getItem() != null);
    }

    public Boolean checkRefreshed(){
        return getCenter().isRefreshed();
    }

    public void rotR(){
        if(getLeft().isAlive()) {
            Pokemon p = getCenter();
            setCenter(getLeft());
            setLeft(getRight());
            setRight(p);
            setRotable(false);
        }
    }

    public void rotL(){
        if(getRight().isAlive()) {
            Pokemon p = getCenter();
            setCenter(getRight());
            setRight(getLeft());
            setLeft(p);
            setRotable(false);
        }
    }

    public void useItem(){
            getCenter().useItem();
    }

    public void attack(Pokemon p){
        if(!p.isAlive()) setWin();
        else {
            p.damage(getCenter().attack(p));
            getCenter().setRefresh(0);
        }
    }

    public void refresh(int r){
        if(!getLeft().isRefreshed()) getLeft().setRefresh(getLeft().getRefresh()+r*getLeft().getAGI());
        if(!getCenter().isRefreshed()) getCenter().setRefresh(getCenter().getRefresh()+r*getCenter().getAGI());
        if(!getRight().isRefreshed()) getRight().setRefresh(getRight().getRefresh()+r*getCenter().getAGI());
    }

}
