package javahwx;
import java.util.*;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
public class Map{
    public static final int maxX=16;
    public static final int maxY=12;
    public static final int pixelHeight=720;
    public static final int pixelWidth=1280;
    public static final int gridHeight=60;
    public static final int gridWidth=80;
    private Image image;
    /*public Map(Image image)
    {
        this.image=image;
    }*/
    public static void drawRange(Coord source,ArrayList<Coord> range,GraphicsContext gc,Color color)
    {
        if (range==null) return;
        Coord pos;
        for (Coord coord:range)
        {
            pos=new Coord(source.getX()+coord.getX(),source.getY()+coord.getY());
            if (pos.isLegal()==false) continue;
            gc.save();
            gc.setGlobalAlpha(0.4f);
			gc.setFill(color);
            gc.fillRect(pos.getX()*gridWidth+MapCanvas.startLeft, pos.getY()*gridHeight+MapCanvas.startUp, gridWidth, gridHeight);
            gc.restore();
        }
    }
    public static boolean isCollisionWith(double gridX,double gridY,double x,double y)
    {
        return (gridX<=x&&x<gridX+Map.gridWidth)&&(gridY<=y&&y<gridY+Map.gridHeight);
    }
    public static Coord collisionCoord(double x,double y)
    {
        for (int i=0;i<maxX;++i)
            for (int j=0;j<maxY;++j)
                if (isCollisionWith(i*gridWidth+MapCanvas.startLeft,j*gridHeight+MapCanvas.startUp,x,y))
                    return new Coord(i,j);
        return new Coord(-1,-1);
    }
}