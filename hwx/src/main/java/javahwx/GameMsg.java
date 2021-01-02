package javahwx;
import java.io.*;
import java.util.*;
public interface GameMsg{
    public static final int MOVE_MSG=1;//移动
    public static final int ATTACK_MSG=2;//攻击
    public static final int NULL_MSG=3;//无行动
    public static final int SKILL_MSG=4;//技能
    //感觉需要像tank一样再建一层Client，用于实现GameMsg的多态，即根据MsgType的不同构建不同的Msg并调用parse
    public void send(GameProtocol gp);//将对应Msg内的参数（如移动位置，棋子编号等）打包成packet并调用GameProtocol的send
    //public void parse(DataInputStream dis,Fight fight);//用于解释“已经获得信息类型的packet”
}
class MoveMsg implements GameMsg{
    private int msgType=MOVE_MSG;//标注为移动Msg
    private int id;//移动的棋子id
    private int moveToX,moveToY;//移动到的位置(x,y)
    public MoveMsg(int id,Coord coord)
    {
        this.id=id;
        this.moveToX=coord.getX();
        this.moveToY=coord.getY();
    }
    @Override
    public void send(GameProtocol gp)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(100);//指定大小, 免得字节数组扩容占用时间
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            dos.writeInt(msgType);
            dos.writeInt(id);
            dos.writeInt(moveToX);
            dos.writeInt(moveToY);
        } catch (IOException e) {
            e.printStackTrace();
        }
        gp.send(baos);
    }
    //@Override
    public static void parse(DataInputStream dis,Fight fight,Replay replay)
    {
        try{
            int characterID=dis.readInt(),x=dis.readInt(),y=dis.readInt();
            Coord coord=new Coord(x,y);
            fight.findID(characterID).move(coord);
            replay.writeMove(characterID,coord.getX(),coord.getY());
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
class AttackMsg implements GameMsg{
    private int msgType=ATTACK_MSG;//标注为攻击Msg
    private int attackerID,attackedID;//攻击者id和受攻击者id
    public AttackMsg(int attackerID,int attackedID)
    {
        this.attackerID=attackerID;
        this.attackedID=attackedID;
    }
    @Override
    public void send(GameProtocol gp)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(100);//指定大小, 免得字节数组扩容占用时间
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            dos.writeInt(msgType);
            dos.writeInt(attackerID);
            dos.writeInt(attackedID);
        } catch (IOException e) {
            e.printStackTrace();
        }
        gp.send(baos);
    }
    //@Override
    public static void parse(DataInputStream dis,Fight fight,Replay replay)
    {
        try{
            int attackerID=dis.readInt(),attackedID=dis.readInt();
            fight.findID(attackerID).normalAttack(fight.findID(attackedID));
            replay.writeAttack(attackerID,attackedID);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
class SkillMsg implements GameMsg{
    private int msgType=SKILL_MSG;//标注为攻击Msg
    private int skillerID,skilledID;//攻击者id和受攻击者id
    public SkillMsg(int skillerID,int skilledID)
    {
        this.skillerID=skillerID;
        this.skilledID=skilledID;
    }
    @Override
    public void send(GameProtocol gp)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(100);//指定大小, 免得字节数组扩容占用时间
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            dos.writeInt(msgType);
            dos.writeInt(skillerID);
            dos.writeInt(skilledID);
        } catch (IOException e) {
            e.printStackTrace();
        }
        gp.send(baos);
    }
    //@Override
    public static void parse(DataInputStream dis,Fight fight,Replay replay)
    {
        try{
            int skillerID=dis.readInt(),skilledID=dis.readInt();
            fight.skillCharacter(fight.findID(skillerID),fight.findID(skilledID));
            replay.writeSkill(skillerID,skilledID);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
class NullMsg implements GameMsg{
    private int msgType=NULL_MSG;//标注为空Msg
    private int id;//未动的角色编号
    public NullMsg(int id)
    {
        this.id=id;
    }
    @Override
    public void send(GameProtocol gp)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(100);//指定大小, 免得字节数组扩容占用时间
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            dos.writeInt(msgType);
            dos.writeInt(id);
        } catch (IOException e) {
            e.printStackTrace();
        }
        gp.send(baos);
    }
    //@Override
    public static void parse(DataInputStream dis,Fight fight,Replay replay)
    {
        try{
            int id=dis.readInt();
            fight.findID(id).nullAttack();
            replay.writeNull(id);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}