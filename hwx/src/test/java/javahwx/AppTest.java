package javahwx;

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
        /*JusticeCharacter fireCalabash=new JusticeCharacter("fireCalabash",3,
                            new ArrayList<Coord>(Arrays.asList(new Coord[] {new Coord(0,0),new Coord(1,0),new Coord(0,1),new Coord(-1,0),new Coord(0,-1)})),
                            10,10,false,
                            new ArrayList<Coord>(Arrays.asList(new Coord[] {new Coord(1,0),new Coord(0,1),new Coord(-1,0),new Coord(0,-1)})));
        fireCalabash.placeHere(1,2);
        justiceArray.add(fireCalabash);
        JusticeCharacter waterCalabash=new JusticeCharacter("waterCalabash",4,
                            new ArrayList<Coord>(Arrays.asList(new Coord[] {new Coord(0,0),new Coord(1,0),new Coord(0,1),new Coord(-1,0),new Coord(0,-1)})),
                            9,9,false,
                            new ArrayList<Coord>(Arrays.asList(new Coord[] {new Coord(1,0),new Coord(0,1),new Coord(-1,0),new Coord(0,-1)})));
        waterCalabash.placeHere(1,3);
        justiceArray.add(waterCalabash);
        
        EvilCharacter snake=new EvilCharacter("snake",4,
                            new ArrayList<Coord>(Arrays.asList(new Coord[] {new Coord(0,0),new Coord(1,0),new Coord(0,1),new Coord(-1,0),new Coord(0,-1)})),
                            10,10,false,
                            new ArrayList<Coord>(Arrays.asList(new Coord[] {new Coord(1,0),new Coord(0,1),new Coord(-1,0),new Coord(0,-1)})));
        snake.placeHere(4,2);
        evilArray.add(snake);
        
        EvilCharacter scorpion=new EvilCharacter("scorpion",2,
                            new ArrayList<Coord>(Arrays.asList(new Coord[] {new Coord(0,0),new Coord(1,0),new Coord(0,1),new Coord(-1,0),new Coord(0,-1)})),
                            12,12,false,
                            new ArrayList<Coord>(Arrays.asList(new Coord[] {new Coord(1,0),new Coord(0,1),new Coord(-1,0),new Coord(0,-1)})));
        scorpion.placeHere(4,3);
        evilArray.add(scorpion);
        Character c;
        justicePointer=justiceArray.iterator();
        while (justicePointer.hasNext())
        {
            c=(JusticeCharacter)justicePointer.next();
            c.tellName();
        }

        c=(JusticeCharacter)justiceArray.iterator().next();
        try {
            c.move(new Coord(2,2));
        }catch (Exception e)
        {
            System.out.println(e);
        }
        justiceArray.set(0,(JusticeCharacter)c);
        
        justicePointer=justiceArray.iterator();
        while (justicePointer.hasNext())
        {
            c=(JusticeCharacter)justicePointer.next();
            System.out.println(c.tellPosition());
        }
        evilPointer=evilArray.iterator();
        while (evilPointer.hasNext())
        {
            c=(EvilCharacter)evilPointer.next();
            System.out.println(c.tellPosition());
        } */
    }
}
