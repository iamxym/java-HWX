package javahwx;

import java.util.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit test for simple App.
 */
class AppTest {
    /**
     * Rigorous Test.
     */
    @Test
    void testApp() {
        assertEquals(1, 1);
    }
    @Test
    void testCreate()
    {
        ArrayList<JusticeCharacter> justiceArray=new ArrayList<>();
        ArrayList<EvilCharacter> evilArray=new ArrayList<>();
        JusticeCharacter fireCalabash=new JusticeCharacter("战士娃",3,
                            new ArrayList<Coord>(Arrays.asList(new Coord[] {new Coord(0,0),new Coord(1,0),new Coord(0,1),new Coord(-1,0),new Coord(0,-1)})),
                            10,10,6,true,
                            new ArrayList<Coord>(Arrays.asList(new Coord[] {new Coord(1,0),new Coord(0,1),new Coord(-1,0),new Coord(0,-1)})),new Skill());
        fireCalabash.placeHere(1,2);
        justiceArray.add(fireCalabash);
        JusticeCharacter waterCalabash=new JusticeCharacter("猎人娃",4,
                            new ArrayList<Coord>(Arrays.asList(new Coord[] {new Coord(0,0),new Coord(1,0),new Coord(0,1),new Coord(-1,0),new Coord(0,-1)})),
                            9,9,7,true,
                            new ArrayList<Coord>(Arrays.asList(new Coord[] {new Coord(1,0),new Coord(0,1),new Coord(-1,0),new Coord(0,-1)})),new Skill());
        waterCalabash.placeHere(1,3);
        justiceArray.add(waterCalabash);
        
        EvilCharacter snake=new EvilCharacter("蛇精",4,
                            new ArrayList<Coord>(Arrays.asList(new Coord[] {new Coord(0,0),new Coord(1,0),new Coord(0,1),new Coord(-1,0),new Coord(0,-1)})),
                            10,10,7,true,
                            new ArrayList<Coord>(Arrays.asList(new Coord[] {new Coord(1,0),new Coord(0,1),new Coord(-1,0),new Coord(0,-1)})),new Skill());
        snake.placeHere(4,2);
        evilArray.add(snake);
        
        EvilCharacter scorpion=new EvilCharacter("蝎子精",2,
                            new ArrayList<Coord>(Arrays.asList(new Coord[] {new Coord(0,0),new Coord(1,0),new Coord(0,1),new Coord(-1,0),new Coord(0,-1)})),
                            12,12,5,true,
                            new ArrayList<Coord>(Arrays.asList(new Coord[] {new Coord(1,0),new Coord(0,1),new Coord(-1,0),new Coord(0,-1)})),new Skill());
        scorpion.placeHere(4,3);
        evilArray.add(scorpion);
        Character c;
        Iterator<JusticeCharacter> justicePointer;
        Iterator<EvilCharacter> evilPointer;
        justicePointer=justiceArray.iterator();
        c=(JusticeCharacter)justicePointer.next();
        assertEquals(c.getName(),"战士娃");
        assertEquals(c.getPosition().getX(),1);
        assertEquals(c.getPosition().getY(),2);
        c=(JusticeCharacter)justicePointer.next();
        assertEquals(c.getName(),"猎人娃");
        

        //c=(JusticeCharacter)justiceArray.iterator().next();
        try {
            c.move(new Coord(2,3));
        }catch (GameException e)
        {
            e.printName();
        }
        assertEquals(c.getPosition().getX(),2);
        assertEquals(c.getPosition().getY(),3);
        evilPointer=evilArray.iterator();
        c=(EvilCharacter)evilPointer.next();
        assertEquals(c.getName(),"蛇精");
        assertEquals(c.getPosition().getX(),4);
        assertEquals(c.getPosition().getY(),2);
        c=(EvilCharacter)evilPointer.next();
        assertEquals(c.getName(),"蝎子精");
        assertEquals(c.getPosition().getX(),4);
        assertEquals(c.getPosition().getY(),3);
    }
}