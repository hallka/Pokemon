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
    private Boolean rotRflag = false;
    private Boolean rotLflag = false;
    private Boolean itemflag = false;
    private Boolean attackflag = false;

    public Trainer(Pokemon left, Pokemon center, Pokemon right){
        setCenter(center);
        setLeft(left);
        setRight(right);
    }

    private void setCenter(Pokemon center) {
        this.center = center;
    }

    private void setLeft(Pokemon left) {
        this.left = left;
    }

    private void setRight(Pokemon right) {
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

    public Boolean checkLose(){
        if(!(left.isAlive()) && !(right.isAlive()) && !(center.isAlive())) return true;
        else return false;
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

    public Boolean getAttackflag() {
        return attackflag;
    }

    public Boolean getItemflag() {
        return itemflag;
    }

    public Boolean getRotLflag() {
        return rotLflag;
    }

    public Boolean getRotRflag() {
        return rotRflag;
    }

    public void setAttackflag(Boolean attackflag) {
        this.attackflag = attackflag;
    }

    public void setItemflag(Boolean itemflag) {
        this.itemflag = itemflag;
    }

    public void setRotLflag(Boolean rotLflag) {
        this.rotLflag = rotLflag;
    }

    public void setRotRflag(Boolean rotRflag) {
        this.rotRflag = rotRflag;
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

    public void setItem(Item litem, Item citem, Item ritem){
        this.left.setItem(litem);
        this.center.setItem(citem);
        this.right.setItem(ritem);
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
