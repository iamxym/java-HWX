# java-HWX
本项目为张浩南-181860134单人完成，无组队

# 评测相关
在`hwx/`目录下执行`mvn clean test package`，然后执行`java -jar target/hwx-1.0-SNAPSHOT.jar`即可进入游戏，接下来的操作可见**游戏流程**

本机环境为Linux debian 4.19.0-5-amd64 #1 SMP Debian 4.19.37-5+deb10u2 (2019-08-08) x86_64 GNU/Linux


环境变量配置为
```
JAVA_HOME=/home/zhanghaonan/java_jdk/jdk1.8.0_261

CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar:$JAVA_HOME/lib/hamcrest-core-1.3.jar:$JAVA_HOME/lib/junit-4.13.1.jar:$JAVA_HOME/lib/javafx-mx.jar:$JAVA_HOME/lib/ant-javafx.jar

MAVEN_HOME=/home/zhanghaonan/java_jdk/apache-maven-3.6.3

PATH=$MAVEN_HOME/bin:$JAVA_HOME/bin:$PATH
```

**Notice**：由于javafx 8包含在JDK 8中，即直接在lib中找到而无需下载，因此我在配置环境时**将其一并加入`CLASSPATH`中，而并没有将其作为第三方独立库处理，在POM.xml中也没有进行相关配置**，所以在助教/老师maven编译时可能会出现找不到javafx库的情况。


# 设计思想
考虑将代码模块分为4个部分：**游戏设计、网络对战、战斗回放、图形界面**

## 游戏设计
本实验采取战棋类游戏策略，每名角色拥有“生命”、“最高血量”、“当前血量”、“速度”、“攻击力”五项基本数值属性，“攻击范围”、“移动范围”两项范围属性、还有“阵营归属”和“技能”
其中**阵营归属**有两种：正义方（葫芦娃）和邪恶方（妖精）

战斗采用**回合制**，且每轮会将双方所有存活角色按速度决定本回合的先后顺序，**速度大的先行动**（移动/攻击/释放技能），若速度相同的随机选其一，直到所有角色都行动过后再开始新的一轮，循环往复直至游戏结束
Notice：根据该回合逻辑，有可能出现“连续若干回合的行动角色均属于同一阵营”的情况

每轮的行动角色需要**先进行“移动(move)”操作**，操作选择位置不得超过该角色的“移动范围”，否则无效；“移动”完后可以**选择“攻击(attack)”、"释放技能(skill)"或“放弃(cancel)”**，“攻击”和“释放技能”同样不能超出给定范围

当角色“当前血量”小于等于0时死亡，死亡角色不能行动，但仍会在死前占据的格子上，其他角色不能“移动”到该格子，也不能对该角色进行“攻击”或“释放技能”


当前回合角色不是玩家方阵营时，玩家需要等待，直至对方玩家操纵角色行动结束且轮到玩家方阵营角色的回合。

具体的角色方法和字段设计在`Character.java`源文件中，而相关的战斗逻辑在`Fight.java`源文件中


## 网络对战
本项目中采用UDP进行网络通信

主机端Server创建UDP报文`datagramSocket`并给出本机的IP和相关Port，等待客户端发送信息；然后客户端Client用Server的IP+Port进行连接并发送"Hello World"信息，这样一来主机和客户均获得了对方的IP和Port，之后的对战通信用对应的UDP报文传输即可。

该部分代码主要为`GameProtocol.java`

应用层（游戏战斗）参考了余萍老师分享的“坦克大战”项目的设计方法，公共接口为`GameMsg`，通过其的不同实现`MoveMsg/AttackMsg/SkillMsg/NullMsg`来分别表示“移动”、“攻击”、“释放技能”和“放弃”。发送方在执行完角色的相关动作后，通过`send()`方法来发送动作信息；而接收方则用`parse()`来对信息进行解读，并在本机执行相关动作。

该部分代码主要为`GameMsg.java`

## 战斗回放
由于是回合制战旗，因此回放文件的读写操作都相对方便不少

采用类似于网络对战环节`GameMsg`的方式来存储角色的动作信息，存储回放文件时只需要每回合处理完动作后向对应的OutputStream中写入对应信息即可，而战斗回放时只需要每隔一段时间（代码中为1000ms）从InputStream中读取动作信息并执行即可

详见`Replay.java`

