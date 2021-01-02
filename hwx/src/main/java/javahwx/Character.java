package javahwx;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.canvas.*;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.*;
//保存角色相关信息
import java.util.ArrayList;
enum Camp{
	JUSTICE,EVIL;
}

public class Character{
	private static Image deadImage;
	private static int id_count=0;
	private static final int FLASHCOUNT=10;
	private int id;//id标示，用于网络对战时找到对应角色
	private Camp camp;//阵营选择
	private String name;
	private int flashDuring;//用于被攻击时的特效表示
	private Image image;//角色头像表示
	private int attackValue;//攻击力
	private ArrayList<Coord> attackRange;//攻击范围
	private int maxHP;
	private int nowHP;
	private int speed;//速度，决定每轮中的谁先动，谁后动
	private Coord coord;//位置坐标
	//private boolean itsTurn;//是否轮到它的回合
	private boolean dead;//是否死亡
	private int isFlash;//是否被攻击，用于被攻击时的特效表示
	private ArrayList<Coord> moveRange;//移动范围
	private boolean move;//本回合是否移动过,T表示移动过，F表示未移动过（可以移动），下同
	private boolean attack;//本回合是否普通攻击/使用技能过
	private Skill skill;
	/*public interface PropertyClickListener {
		public void onMenuItemClick(int index);
	}
	private PropertyClickListener propertyClickListener;*/
	Character(){}
	Character(String name,int attackValue,ArrayList<Coord> attackRange,int maxHP,int nowHP,int speed,boolean isDead,ArrayList<Coord> moveRange,Camp camp,Skill skill)
	{
		this.id=++Character.id_count;
		this.name=new String(name);
		this.attackValue=attackValue;
		this.attackRange=new ArrayList<>();
		for (Coord x:attackRange)
            this.attackRange.add(x);
		this.maxHP=maxHP;
		this.nowHP=nowHP;
		this.speed=speed;
		this.dead=isDead;
		this.camp=camp;
		this.skill=skill;
		this.image=new Image("file:image/"+name+".png");
		//this.image=new Image("file:image/hulu1.jpg");
		this.flashDuring=0;
		this.isFlash=0;
		//this.itsTurn=false;
		this.moveRange=new ArrayList<>();
		for (Coord x:moveRange)
            this.moveRange.add(x);
	}
	
