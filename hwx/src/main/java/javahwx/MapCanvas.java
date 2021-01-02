package javahwx;
import java.util.*;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.event.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.scene.control.*;
//保存地图相关信息
enum UserState{
    Start,//游戏初始界面
    ServerConnecting,//服务器输出IP/Port等待客户端连接
    ClientConnecting,//客户端连接到服务器，需要向文本框中输入IP/Port
    Replay,//战斗回放状态
    MoveFree,AttackFree,Skilling,Attacking,Moving,Waiting//战斗时的五种状态
};
public class MapCanvas extends Canvas{
    private Fight fight;
    private GraphicsContext gc;
    private Image startimage;
    private boolean isRunning=true;
    private long sleep=100;
    public static final int startLeft=(1452-1280)/2;
    public static final int startUp=(876-720)/2;
    private UserState userState;
    private GameProtocol gp;
    private boolean showAlert;
    private Button[] button=new Button[4];
    private Replay replay;
    private Thread thread=new Thread(new Runnable(){
        @Override
        public void run(){
            while (isRunning){
                
                Platform.runLater(()->{
                    //System.out.println(getState().toString());
                    draw();update();
                });
                try{
                    Thread.sleep(sleep);
                }
                catch(InterruptedException e){e.printStackTrace();}
            }
        }
    });
    private Thread thread2=new Thread(new Runnable(){
        @Override
        public void run(){
            while (isRunning){
                
                if (userState==UserState.Waiting)
                {
                    boolean ret=fight.waitMsg(gp,replay);
                    if (fight.isOver())
                    {
                        replay.writeEnd();
                        userState=UserState.Start;
                        showAlert=true;
                    }
                    else if (ret) userState=UserState.MoveFree;
                }
                else if (userState==UserState.ServerConnecting)
                {
                    startFight();
                }
                else if (userState==UserState.Replay)
                {
                    Replay.startReplay();
                    userState=UserState.Start;
                }
                try{
                    Thread.sleep(sleep);
                }
                catch(InterruptedException e){e.printStackTrace();}
            }
        }
    });
    public MapCanvas(double width,double height,Button[] btn)
    {
        super(width,height);
        button[0]=btn[0];
        button[1]=btn[1];
        button[2]=btn[2];
        button[3]=btn[3];
        gc=getGraphicsContext2D();
        startimage=new Image("file:image/0001.jpg");
        userState=UserState.Start;
        showAlert=false;
        //resetFight(camp,btn1,btn2);//Notice 网络链接后可能要改的地方
        //if (camp==Camp.JUSTICE) gp=new GameServer();
        //else gp=new GameClient(ip,port);
        //gp.init();
        setOnMousePressed(event->{
            if (userState==UserState.MoveFree||userState==UserState.Waiting||userState==UserState.AttackFree)
            {
                fight.setPropertyID(event);
            }
            else if (userState==UserState.Replay)
            {
                Replay.getFight().setPropertyID(event);
            }
            else if (userState==UserState.Moving)
            {
                try{
                    Coord coord=Map.collisionCoord(event.getX(),event.getY());
                    int id=fight.nextActCharacter().getID();
                    MoveMsg moveMsg=new MoveMsg(id,coord);
                    fight.moveCharacter(coord);
                    moveMsg.send(gp);
                    replay.writeMove(id,coord.getX(),coord.getY());
                    userState=UserState.AttackFree;
                    btn[0].setStyle("-fx-text-fill:grey");
                    btn[1].setStyle("-fx-text-fill:grey");
                    btn[3].setStyle("-fx-text-fill:grey");
                }catch (GameException ge){ge.printName();}
                finally{
                    if (fight.isOver()){
                        replay.writeEnd();
                        userState=UserState.Start;
                        showAlert=true;
                        //resetFight(camp,btn1,btn2);
                    }
                }
            }
            else if (userState==UserState.Attacking)
            {
                try{
                    Character attacked=fight.coordToCharacter(Map.collisionCoord(event.getX(),event.getY()));
                    Character attacker=fight.nextActCharacter();
                    AttackMsg attackMsg=new AttackMsg(attacker.getID(),attacked.getID());
                    attacker.normalAttack(attacked);
                    attackMsg.send(gp);
                    replay.writeAttack(attacker.getID(),attacked.getID());
                    userState=(fight.nextActCharacter().getCamp()==fight.getCamp()?UserState.MoveFree:UserState.Waiting);//Notice：联网时这边也要改
                    btn[0].setStyle("-fx-text-fill:grey");
                    btn[1].setStyle("-fx-text-fill:grey");
                    btn[3].setStyle("-fx-text-fill:grey");
                }catch (GameException ge){ge.printName();}
                finally{
                    if (fight.isOver()){
                        replay.writeEnd();
                        userState=UserState.Start;
                        showAlert=true;
                        //resetFight(camp,btn1,btn2);
                    }
                }
            }
            else if (userState==UserState.Skilling)
            {
                try{
                    Character skilled=fight.coordToCharacter(Map.collisionCoord(event.getX(),event.getY()));
                    Character skiller=fight.nextActCharacter();
                    SkillMsg skillMsg=new SkillMsg(skiller.getID(),skilled.getID());
                    fight.skillCharacter(skiller,skilled);
                    skillMsg.send(gp);
                    replay.writeSkill(skiller.getID(),skilled.getID());
                    userState=(fight.nextActCharacter().getCamp()==fight.getCamp()?UserState.MoveFree:UserState.Waiting);//Notice：联网时这边也要改
                    btn[0].setStyle("-fx-text-fill:grey");
                    btn[1].setStyle("-fx-text-fill:grey");
                    btn[3].setStyle("-fx-text-fill:grey");
                }catch (GameException ge){ge.printName();}
                finally{
                    if (fight.isOver()){
                        replay.writeEnd();
                        userState=UserState.Start;
                        showAlert=true;
                        //resetFight(camp,btn1,btn2);
                    }
                }
            }
        });
        
        thread.start();
        thread2.start();
    }
    public Replay getReplay(){return replay;}
    public Fight getFight(){return fight;}
    public void setState(UserState val){userState=val;}
    public UserState getState(){return userState;}
    public GameProtocol getGameProtocol(){return gp;}
    public void resetFight(Camp camp,String ip,int port)
    {
        if (camp==Camp.JUSTICE) gp=new GameServer();
        else gp=new GameClient(ip,port);
        
        //gp.init();//这里会阻塞
        fight=new Fight(camp);
        fight.createCharacter();
        replay=new Replay(camp);
        //if (fight.nextActCharacter().getCamp()!=camp) userState=UserState.Waiting;
        //else userState=UserState.MoveFree;
        button[0].setStyle("-fx-text-fill:grey");
        button[1].setStyle("-fx-text-fill:grey");
        button[3].setStyle("-fx-text-fill:grey");
    }
    public void startFight()
    {
        gp.init();//这里会阻塞
        if (fight.nextActCharacter().getCamp()!=fight.getCamp()) userState=UserState.Waiting;
        else userState=UserState.MoveFree;
        for (Button btn:button)
            btn.setVisible(true);
    }
    public void draw()
    {
        gc.clearRect(0,0,1452,876);
        switch (userState)
        {
            case Start:
                for (Button btn:button) btn.setVisible(false);
                gc.drawImage(startimage,0,0,App.backW,App.backH);
                break;
            case ServerConnecting:
                gc.setStroke(Color.BLACK);
                gc.setLineWidth(2);
                gc.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 40));
                gc.setFill(Color.BLACK);
                gc.fillText("IP address:"+gp.getIP()+"\nPort:"+gp.getPort(),1452/2-190,876/2);
                //System.out.println("11");
                break;
            case ClientConnecting:
                //System.out.println("122");
                break;
            case Replay:
                gc.setStroke(Color.BLACK);
                gc.setLineWidth(2);
                for (int i=0;i<=Map.maxX;++i)
                    gc.strokeLine(startLeft+i*Map.gridWidth,startUp,startLeft+i*Map.gridWidth,startUp+Map.maxY*Map.gridHeight);
                for (int i=0;i<=Map.maxY;++i)
                    gc.strokeLine(startLeft,startUp+i*Map.gridHeight,startLeft+Map.maxX*Map.gridWidth,startUp+i*Map.gridHeight);
                Replay.getFight().drawCharacter(gc);
                gc.setFont(Font.font("YouYuan", FontWeight.BOLD, FontPosture.REGULAR, 50));
                gc.setFill(Color.BLACK);
                gc.fillText("战斗回放——第 "+Replay.getFight().getTurnCount()+" 轮",1452/2-200,startUp-10);
                break;
            default:
                gc.setStroke(Color.BLACK);
                gc.setLineWidth(2);
                for (int i=0;i<=Map.maxX;++i)
                    gc.strokeLine(startLeft+i*Map.gridWidth,startUp,startLeft+i*Map.gridWidth,startUp+Map.maxY*Map.gridHeight);
                for (int i=0;i<=Map.maxY;++i)
                    gc.strokeLine(startLeft,startUp+i*Map.gridHeight,startLeft+Map.maxX*Map.gridWidth,startUp+i*Map.gridHeight);
                fight.drawCharacter(gc);
                if (userState==UserState.Moving)
                    Map.drawRange(fight.nextActCharacter().getPosition(),fight.nextActCharacter().getMoveRange(),gc,Color.YELLOW);
                else if (userState==UserState.Attacking)
                    Map.drawRange(fight.nextActCharacter().getPosition(),fight.nextActCharacter().getAttackRange(),gc,Color.RED);
                else if (userState==UserState.Skilling)
                    Map.drawRange(fight.nextActCharacter().getPosition(),fight.nextActCharacter().getSkillRange(),gc,Color.BLUE);
                gc.setFont(Font.font("YouYuan", FontWeight.BOLD, FontPosture.REGULAR, 50));
                gc.setFill(Color.BLACK);
                gc.fillText("第 "+fight.getTurnCount()+" 轮",1452/2-50,startUp-10);
                gc.fillText("你的阵营为："+(fight.getCamp()==Camp.JUSTICE?"葫芦娃":"妖精"),1452/2-80,startUp+760);
                break;
        }
        if (showAlert)
        {
            showAlert=false;
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setContentText("你"+(fight.getCamp()==fight.nextActCamp()?"赢":"输")+"了");
            alert.setHeaderText((fight.nextActCamp()==Camp.JUSTICE?"葫芦娃":"妖精")+"获胜！");
            alert.showAndWait();
        }
    }
    public void update()
    {
        
    }
}
