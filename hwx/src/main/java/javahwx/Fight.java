package javahwx;
import java.net.*;
import java.io.*;
import java.util.*;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
public class Fight{
    ArrayList<JusticeCharacter> justiceArray;
    ArrayList<EvilCharacter> evilArray;
    private Map map;
    private int turnCount;//轮数
    private Camp camp;
    private Iterator<JusticeCharacter> justicePointer;
    private Iterator<EvilCharacter> evilPointer;
    private int propertyID;
    private Random seed;
    private int[] randomVal;
    /*
    希望的战斗流程——按照状态机的格式进行
    1.当前回合开始
    2.程序选择此时应当行动的角色，如果存在这样的角色（不为null）则跳到3，否则跳到6
    3.对应阵营的玩家控制该角色移动
    4.对应阵营的玩家控制该角色普攻/释放技能/待机
    5.回到步骤2
    6.当前回合结束，判断战斗是否结束，未结束则回合数++（准备下一回合），所有角色的状态进行重置（回到可以移动/操作的状态），回到步骤1
    */
    public Fight(Camp camp)
    {
        this.camp=camp;
        this.map=new Map();
        this.turnCount=1;
        this.seed=new Random(19260817);
        this.justiceArray=new ArrayList<>();
        this.evilArray=new ArrayList<>();
        this.propertyID=-1;
    }
    public Character findID(int id)
    {
        for (Character character:justiceArray)
            if (character.getID()==id) return character;
        for (Character character:evilArray)
            if (character.getID()==id) return character;
        return null;
    }
    public int getTurnCount(){return turnCount;}
    public Camp getCamp(){return camp;}
    public Character coordToCharacter(Coord pos) throws OutOfRangeException
    {
        for (Character character:justiceArray)
            if (character.getPosition().equals(pos)) return character;
        for (Character character:evilArray)
            if (character.getPosition().equals(pos)) return character;
        throw new OutOfRangeException();
    }
    public void moveCharacter(Coord pos) throws OutOfRangeException,OccupiedException,HasMovedException
    {
        if (nextActCharacter().getPosition().equals(pos)) nextActCharacter().move(pos);
        else
        {
            for (Character character:justiceArray)
                if (character.getPosition().equals(pos)) throw new OccupiedException();
            for (Character character:evilArray)
                if (character.getPosition().equals(pos)) throw new OccupiedException();
            nextActCharacter().move(pos);
        }
    }
    public void skillCharacter(Character skiller,Character skilled) throws InvalidGoalException,NoSkillException,OutOfRangeException,HasAttackedException
    {
        //Character now=nextActCharacter();
        if (skiller.getSkill()==null) throw new NoSkillException();
        //Character character=coordToCharacter(pos);
        if ((skiller.getCamp()==skilled.getCamp()&&skiller.getSkill().Objcamp==1)||(skiller.getCamp()!=skilled.getCamp()&&skiller.getSkill().Objcamp==0));
        else throw new InvalidGoalException();
        Coord pos=skilled.getPosition();
        switch (skiller.skillAttack(skilled))
        {
            case HEAL:break;
            case GAZE:break;
            case SNIPE:break;
            case CUT:
                try{
                    skilled=coordToCharacter(new Coord(pos.getX(),pos.getY()+1));
                    if (skilled.getCamp()!=skiller.getCamp()) skilled.getAttacked(2);
                }catch (OutOfRangeException ore){}
                try{
                    skilled=coordToCharacter(new Coord(pos.getX(),pos.getY()-1));
                    if (skilled.getCamp()!=skiller.getCamp()) skilled.getAttacked(2);
                }catch (OutOfRangeException ore){}
                try{
                    skilled=coordToCharacter(new Coord(pos.getX()-1,pos.getY()));
                    if (skilled.getCamp()!=skiller.getCamp()) skilled.getAttacked(2);
                }catch (OutOfRangeException ore){}
                try{
                    skilled=coordToCharacter(new Coord(pos.getX()+1,pos.getY()));
                    if (skilled.getCamp()!=skiller.getCamp()) skilled.getAttacked(2);
                }catch (OutOfRangeException ore){}
                break;
            case SWAP:
                Coord coord=skilled.getPosition();
                skilled.placeHere(skiller.getPosition().getX(),skiller.getPosition().getY());
                skiller.placeHere(coord.getX(),coord.getY());
                break;
            case VAMPIRE:
                skiller.getAttacked(-3);
                break;
        }
    }
    public void createCharacter()//创建相关角色
    {
        Character.idReset();
        justiceArray.clear();
        evilArray.clear();
        
        JusticeCharacter jc=new JusticeCharacter("战士娃",5,
                            new ArrayList<Coord>(Arrays.asList(new Coord[] {new Coord(0,0),new Coord(1,0),new Coord(0,1),new Coord(-1,0),new Coord(0,-1),
                            new Coord(1,-1),new Coord(1,1),new Coord(-1,1),new Coord(-1,-1)})),
                            18,18,4,false,
                            new ArrayList<Coord>(Arrays.asList(new Coord[] {new Coord(0,0),new Coord(1,0),new Coord(0,1),new Coord(-1,0),new Coord(0,-1)})),
                            //new SwapSkill()
                            null
                            );
        jc.placeHere(3,2);
        justiceArray.add(jc);
        
        jc=new JusticeCharacter("盗贼娃",4,
                            new ArrayList<Coord>(Arrays.asList(new Coord[] {
                                new Coord(0,0),new Coord(1,0),new Coord(0,1),new Coord(-1,0),new Coord(0,-1),
                                new Coord(1,-1),new Coord(1,1),new Coord(-1,1),new Coord(-1,-1),
                                new Coord(-2,-2),new Coord(-2,-1),new Coord(-2,0),new Coord(-2,1),new Coord(-2,2),
                                new Coord(2,-2),new Coord(2,-1),new Coord(2,0),new Coord(2,1),new Coord(2,2),
                                new Coord(-1,-2),new Coord(0,-2),new Coord(1,-2),new Coord(-1,2),new Coord(0,2),new Coord(1,2),
                                new Coord(-3,0),new Coord(3,0),new Coord(0,-3),new Coord(0,3)})),
                            12,12,8,false,
                            new ArrayList<Coord>(Arrays.asList(new Coord[] {new Coord(0,0),new Coord(1,0),new Coord(0,1),new Coord(-1,0),new Coord(0,-1)})),
                            new SwapSkill()
                            //null
                            );
        jc.placeHere(3,4);
        justiceArray.add(jc);

        jc=new JusticeCharacter("骑士娃",4,
                            new ArrayList<Coord>(Arrays.asList(new Coord[] {new Coord(0,0),new Coord(1,0),new Coord(0,1),new Coord(-1,0),new Coord(0,-1),
                                new Coord(1,-1),new Coord(1,1),new Coord(-1,1),new Coord(-1,-1)})),
                            20,20,4,false,
                            new ArrayList<Coord>(Arrays.asList(new Coord[] {new Coord(0,0),new Coord(1,0),new Coord(0,1),new Coord(-1,0),new Coord(0,-1),
                                new Coord(1,-1),new Coord(1,1),new Coord(-1,1),new Coord(-1,-1)})),
                            //new SwapSkill()
                            null
                            );
        jc.placeHere(3,6);
        justiceArray.add(jc);

        jc=new JusticeCharacter("术士娃",4,
                            new ArrayList<Coord>(Arrays.asList(new Coord[] {new Coord(0,0),new Coord(1,0),new Coord(0,1),new Coord(-1,0),new Coord(0,-1),
                                new Coord(1,-1),new Coord(1,1),new Coord(-1,1),new Coord(-1,-1),
                                new Coord(-2,0),new Coord(2,0),new Coord(0,-2),new Coord(0,2)})),
                            18,18,6,false,
                            new ArrayList<Coord>(Arrays.asList(new Coord[] {new Coord(0,0),new Coord(1,0),new Coord(0,1),new Coord(-1,0),new Coord(0,-1),
                                new Coord(1,-1),new Coord(1,1),new Coord(-1,1),new Coord(-1,-1)})),
                            //new SwapSkill()
                            null
                            );
        jc.placeHere(3,8);
        justiceArray.add(jc);

        jc=new JusticeCharacter("猎人娃",5,
                            new ArrayList<Coord>(Arrays.asList(new Coord[] {new Coord(0,0),new Coord(1,0),new Coord(0,1),new Coord(-1,0),
                                new Coord(0,-1),new Coord(1,-1),new Coord(1,1),new Coord(-1,1),new Coord(-1,-1),
                                new Coord(-2,0),new Coord(2,0),new Coord(0,-2),new Coord(0,2)})),
                            12,12,7,false,
                            new ArrayList<Coord>(Arrays.asList(new Coord[] {new Coord(0,0),new Coord(1,0),new Coord(0,1),new Coord(-1,0),
                                new Coord(0,-1),new Coord(1,-1),new Coord(1,1),new Coord(-1,1),new Coord(-1,-1),
                                new Coord(-2,0),new Coord(2,0),new Coord(0,-2),new Coord(0,2)})),
                            new SnipeSkill()
                           // null
                            );
        jc.placeHere(2,3);
        justiceArray.add(jc);

        jc=new JusticeCharacter("法师娃",5,
                            new ArrayList<Coord>(Arrays.asList(new Coord[] {new Coord(0,0),new Coord(1,0),new Coord(0,1),new Coord(-1,0),
                                new Coord(0,-1),new Coord(1,-1),new Coord(1,1),new Coord(-1,1),new Coord(-1,-1),
                                new Coord(-2,0),new Coord(2,0),new Coord(0,-2),new Coord(0,2)})),
                            14,14,5,false,
                            new ArrayList<Coord>(Arrays.asList(new Coord[] {new Coord(0,0),new Coord(1,0),new Coord(0,1),new Coord(-1,0),
                                new Coord(0,-1),new Coord(1,-1),new Coord(1,1),new Coord(-1,1),new Coord(-1,-1)})),
                            //new SwapSkill()
                            null
                            );
        jc.placeHere(2,5);
        justiceArray.add(jc);

        jc=new JusticeCharacter("牧师娃",3,
                            new ArrayList<Coord>(Arrays.asList(new Coord[] {new Coord(0,0),new Coord(1,0),new Coord(0,1),new Coord(-1,0),
                                new Coord(0,-1),new Coord(1,-1),new Coord(1,1),new Coord(-1,1),new Coord(-1,-1),
                                new Coord(-2,0),new Coord(2,0),new Coord(0,-2),new Coord(0,2)})),
                            16,16,5,false,
                            new ArrayList<Coord>(Arrays.asList(new Coord[] {new Coord(0,0),new Coord(1,0),new Coord(0,1),new Coord(-1,0),
                                new Coord(0,-1),new Coord(1,-1),new Coord(1,1),new Coord(-1,1),new Coord(-1,-1)})),
                            new HealSkill()
                            //null
                            );
        jc.placeHere(2,7);
        justiceArray.add(jc);
        
        
        EvilCharacter ec=new EvilCharacter("蝎子精",4,
                            new ArrayList<Coord>(Arrays.asList(new Coord[] {new Coord(0,0),new Coord(1,0),new Coord(0,1),new Coord(-1,0),new Coord(0,-1),
                            new Coord(1,-1),new Coord(1,1),new Coord(-1,1),new Coord(-1,-1)})),
                            18,18,3,false,
                            new ArrayList<Coord>(Arrays.asList(new Coord[] {new Coord(0,0),new Coord(1,0),new Coord(0,1),new Coord(-1,0),new Coord(0,-1)})),
                            new CutSkill()
                            //null
                            );
        ec.placeHere(12,2);
        evilArray.add(ec);

        ec=new EvilCharacter("蜘蛛精",5,
                            new ArrayList<Coord>(Arrays.asList(new Coord[] {
                                new Coord(0,0),new Coord(1,0),new Coord(0,1),new Coord(-1,0),new Coord(0,-1),
                                new Coord(1,-1),new Coord(1,1),new Coord(-1,1),new Coord(-1,-1),
                                new Coord(-2,-2),new Coord(-2,-1),new Coord(-2,0),new Coord(-2,1),new Coord(-2,2),
                                new Coord(2,-2),new Coord(2,-1),new Coord(2,0),new Coord(2,1),new Coord(2,2),
                                new Coord(-1,-2),new Coord(0,-2),new Coord(1,-2),new Coord(-1,2),new Coord(0,2),new Coord(1,2)})),
                            14,14,7,false,
                            new ArrayList<Coord>(Arrays.asList(new Coord[] {new Coord(0,0),new Coord(1,0),new Coord(0,1),new Coord(-1,0),new Coord(0,-1)})),
                            null
                            );
        ec.placeHere(12,4);
        evilArray.add(ec);

        ec=new EvilCharacter("蛤蟆精",4,
                            new ArrayList<Coord>(Arrays.asList(new Coord[] {new Coord(0,0),new Coord(1,0),new Coord(0,1),new Coord(-1,0),new Coord(0,-1),
                                new Coord(1,-1),new Coord(1,1),new Coord(-1,1),new Coord(-1,-1)})),
                            20,20,4,false,
                            new ArrayList<Coord>(Arrays.asList(new Coord[] {new Coord(0,0),new Coord(1,0),new Coord(0,1),new Coord(-1,0),new Coord(0,-1),
                                new Coord(1,-1),new Coord(1,1),new Coord(-1,1),new Coord(-1,-1)})),
                            //new SwapSkill()
                            null
                            );
        ec.placeHere(12,6);
        evilArray.add(ec);

        ec=new EvilCharacter("蜈蚣精",4,
                            new ArrayList<Coord>(Arrays.asList(new Coord[] {new Coord(0,0),new Coord(1,0),new Coord(0,1),new Coord(-1,0),new Coord(0,-1),
                                new Coord(1,-1),new Coord(1,1),new Coord(-1,1),new Coord(-1,-1)})),
                            18,18,6,false,
                            new ArrayList<Coord>(Arrays.asList(new Coord[] {new Coord(0,0),new Coord(1,0),new Coord(0,1),new Coord(-1,0),new Coord(0,-1),
                                new Coord(1,-1),new Coord(1,1),new Coord(-1,1),new Coord(-1,-1)})),
                            //new SwapSkill()
                            null
                            );
        ec.placeHere(12,8);
        evilArray.add(ec);

        ec=new EvilCharacter("马蜂",6,
                            new ArrayList<Coord>(Arrays.asList(new Coord[] {new Coord(0,0),new Coord(1,0),new Coord(0,1),new Coord(-1,0),
                                new Coord(0,-1),new Coord(1,-1),new Coord(1,1),new Coord(-1,1),new Coord(-1,-1),
                                new Coord(-2,0),new Coord(2,0),new Coord(0,-2),new Coord(0,2)})),
                            12,12,7,false,
                            new ArrayList<Coord>(Arrays.asList(new Coord[] {new Coord(0,0),new Coord(1,0),new Coord(0,1),new Coord(-1,0),
                                new Coord(0,-1),new Coord(1,-1),new Coord(1,1),new Coord(-1,1),new Coord(-1,-1),
                                new Coord(-2,0),new Coord(2,0),new Coord(0,-2),new Coord(0,2)})),
                            //new SwapSkill()
                            null
                            );
        ec.placeHere(13,3);
        evilArray.add(ec);

        ec=new EvilCharacter("蝙蝠",5,
                            new ArrayList<Coord>(Arrays.asList(new Coord[] {new Coord(0,0),new Coord(1,0),new Coord(0,1),new Coord(-1,0),
                                new Coord(0,-1),new Coord(1,-1),new Coord(1,1),new Coord(-1,1),new Coord(-1,-1),
                                new Coord(-2,0),new Coord(2,0),new Coord(0,-2),new Coord(0,2)})),
                            14,14,5,false,
                            new ArrayList<Coord>(Arrays.asList(new Coord[] {new Coord(0,0),new Coord(1,0),new Coord(0,1),new Coord(-1,0),
                                new Coord(0,-1),new Coord(1,-1),new Coord(1,1),new Coord(-1,1),new Coord(-1,-1)})),
                            new VampireSkill()
                            //null
                            );
        ec.placeHere(13,7);
        evilArray.add(ec);
        ec=new EvilCharacter("蛇精",4,
                            new ArrayList<Coord>(Arrays.asList(new Coord[] {new Coord(0,0),new Coord(1,0),new Coord(0,1),new Coord(-1,0),
                                new Coord(0,-1),new Coord(1,-1),new Coord(1,1),new Coord(-1,1),new Coord(-1,-1),new Coord(-2,0),new Coord(2,0),new Coord(0,-2),new Coord(0,2)
                            })),
                            16,16,5,false,
                            new ArrayList<Coord>(Arrays.asList(new Coord[] {new Coord(0,0),new Coord(1,0),new Coord(0,1),new Coord(-1,0),
                                new Coord(0,-1),new Coord(1,-1),new Coord(1,1),new Coord(-1,1),new Coord(-1,-1)})),
                            new GazeSkill()
                            //null
                            );
        ec.placeHere(13,5);
        evilArray.add(ec);

        randomVal=new int[justiceArray.size()+evilArray.size()];
        for (int i=0;i<randomVal.length;++i) randomVal[i]=i;
        for (int tmp,j,i=randomVal.length-1;i>0;--i)
        {
            j=seed.nextInt(i+1);
            tmp=randomVal[i];
            randomVal[i]=randomVal[j];
            randomVal[j]=tmp;
        }
    }
    public void setPropertyID(MouseEvent e)
    {
        for (JusticeCharacter jc:justiceArray)
              if (jc.isCollisionWith(e.getX(),e.getY()))
                propertyID=jc.getID();
        for (EvilCharacter ec:evilArray)
            if (ec.isCollisionWith(e.getX(),e.getY()))
                propertyID=ec.getID();
    }
    public void drawCharacter(GraphicsContext gc)
    {
        int nowId=nextActCharacter().getID();
        for (JusticeCharacter jc:justiceArray) jc.draw(gc,propertyID,nowId);
        for (EvilCharacter ec:evilArray) ec.draw(gc,propertyID,nowId);
    }
    public Character nextActCharacter()
    {
        int maxSpeed=-999,i=0,j=0;
        Character ret=null;
        for (Character c:justiceArray)
        {
            if (((c.isDead()==false)&&(c.hasAttacked()==false))&&(maxSpeed<c.getSpeed()||(maxSpeed==c.getSpeed()&&randomVal[i]>randomVal[j])))//速度相同则需要随机确定
            {
                maxSpeed=c.getSpeed();
                ret=c;
                j=i;
            }
            ++i;
        }
        for (Character c:evilArray)
        {
            if (((c.isDead()==false)&&(c.hasAttacked()==false))&&(maxSpeed<c.getSpeed()||(maxSpeed==c.getSpeed()&&randomVal[i]>randomVal[j])))//速度相同则需要随机确定
            {
                maxSpeed=c.getSpeed();
                ret=c;
                j=i;
            }
            ++i;
        }
        if (maxSpeed==-999){actionResetAll();++turnCount;return nextActCharacter();}
        else return ret;
    }
    public Camp nextActCamp(){return nextActCharacter().getCamp();}
    public void actionResetAll()//所有角色的状态进行重置（回到可以移动/操作的状态）
    {
        Character c;
        for (int i=0;i<justiceArray.size();++i)
        {
            c=(JusticeCharacter)justiceArray.get(i);
            c.actionReset();
            justiceArray.set(i,(JusticeCharacter)c);
        }
        for (int i=0;i<evilArray.size();++i)
        {
            c=(EvilCharacter)evilArray.get(i);
            c.actionReset();
            evilArray.set(i,(EvilCharacter)c);
        }
        for (int tmp,j,i=randomVal.length-1;i>0;--i)
        {
            j=seed.nextInt(i+1);
            tmp=randomVal[i];
            randomVal[i]=randomVal[j];
            randomVal[j]=tmp;
        }
    }
    public boolean waitMsg(GameProtocol gp,Replay replay)
    {
        Character character=nextActCharacter();
        byte[] buf = new byte[1024];
        DatagramPacket dp=gp.receive(buf);
        ByteArrayInputStream bais = new ByteArrayInputStream(buf, 0, dp.getLength());
        DataInputStream dis = new DataInputStream(bais);
        int msgType = 0;
        try {
            msgType = dis.readInt();//先拿到消息的类型
        } catch (IOException e) {
            e.printStackTrace();
        }
        switch (msgType){
            case GameMsg.MOVE_MSG:
                MoveMsg.parse(dis,this,replay);
                break;
            case GameMsg.ATTACK_MSG:
                AttackMsg.parse(dis,this,replay);
                break;
            case GameMsg.SKILL_MSG:
                SkillMsg.parse(dis,this,replay);
                break;
            case GameMsg.NULL_MSG:
                NullMsg.parse(dis,this,replay);
                break;
        }
        return (nextActCharacter().getCamp()==camp);
    }
    public boolean isOver()
    {
        int flag=0;
        for (Character c:justiceArray)
            if (c.isDead()==false)
            {
                flag=1;
                break;
            }
        if (flag==0) return true;//有一方全部死亡，则战斗结束
        flag=0;
        for (Character c:evilArray)
            if (c.isDead()==false)
            {
                flag=1;
                break;
            }
        if (flag==0) return true;
        return false;
    }
}