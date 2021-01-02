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
}//已经攻击过
class NoSkillException extends GameException{
    @Override
    public void printName()
    {
        System.out.println("NoSkillException");
    }
}//已经攻击过