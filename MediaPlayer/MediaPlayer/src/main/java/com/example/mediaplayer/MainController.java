package com.example.mediaplayer;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML private ImageView iv;
    @FXML private MediaView mv;
    @FXML private Button btn_play, btn_pause, btn_stop, btn_open;
    @FXML private Slider pbar, vbar;
    @FXML private Label lb_currenttime, lb_totaltime;

    private Stage primaryStage;
    private String path = null;
    Media media;
    MediaPlayer mediaPlayer;
    boolean endOfMedia, pause;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

//        media = new Media(Paths.get(path).toUri().toString());
        media = new Media(getClass().getResource("/media/video.mp4").toString());
        mv.setVisible(false);
        mediaPlayer = new MediaPlayer(media);
        mv.setMediaPlayer(mediaPlayer);

        vbar.setValue(50);
        mediaPlayer.setVolume(50/100.0);
        
        mediaPlayer.setOnReady(new Runnable() {
            @Override
            public void run() {
                btn_play.setVisible(true); btn_pause.setVisible(false);
                System.out.println("Ready state");
                pause = false;
            }
        });

        mediaPlayer.setOnPlaying(new Runnable() {
            @Override
            public void run() {
                btn_play.setVisible(false); btn_pause.setVisible(true);
                System.out.println("Playing state");
                pause = false;
            }
        });

        mediaPlayer.setOnPaused(new Runnable() {
            @Override
            public void run() {
                btn_play.setVisible(true); btn_pause.setVisible(false);
                System.out.println("Paused state");
                pause = true;
            }
        });

        mediaPlayer.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                btn_play.setVisible(false); btn_pause.setVisible(true);
                endOfMedia = true;
                System.out.println("EndOfMedia state");
                pause = true;
            }
        });

        mediaPlayer.setOnStopped(new Runnable() {
            @Override
            public void run() {
                btn_play.setVisible(true); btn_pause.setVisible(false);
                System.out.println("Stopped state");
                pause = true;
            }
        });

        btn_play.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(endOfMedia){
                    mediaPlayer.stop();
                    mediaPlayer.seek(mediaPlayer.getStartTime());
                }

                iv.setVisible(false);
                mv.setVisible(true);
                mediaPlayer.play();
                endOfMedia = false;
            }
        });
        btn_pause.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                mediaPlayer.pause();
            }
        });

        btn_stop.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                mediaPlayer.stop();
            }
        });

        vbar.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                mediaPlayer.setVolume(t1.doubleValue()/100.0);
            }
        });

        mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observableValue, Duration duration, Duration t1) {
                pbar.setMax(mediaPlayer.getTotalDuration().toSeconds());
                pbar.setValue(t1.toSeconds());
                lb_currenttime.setText(""+mediaPlayer.getCurrentTime().toSeconds()+" sec");
                lb_totaltime.setText(""+mediaPlayer.getTotalDuration().toSeconds()+" sec");

                System.out.println(mediaPlayer.getCurrentTime()+"/"+t1+"/"+mediaPlayer.getTotalDuration());

//                double time = mediaPlayer.getCurrentTime().toSeconds()/mediaPlayer.getTotalDuration().toSeconds();
//                System.out.println(time+"/"+mediaPlayer.getCurrentRate()+"/"+t1);
//                System.out.println(observableValue+"/"+duration+"/"+t1);
//                pbar.setValue(time*100);
            }
        });

        pbar.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                mediaPlayer.seek(Duration.seconds(pbar.getValue()));
//                double total = mediaPlayer.getTotalDuration().toSeconds();
//                mediaPlayer.seek(Duration.seconds(total*pbar.getValue()/100.0));
//                mediaPlayer.setRate(pbar.getValue()/100.0);
            }
        });

        mv.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getClickCount() > 1){
                    if(!pause)
                        mediaPlayer.pause();
                    else
                        mediaPlayer.play();
                }
            }
        });

        iv.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getClickCount() > 1){
                    iv.setVisible(false);
                    mv.setVisible(true);
                    mediaPlayer.play();
                    endOfMedia = false;
                }
            }
        });

        // filechooser를 통해 불러온 파일은 실행은 되나 몇가지 문제가 있음.
        // 파일 선택 후 정상적으로 동작되게 하려면 initialize 메소드의 역할이 뭔지 정확히 파악하고 처리해야함. initialize()의 역할은? 한번 시도해보면 좋을듯 함.
        btn_open.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().addAll();
                File selectedFile = fileChooser.showOpenDialog(primaryStage);
                path = selectedFile.getPath();
                System.out.println(path);
            }
        });
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

}