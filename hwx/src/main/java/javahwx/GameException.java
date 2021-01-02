package javahwx;
public class GameException extends Exception{
    public void printName()
    {
        System.out.println("GameException");
    }
}//特指游戏进行时的异常情况
class OutOfRangeException extends GameException{
    @Override
    public void printName()
    {
        System.out.println("OutOfRangeException");
    }
}//超出范围
class InvalidGoalException extends GameException{
    @Override
    public void printName()
    {
        System.out.println("InvalidGoalException");
    }
}//目标错误
class OccupiedException extends GameException{
    @Override
    public void printName()
    {
        System.out.println("OccupiedException");
    }
}//移动时撞墙/撞人
class HasAttackedException extends GameException{
    @Override
    public void printName()
    {
        System.out.println("HasAttackedException");
    }
}//已经攻击过
class HasMovedException extends GameException{
    @Override
    public void printName()
    {
        System.out.println("HasMovedException");
    }
}//已经移动过
class NoSkillException extends GameException{
    @Override
    public void printName()
    {
        System.out.println("NoSkillException");
    }
}//没有技能
class PaintException extends GameException{
    @Override
    public void printName()
    {
        System.out.println("PaintException");
    }
}//绘制出错
class TurnException extends GameException{
    @Override
    public void printName()
    {
        System.out.println("TurnException");
    }
}//回合相关
class CharacterException extends GameException{
    @Override
    public void printName()
    {
        System.out.println("CharacterException");
    }
}//错误的角色选中
class CampException extends GameException{
    @Override
    public void printName()
    {
        System.out.println("CampException");
    }
}//错误的角色选中
class MsgException extends GameException{
    @Override
    public void printName()
    {
        System.out.println("MsgException");
    }
}//通信时的信息
class ReplayException extends GameException{
    @Override
    public void printName()
    {
        System.out.println("ReplayException");
    }
}//Replay