package structures;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Vector2d {
    public final int x ;
    public final int y;

    public Vector2d(int x,int y){
        this.x=x;
        this.y=y;
    }

    @Override
    public String toString(){
        return String.format("(%d, %d)", this.x, this.y);
    }

    public boolean precedes(Vector2d vector) {
        return this.x <= vector.x && this.y <= vector.y;

    }

    public boolean follows(Vector2d vector) {
        return this.x >= vector.x && this.y >= vector.y;

    }

    public Vector2d upperRight(Vector2d vector) {
        return new Vector2d(max(this.x, vector.x), max(this.y, vector.y));
    }

    public Vector2d lowerLeft(Vector2d vector) {
        return new Vector2d(min(this.x, vector.x), min(this.y, vector.y));
    }

    public Vector2d add(Vector2d vector) {
        return new Vector2d(this.x + vector.x, this.y + vector.y);
    }

    public Vector2d subtract(Vector2d vector) {
        return new Vector2d(this.x - vector.x, this.y - vector.y);
    }

    @Override
    public boolean equals(Object vector) {
        if (this == vector) return true;
        if (!(vector instanceof Vector2d)) return false;
        Vector2d that = (Vector2d) vector;
        return that.x == this.x && that.y == this.y;

    }

    public Vector2d opposite() {
        return new Vector2d(-this.x, -this.y);
    }

    @Override
    public int hashCode(){
        int hash=13;
        hash+=this.x *31;
        hash+=this.y*17;
        return hash;
    }

    public int getX(){ return this.x; }

    public int getY(){
        return this.y;
    }

}