	public static void idReset(){id_count=0;}
	public static void setDeadImage(Image img){deadImage=img;}
	public boolean isDead(){return dead;}
	public boolean hasMoved(){return move;}
	public Camp getCamp(){return camp;}
	public int getID(){return id;}
	public Skill getSkill(){return skill;}
	public int getSpeed(){return speed;}
	public void reduceSpeed(int val)//速度不低于1
	{
		if (speed-val>=1) speed-=val;
		else speed=1;
	}
	//public void setTurn(){itsTurn=true;}
	//public void resetTurn(){itsTurn=false;}
	public void setFlash(int val){isFlash=val;}
	public void resetFlash(){isFlash=0;}
	public void setMove(){move=true;}
	public void setAttack(){attack=true;}
	public boolean hasAttacked(){return attack;}
	public void actionReset(){move=false;attack=false;}
	public String getName(){return name;}
	public Coord getPosition(){return coord;}
	public void placeHere(int x,int y){coord=new Coord(x,y);/*TODO:需要将角色放在这里的图像化处理*/}
	public ArrayList<Coord> getMoveRange(){return moveRange;}
	public ArrayList<Coord> getAttackRange(){return attackRange;}
	public ArrayList<Coord> getSkillRange(){return (skill==null)?null:skill.skillRange;}
	//以下为返回图片像素坐标的左上角
	public int getPixelX(){return MapCanvas.startLeft+coord.getX()*Map.gridWidth;}
	public int getPixelY(){return MapCanvas.startUp+coord.getY()*Map.gridHeight;}
	public boolean isCollisionWith(double otherPixelX,double otherPixelY)
	{
		return Map.isCollisionWith(getPixelX(),getPixelY(),otherPixelX,otherPixelY);
		//return (getPixelX()<=otherPixelX&&otherPixelX<getPixelX()+Map.gridWidth)&&(getPixelY()<=otherPixelY&&otherPixelY<getPixelY()+Map.gridHeight);
	}
	public String getPropery()
	{
		return "名称:"+name+"  血量:"+nowHP+"/"+maxHP+"  攻击力:"+attackValue+"  速度:"+speed;
	}
	public void getAttacked(int value)
	{
		if (value>=0)
		{
			if (nowHP<=value)//已死亡
			{
				nowHP=0;
				dead=true;
				//TODO:需要触发一些死亡事件
			}
			else
			{
				nowHP-=value;
				isFlash=1;
			}
		}
		else//治疗
		{
			if (nowHP-value>maxHP) nowHP=maxHP;
			else nowHP-=value;
			isFlash=2;
		}
	}
	public void nullAttack() throws HasAttackedException
	{
		if (this.attack==true)//本回合已经攻击过
			throw new HasAttackedException();
		this.setAttack();
	}
	public void normalAttack(Character other) throws OutOfRangeException,InvalidGoalException,HasAttackedException
	{
		//普通攻击
		if (other==null||other.isDead()==true||other.getCamp()==this.getCamp())//无效攻击
			throw new InvalidGoalException();
		if (this.attack==true)//本回合已经攻击过
			throw new HasAttackedException();
		boolean flag=false;
		for (Coord c:attackRange)
			if (other.coord.getX()==this.coord.getX()+c.getX()&&other.coord.getY()==this.coord.getY()+c.getY())
			{
				flag=true;
				break;
			}
		if (flag==false)//不在攻击范围内
			throw new OutOfRangeException();
		this.setAttack();
		other.getAttacked(this.attackValue);
	}
	public SkillClassifier skillAttack(Character other) throws OutOfRangeException,InvalidGoalException,HasAttackedException,NoSkillException
	{
		//技能攻击
		if (other==null||other.isDead()==true)//无效攻击
			throw new InvalidGoalException();
		if (this.attack==true)//本回合已经攻击过
			throw new HasAttackedException();
		if (skill==null) throw new NoSkillException();
		boolean flag=false;
		for (Coord c:skill.skillRange)
			if (other.coord.getX()==this.coord.getX()+c.getX()&&other.coord.getY()==this.coord.getY()+c.getY())
			{
				flag=true;
				break;
			}
		if (flag==false)//不在攻击范围内
			throw new OutOfRangeException();
		this.setAttack();
		return skill.doSkill(other);
	}
	public void move(Coord pos) throws OutOfRangeException,HasMovedException
	{
		if (pos.isLegal()==false)
			throw new OutOfRangeException();
		if (this.move==true)
			throw new HasMovedException();
		boolean flag=false;
        for (Coord c:moveRange)
            if (pos.getX()==this.coord.getX()+c.getX()&&pos.getY()==this.coord.getY()+c.getY())
            {
                flag=true;
                break;
            }
		if (flag==false)//不在移动范围内
			throw new OutOfRangeException();
		this.setMove();
		this.placeHere(pos.getX(),pos.getY());
	}
	public void draw(GraphicsContext gc,int propertyID,int nowID)
	{
		gc.save();
		if (isDead())
			gc.drawImage(deadImage,getPixelX(),getPixelY(),Map.gridWidth,Map.gridHeight);
		else
		{
			if (this.id==nowID)
			{
				gc.setGlobalAlpha(1.5f);
				//gc.strokeRect(getPixelX(), getPixelY(), Map.gridWidth,Map.gridHeight);
				gc.setStroke(Color.BLUE);
				gc.setLineWidth(6);
				gc.strokeRect(getPixelX(), getPixelY(), Map.gridWidth,Map.gridHeight);
			}
			//else
			//{
			//}
			gc.setGlobalAlpha(1.0f);
			gc.drawImage(image,getPixelX(),getPixelY(),Map.gridWidth,Map.gridHeight);
			gc.setFill(Color.RED);
			//gc.drawRect(getPixelX()+10,getPixelY(),60,7);
			gc.fillRect(getPixelX()+10,getPixelY(),nowHP*1.0/maxHP*60,7);
			if (isFlash>0)
				{
					if (flashDuring < FLASHCOUNT)
					{
						gc.setGlobalAlpha(0.5f);
						gc.setFill(isFlash==1?Color.RED:Color.GREEN);
						gc.fillRect(getPixelX(), getPixelY(), Map.gridWidth, Map.gridHeight);
						++flashDuring;
					}
					else
					{
						flashDuring=0;
						isFlash=0;
					}
				}
		}
		if (propertyID==this.id)
		{
			gc.clearRect(MapCanvas.startLeft,MapCanvas.startUp+725,800,45);
			gc.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 30));
			gc.setFill(Color.BROWN);
			gc.fillText(getPropery(),MapCanvas.startLeft-60,MapCanvas.startUp+760);
			
		}
		gc.restore();
	}
	public void update()
	{

	}
}
class JusticeCharacter extends Character{
	JusticeCharacter(String name,int attackValue,ArrayList<Coord> attackRange,int maxHP,int nowHP,int speed,boolean isDead,ArrayList<Coord> moveRange,Skill skill)
	{
		super(name,attackValue,attackRange,maxHP,nowHP,speed,isDead,moveRange,Camp.JUSTICE,skill);
	}
}
class EvilCharacter extends Character{
	EvilCharacter(String name,int attackValue,ArrayList<Coord> attackRange,int maxHP,int nowHP,int speed,boolean isDead,ArrayList<Coord> moveRange,Skill skill)
    {
		super(name,attackValue,attackRange,maxHP,nowHP,speed,isDead,moveRange,Camp.EVIL,skill);
    }
}