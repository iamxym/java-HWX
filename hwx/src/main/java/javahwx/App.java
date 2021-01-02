package javahwx;
import java.util.*;
import javafx.application.Application;
import javafx.event.*;
import javafx.scene.image.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.canvas.*;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.paint.Color;
import javafx.stage.*;
import javafx.geometry.*; 
import javafx.scene.text.*;
import javafx.event.*;
import javafx.stage.FileChooser;
import java.io.File;
public class App extends Application {
    public static int backW=1452;
    public static int backH=876;
    String serverIP;
    int serverPort;
    //(1280,720)/(16,12)=(80,60)
    
    @Override
    public void start(Stage primaryStage) {
        
        Image img=new Image("file:image/2.jpg");
        StackPane root = new StackPane();
        root.setBackground(new Background(new BackgroundImage(img,null,null,null,null)));

        Button moveButton = new Button();
        Button attackButton = new Button();
        Button cancelButton = new Button();
        Button skillButton=new Button();
        MapCanvas mapCanvas=new MapCanvas(1452,876,new Button[]{moveButton,attackButton,cancelButton,skillButton});
        Character.setDeadImage(new Image("file:image/坟墓.jpg"));
        //gc.drawImage(new Image("file:image/战士娃.png"), startLeft,startUp,gridW,gridH);    
        moveButton.setTranslateX(backW/2.9-60);
        moveButton.setTranslateY(backH/2.2);
        moveButton.setText("Move");
        //moveButton.setFill(Color.rgb(0xaa,0xbb,0xcc));
        moveButton.setStyle("-fx-text-fill:grey");
        moveButton.setMinSize(80, 40);
        int cnt=0;
        moveButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new   EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton().name().equals(MouseButton.PRIMARY.name())) {
                    if (mapCanvas.getState()==UserState.MoveFree)
                    {
                        mapCanvas.setState(UserState.Moving);
                        moveButton.setStyle("-fx-text-fill:black");
                    }
                }
                else if(event.getButton().name().equals(MouseButton.SECONDARY.name())) {
                    if (mapCanvas.getState()==UserState.Moving)
                    {
                        mapCanvas.setState(UserState.MoveFree);
                        moveButton.setStyle("-fx-text-fill:grey");
                    }
                }
            }
        });
        
        attackButton.setTranslateX(backW/2.9+20);
        attackButton.setTranslateY(backH/2.2);
        attackButton.setText("Attack");
        attackButton.setStyle("-fx-text-fill:grey");
        attackButton.setMinSize(80, 40);
        attackButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new   EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton().name().equals(MouseButton.PRIMARY.name())) {
                    if (mapCanvas.getState()==UserState.AttackFree)
                    {
                        attackButton.setStyle("-fx-text-fill:black");
                        mapCanvas.setState(UserState.Attacking);
                    }
                }
                else if(event.getButton().name().equals(MouseButton.SECONDARY.name())) {
                    if (mapCanvas.getState()==UserState.Attacking)
                    {
                        attackButton.setStyle("-fx-text-fill:grey");
                        mapCanvas.setState(UserState.AttackFree);
                    }
                }
            }
        });
        skillButton.setTranslateX(backW/2.9+100);
        skillButton.setTranslateY(backH/2.2);
        skillButton.setText("Skill");
        skillButton.setStyle("-fx-text-fill:grey");
        skillButton.setMinSize(80, 40);
        skillButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new   EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton().name().equals(MouseButton.PRIMARY.name())) {
                    if (mapCanvas.getState()==UserState.AttackFree&&mapCanvas.getFight().nextActCharacter().getSkill()!=null)
                    {
                        skillButton.setStyle("-fx-text-fill:black");
                        mapCanvas.setState(UserState.Skilling);
                    }
                }
                else if(event.getButton().name().equals(MouseButton.SECONDARY.name())) {
                    if (mapCanvas.getState()==UserState.Skilling)
                    {
                        skillButton.setStyle("-fx-text-fill:grey");
                        mapCanvas.setState(UserState.AttackFree);
                    }
                }
            }
        });
        
        cancelButton.setTranslateX(backW/2.9+180);
        cancelButton.setTranslateY(backH/2.2);
        cancelButton.setText("Cancel");
        cancelButton.setStyle("-fx-text-fill:red");
        cancelButton.setMinSize(80, 40);
        cancelButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new   EventHandler<MouseEvent>() {
            
            @Override
            public void handle(MouseEvent event) {
                
                if(event.getButton().name().equals(MouseButton.PRIMARY.name())) {
                    if (mapCanvas.getState()==UserState.AttackFree||mapCanvas.getState()==UserState.Attacking||mapCanvas.getState()==UserState.Skilling)
                    {
                        try{
                            int id=mapCanvas.getFight().nextActCharacter().getID();
                            NullMsg nullMsg=new NullMsg(id);
                            mapCanvas.getFight().nextActCharacter().nullAttack();
                            nullMsg.send(mapCanvas.getGameProtocol());
                            mapCanvas.getReplay().writeNull(id);
                            skillButton.setStyle("-fx-text-fill:grey");
                            attackButton.setStyle("-fx-text-fill:grey");
                            moveButton.setStyle("-fx-text-fill:grey");
                            mapCanvas.setState(mapCanvas.getFight().nextActCamp()==mapCanvas.getFight().getCamp()?UserState.MoveFree:UserState.Waiting);
                        }catch(GameException e){e.printName();}
                        
                        
                    }
                    else if (mapCanvas.getState()==UserState.MoveFree||mapCanvas.getState()==UserState.Moving)
                    {
                        try{
                            int id=mapCanvas.getFight().nextActCharacter().getID();
                            Coord pos=mapCanvas.getFight().nextActCharacter().getPosition();
                            MoveMsg moveMsg=new MoveMsg(id,pos);
                            mapCanvas.getFight().moveCharacter(pos);
                            moveMsg.send(mapCanvas.getGameProtocol());
                            mapCanvas.getReplay().writeMove(id,pos.getX(),pos.getY());
                            skillButton.setStyle("-fx-text-fill:grey");
                            attackButton.setStyle("-fx-text-fill:grey");
                            moveButton.setStyle("-fx-text-fill:grey");
                            mapCanvas.setState(UserState.AttackFree);
                        }catch(GameException e){e.printName();}
                        
                        
                    }
                }
            }
        });
        
        root.getChildren().add(mapCanvas);
        root.getChildren().add(moveButton);
        root.getChildren().add(attackButton);
        root.getChildren().add(skillButton);
        root.getChildren().add(cancelButton);
        moveButton.setVisible(false);
        attackButton.setVisible(false);
        skillButton.setVisible(false);
        cancelButton.setVisible(false);


        Text ipText = new Text("IP Address:");   
        ipText.setStyle("-fx-font: normal bold 20px 'serif' "); 
        final TextField ipField = new TextField();
        ipField.setStyle("-fx-font: normal bold 15px 'serif' "); 
        ipField.setMaxWidth(165);
        ipText.setTranslateX(-50);
        ipField.setTranslateX(100);
        ipText.setTranslateY(-80);
        ipField.setTranslateY(-80);
        Text portText = new Text("Port:");   
        portText.setStyle("-fx-font: normal bold 20px 'serif' "); 
        final TextField portField = new TextField();
        portField.setStyle("-fx-font: normal bold 15px 'serif' "); 
        portField.setMaxWidth(165);
        portText.setTranslateX(-50);
        portField.setTranslateX(100);
        portText.setTranslateY(-30);
        portField.setTranslateY(-30);
        Button enterButton=new Button("确认");
        enterButton.setMinSize(80, 40);
        enterButton.setTranslateY(10);
        enterButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new   EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton().name().equals(MouseButton.PRIMARY.name())) {
                    System.out.println(ipField.getText().trim()+","+portField.getText().trim());
                    try{
                        mapCanvas.resetFight(Camp.EVIL,ipField.getText().trim(),Integer.valueOf(portField.getText().trim()).intValue());
                        mapCanvas.startFight();
                        ipText.setVisible(false);
                        ipField.setVisible(false);
                        portText.setVisible(false);
                        portField.setVisible(false);
                        enterButton.setVisible(false);
                    }catch (NumberFormatException e){e.printStackTrace();}
                }
            }
        });
        root.getChildren().add(ipText);
        root.getChildren().add(ipField);
        root.getChildren().add(portText);
        root.getChildren().add(portField);
        root.getChildren().add(enterButton);

        ipText.setVisible(false);
        ipField.setVisible(false);
        portText.setVisible(false);
        portField.setVisible(false);
        enterButton.setVisible(false);

        final FileChooser fileChooser = new FileChooser();
 
        Scene scene=new Scene(root,backW,backH);
        scene.setOnKeyPressed(event->{
            
            if (mapCanvas.getState()==UserState.Start)
                switch (event.getCode())
                {
                    case R:
                        File file = fileChooser.showOpenDialog(primaryStage);
                        if (file != null) {
                            
                            if (mapCanvas.getReplay().setReplayFile(file)==-1)
                            {
                                System.out.println("Replay File Error!");    
                            }
                            else mapCanvas.setState(UserState.Replay);
                        }
                        //TODO
                        //openButton.setVisible(true);
                        break;
                    case S:
                        mapCanvas.setState(UserState.ServerConnecting);
                        mapCanvas.resetFight(Camp.JUSTICE,null,-1);
                        //mapCanvas.startFight();
                        break;
                    case C:
                        ipText.setVisible(true);
                        ipField.setVisible(true);
                        portText.setVisible(true);
                        portField.setVisible(true);
                        enterButton.setVisible(true);
                        mapCanvas.setState(UserState.ClientConnecting);
                        break;
                    default:
                        break;
                }
        });
        //scene.getStylesheets().add(getClass().getResource("MainStyle.css").toExternalForm());
        primaryStage.setTitle("葫芦娃大战妖精");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch();
    }
}
