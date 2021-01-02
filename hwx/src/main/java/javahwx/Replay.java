package javahwx;
import java.io.*;
import java.util.*;
import java.text.*;
public class Replay{
    private String fileName;
    private File file;
    private DataOutputStream dos;
    private static Fight fight;
    private static File replayFile;
    public Replay(Camp camp) {   //创建一个回放文件
        Calendar calendar = Calendar.getInstance(); 
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");  
        fileName=formatter.format(calendar.getTime())+(camp==Camp.JUSTICE?"_J":"_E")+".gameplay";
        System.out.println(fileName);
        file=new File(fileName);
        try{
            file.createNewFile();
            dos=new DataOutputStream(new FileOutputStream(file));
            /*dos.writeInt(GameMsg.MOVE_MSG);
            dos.writeInt(4);
            dos.writeInt(0);
            dos.writeInt(0);
            dos.writeInt(GameMsg.ATTACK_MSG);
            dos.writeInt(4);
            dos.writeInt(2);
            dos.writeInt(GameMsg.NULL_MSG);
            dos.writeInt(4);
            dos.writeInt(-1);*///用-1来表示play结束
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    public static Fight getFight(){return fight;}
    public void writeMove(int id,int x,int y)
    {
        try{
            dos.writeInt(GameMsg.MOVE_MSG);
            dos.writeInt(id);
            dos.writeInt(x);
            dos.writeInt(y);
        }catch(IOException e){e.printStackTrace();}
    }
    public void writeAttack(int attacker,int attacked)
    {
        try{
            dos.writeInt(GameMsg.ATTACK_MSG);
            dos.writeInt(attacker);
            dos.writeInt(attacked);
        }catch(IOException e){e.printStackTrace();}
    }
    public void writeSkill(int skiller,int skilled)
    {
        try{
            dos.writeInt(GameMsg.SKILL_MSG);
            dos.writeInt(skiller);
            dos.writeInt(skilled);
        }catch(IOException e){e.printStackTrace();}
    }
    public void writeNull(int id)
    {
        try{
            dos.writeInt(GameMsg.NULL_MSG);
            dos.writeInt(id);
        }catch(IOException e){e.printStackTrace();}
    }
    public void writeEnd()
    {
        try{
            dos.writeInt(-1);
            dos.close();
        }catch(IOException e){e.printStackTrace();}
    }
    public static int setReplayFile(File file)
    {
        if (!file.exists()) return -1;
        String path=file.getName();
        int index=path.indexOf(".");
        if (!path.substring(index+1).equalsIgnoreCase("gameplay")) return -1;
        fight=new Fight(Camp.JUSTICE);
        fight.createCharacter();
        replayFile=file;
        return 0;
    }
    public static void startReplay()
    {
        //File file=new File(path);
        try{
            DataInputStream dis=new DataInputStream(new FileInputStream(replayFile));
            int msgType,x,y,id1,id2;
            while ((msgType=dis.readInt())!=-1)
            {
                switch (msgType) {
                    case GameMsg.MOVE_MSG:
                        id1=dis.readInt();
                        x=dis.readInt();
                        y=dis.readInt();
                        try{
                            fight.nextActCharacter().move(new Coord(x,y));
                            //fight.findID(id1).actionReset();
                        }catch (GameException e){e.printName();}
                        break;
                    case GameMsg.ATTACK_MSG:
                        id1=dis.readInt();
                        id2=dis.readInt();
                        try{
                            fight.nextActCharacter().normalAttack(fight.findID(id2));
                            //fight.findID(id1).actionReset();
                        }catch (GameException e){e.printName();}
                        break;
                    case GameMsg.NULL_MSG:
                        id1=dis.readInt();
                        try{
                            fight.nextActCharacter().nullAttack();
                            //fight.findID(id1).actionReset();
                        }catch (GameException e){e.printName();}
                        break;
                    case GameMsg.SKILL_MSG:
                        id1=dis.readInt();
                        id2=dis.readInt();
                        try{
                            fight.skillCharacter(fight.nextActCharacter(),fight.findID(id2));
                            //fight.findID(id1).actionReset();
                        }catch (GameException e){e.printName();}
                        break;
                    
                }
                try{
                    Thread.sleep(1000);//每秒进行一次动作
                }
                catch(InterruptedException e){e.printStackTrace();}
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    
}