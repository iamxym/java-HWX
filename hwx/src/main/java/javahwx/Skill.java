package javahwx;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.canvas.*;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.*;
import java.util.*;
enum SkillClassifier{
    HEAL,GAZE,CUT,SWAP,NULL;
}
public class Skill{
    public ArrayList<Coord> skillRange;
    public int Objcamp;//0不同阵营，1相同阵营
    public SkillClassifier doSkill(Character character){return SkillClassifier.NULL;};
    Skill(){}
    public Skill(int flag,ArrayList<Coord> Range)
    {
        this.Objcamp=flag;
        this.skillRange=Range;
    }
}
class HealSkill extends Skill{
    public HealSkill()
    {
        super(1,new ArrayList<Coord>(Arrays.asList(new Coord[] {
            new Coord(0,0),new Coord(1,0),new Coord(0,1),new Coord(-1,0),
            new Coord(0,-1),new Coord(1,-1),new Coord(1,1),new Coord(-1,1),new Coord(-1,-1)
        })));
    }
    @Override
    public SkillClassifier doSkill(Character character)//加3血
    {
        character.getAttacked(-3);
        return SkillClassifier.HEAL;
    }
}
class GazeSkill extends Skill{
    public GazeSkill()
    {
        super(0,new ArrayList<Coord>(Arrays.asList(new Coord[] {
            new Coord(0,0),new Coord(1,0),new Coord(0,1),new Coord(-1,0),
            new Coord(0,-1),new Coord(1,-1),new Coord(1,1),new Coord(-1,1),new Coord(-1,-1),
            new Coord(-2,-2),new Coord(-2,-1),new Coord(-2,0),new Coord(-2,1),new Coord(-2,2),
            new Coord(2,-2),new Coord(2,-1),new Coord(2,0),new Coord(2,1),new Coord(2,2),
            new Coord(-1,-2),new Coord(0,-2),new Coord(1,-2),new Coord(-1,2),new Coord(0,2),new Coord(1,2)
        })));
    }
    @Override
    public SkillClassifier doSkill(Character character)//单体-2血并减1速
    {
        character.getAttacked(2);
        character.reduceSpeed(1);
        return SkillClassifier.GAZE;
    }
}
class CutSkill extends Skill{
    public CutSkill()
    {
        super(0,new ArrayList<Coord>(Arrays.asList(new Coord[] {
            new Coord(0,0),new Coord(1,0),new Coord(0,1),new Coord(-1,0),
            new Coord(0,-1),new Coord(1,-1),new Coord(1,1),new Coord(-1,1),new Coord(-1,-1),
            new Coord(-2,0),new Coord(2,0),new Coord(0,-2),new Coord(0,2),
        })));
    }
    @Override
    public SkillClassifier doSkill(Character character)//十字范围内减1血，主要目标-2血
    {
        character.getAttacked(2);
        return SkillClassifier.CUT;
    }
}
class SwapSkill extends Skill{
    public SwapSkill()
    {
        super(0,new ArrayList<Coord>(Arrays.asList(new Coord[] {
            new Coord(0,0),new Coord(1,0),new Coord(0,1),new Coord(-1,0),
            new Coord(0,-1),new Coord(1,-1),new Coord(1,1),new Coord(-1,1),new Coord(-1,-1),
            new Coord(-2,0),new Coord(2,0),new Coord(0,-2),new Coord(0,2),
        })));
    }
    @Override
    public SkillClassifier doSkill(Character character)//单体-3血并换位
    {
        character.getAttacked(2);
        return SkillClassifier.SWAP;
    }
}