## 图形界面
参考[对应博客地址](https://blog.csdn.net/wingfourever)的“JavaFX战旗游戏开发”相关博文，用`StackPane`+`MapCanvas`的主要方式完成图形界面的构建，其中`MapCanvas`是`Canvas`的自定义子类，其作为主要控件，加入了需要用到的相关字段，如战斗对象`Fight fight`,UDP通信相关`GameProtocol gameProtocol`,回放对象`Replay replay`等

绘制的核心代码如下：
```java
private Thread thread=new Thread(new Runnable(){
        @Override
        public void run(){
            while (isRunning){
                
                Platform.runLater(()->{
                    draw();update();
                });
                try{
                    Thread.sleep(100);
                }
                catch(InterruptedException e){e.printStackTrace();}
            }
        }
    });
```
这里选择在MapCanvas中新建一个线程用于绘制，而`draw(),update()`方法负责具体的绘制行为

# 游戏流程（作业评测时的操作相关）
>**游戏状态机**：

登录界面->联机对战 or 战局回放

联机对战->作为Server端（葫芦娃） or 作为Client端（妖精）

作为Server端->显示IP地址和端口号，等待连接

作为Client端->要求输入服务器的IP地址和端口号，尝试连接

连接成功->棋盘初始化，并进入战斗

战局回放(R键)->弹出界面，要求用户选择适当的文件进行回放

已选择回放文件->棋盘初始化，并进入战斗，但这里的战斗指令由文件内的指令控制，大概每`sleep(1000)`进行一次操作

战斗结束（`isOver()`）->切断UDP连接（如果有的话），并回到 登录界面

游戏开始时的界面为：`image/启动背景.jpg`

此时按下S键会将本机作为Server端，**显示IP和port并等待Client链接**

按下C键会将本机作为Client端，**需要玩家输入Server端的IP和port，点击Enter键后尝试连接**

**按下R键**会弹出文件选择窗，选择文件夹中的`2021-01-02_21:00:43_J.gameplay`或`2021-01-02_21:00:54_E.gameplay`即可观察战斗回放
---

连接成功后进入战斗画面，**右下角的四个按钮**分别对应”移动”、“攻击”、“释放技能”、“取消”

（注意无连接的UDP在极少情况下会出现连接失败的情况，可能会出现某一方没有进入战斗场景的情况，此时将两边均中断`Ctrc+C`后，重新启动并连接即可；若通信成功则两边均会显示战斗场景）

当轮到玩家阵营的角色行动时，起始处于移动环节和`Free`状态

Notice：`Free`状态下可以**右键点击不同角色的头像以查看角色属性**（回放模式下同样适用）
移动环节的Free状态下**右键点击move**会在棋盘上显示移动范围（黄色），若此时**点击范围内的方格**则会将该角色移动至此，并进入**攻击/释放技能环节**；若此时**左键点击move**则会回到`Free`状态。

攻击/释放技能环节起始也是`Free`状态，此时**右键点击attack/skill**会在棋盘上显示移动范围（红色/蓝色），若此时**点击范围内的角色**则会对该角色攻击/释放技能，并结束本角色回合；同样的，若此时**左键点击move**则会回到`Free`状态。

总的来说，移动环节->攻击/释放技能环节->本回合结束

本回合中，无论何时，只要**右键按下cancel**，均会进入下一环节（如攻击/释放技能环节按下cancel会直接结束本回合）


游戏结束（某一方角色全部死亡）时，会弹出信息框供玩家确认，并回到游戏开始时的界面

# 设计技巧与实现细节
利用状态机的思想来完成游戏流程和战斗过程的构建，这样设计回合制游戏使得程序的运行过程和状态清晰可见，且能够较好地区分战斗与回放，Free状态与“移动/攻击或释放技能环节"，其中状态机的状态描述用枚举类型`enum`表示

在战斗过程的状态机中加入`Waiting`状态阻止接收本机玩家除”查看角色属性“以外的操作，同时使用`Fight`类中的`nextActCharacter()`方法，通过此时行动角色的阵营来更改状态——玩家可操作的`Free`还是需要玩家等待的`Waiting`

战斗时棋盘的绘制同样在mapCanvas中进行，玩家选择移动/攻击/释放技能时会触发鼠标点击事件，通过捕获点击位置坐标和一定计算可以得到移动位置/被攻击的角色

Server/Client连接时显示的文本框/Enter按钮等部件均不属于`MapCanvas`，而是作为`StackPane`的子节点，但我们可以通过`MapCanvas`中记录的**状态机此时状态**来控制这些部件是否可见，从而使其不会相互影响

角色受到攻击时，其头像会**显示红光**一段时间，表示其受到伤害；接受治疗时则会**显示绿光**一段时间；当前回合行动的角色头像框周围会有**蓝色方框**加以显示

`GameException.java`中，`GameException`类继承了`Exception`异常类，用于表示游戏中可能遇到的异常，定义的子类有`OutOfRangeException`,`InvalidGoalException`,`OccupiedException`等，这些子类用于代码的调试以及提示玩家的误操作，如玩家移动/攻击/释放技能时如果超出规定范围，则会在终端输出"OutOfRangeException"

由于通信时的`recieve()`会造成线程阻塞，因此进一步创建一个线程`thread2`来执行“等待并解析对方信息”的操作，否则游戏无法正常运行

由于游戏开始时的状态布局均不变，所以联机对战时只需要保证中间传递的动作信息正确一致即可，但“速度相同时随机选一”的策略导致`nextActCharacter()`返回值可能不一样，从而导致当前回合行动角色不同，这只需要使双方使用相同的随机种子即可（种子相同+(两边行为一致->随机次数和地方一致)=各处随机数相同）；战斗回放时同理，只要让随机种子与回放文件中的战斗相同即可

Server端获取本机IP时在代码中需要过滤掉127.0.0.1/127.0.1.1，否则无法进行联机对战，而只能在本机对战

每次战斗的重置`resetFight()`会创建一个新的回放对象`Replay replay`，回放对象中的构造函数中创建了用于存储的回放文件，其中回放文件的文件名采用了`yyyy-MM-dd_HH:mm:ss_X.gameplay`的格式
其中`.gameplay`为后缀名，yyyy-MM-dd_HH:mm:ss表示年-月-日-时-分-秒，而X是J或E，表示葫芦娃Justice方或Evil方。注意**这里的J/E文件存储内容完全相同**，而这样命名的原因是因为本机两端口对战时，不会引发回放文件的命名冲突（年月日时分秒保证**不会与之前的回放文件冲突**，J/E保证**本次对战的本机双方**不会冲突）

相关图像在`image`文件夹下

正义方（葫芦娃）头像：`image/战士娃.png`,`image/骑士娃.png`,`image/牧师娃.png`,`image/术士娃.png`,`image/法师娃.png`,`image/盗贼娃.png`,`image/猎人娃.png`

邪恶方（妖精）头像：`image/蛇精.png`,`image/蛤蟆精.png`,`image/蝎子精.png`,`image/蝙蝠.png`,`image/马蜂.png`,`image/蜈蚣精.png`,`image/蜘蛛精.png`

战斗背景：`image/战斗背景.jpg`

死亡角色的头像会统一换成`image/坟墓.png`

## 游戏特色——技能说明
仅有正义方的“牧师娃”，“盗贼娃”，“猎人娃”和邪恶方的“蝎子精”、“蛇精”、“蝙蝠”有技能，其他角色的技能均为空

技能类`Skill`包含两个字段`Objcamp,skillRange`和方法`doSkill()`

`int Objcamp`表示该技能的施法对象为相同阵营/不同阵营，`ArrayList<Coord> skillRange`存储该技能的施法范围，`doSkill()`方法在不同子类中有不同实现。`Skill`子类共有6个：`HealSkill,SwapSkill,SnipeSkill,GazeSkill,CutSkill,VampireSkill`，分别对应“牧师娃”，“盗贼娃”，“猎人娃”，“蝎子精”、“蛇精”、“蝙蝠”各自的技能

牧师娃技能HealSkill为单体治疗技能，为友方角色回复生命值

盗贼娃技能SwapSkill为单体伤害+位移技能，对敌方角色造成伤害并与之交换位置

猎人娃技能为SnipeSkill为单体伤害技能，远距离对敌方角色造成伤害

蝎子精技能为CutSkill为范围伤害技能，对指定敌方角色造成伤害，其周围的敌方角色也会受到一定伤害

蛇精技能GazeSkill为单体伤害减益技能，对敌方角色造成伤害并降低速度

蝙蝠技能VampireSkill为单体伤害+自身治疗技能，对敌方造成伤害且自身回复生命值

为适应不同技能效果的多样性，在`Fight`类中加入`skillCharacter()`方法来辅助实现技能效果，如CutSkill的范围伤害等；战斗过程中技能释放的对象需要指定为某一角色（不能“点地板”释放）





