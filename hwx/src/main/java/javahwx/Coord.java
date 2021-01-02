package javahwx;
public class Coord{
	private int x;
	private int y;
	static final int maxX=16;
	static final int maxY=12;
	public Coord(){}
	public Coord(Coord c){this.x=c.getX();this.y=c.getY();}
    public Coord(int X,int Y){this.x=X;this.y=Y;}
	public boolean isLegal(){return 0<=x&&x<maxX&&0<=y&&y<maxY;}
	public int getX(){return x;}
	public int getY(){return y;}
	public boolean equals(Coord coord){return coord.getX()==this.getX()&&coord.getY()==this.getY();}
    @Override
    public String toString() {return x+","+y;}
}