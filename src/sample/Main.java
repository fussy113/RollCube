package sample;

import com.interactivemesh.jfx.importer.stl.StlMeshImporter;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Mesh;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Main extends Application {

    private static final String stl = "mono.stl";
    static int count = 0;
    static int n = 0;
    private AnimationTimer timer;
    private final Rotate cameraRotateX = new Rotate(0, Rotate.X_AXIS);
    private final Rotate cameraRotateY = new Rotate(0, Rotate.Y_AXIS);
    private final Rotate cameraRotateZ = new Rotate(0, Rotate.Z_AXIS);
    private final Translate cameraTranslate = new Translate(0, -45 , -350);
    private final Rotate stlRotateX = new Rotate(15, Rotate.X_AXIS);
    private final Rotate stlRotateY = new Rotate(30, Rotate.Y_AXIS);
    private final Rotate stlRotateZ = new Rotate(0, Rotate.Z_AXIS);
    private final Translate stlTranslate = new Translate(0, 0 ,0);
    private static Scene scene;
    private static Group root;
    private static AmbientLight ambientLight;
    private static PointLight pointLight;
    private static PerspectiveCamera cam;
    private static final Color lightColor = Color.rgb(244, 255, 250);
    private static final Color stlColor = Color.WHITE;


    //stl読み込み
    static MeshView[] loadMeshViews() {

        File file = new File(stl);
        StlMeshImporter importer = new StlMeshImporter();
        importer.read(file);
        Mesh mesh = importer.getImport();

        return new MeshView[] { new MeshView(mesh) };
    }

    //スクリーンショット
    public static void screenshot(){
        Robot robot = null;
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        BufferedImage image = robot.createScreenCapture(new Rectangle(240, 25, 800, 800));
        try {
            ImageIO.write(image, "PNG", new File("./mono/mono" + n++ + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //スクショ保存用のディレクトリの作成、シーンの作成
    private Group buildScene() {
        File newdir = new File("mono");
        newdir.mkdir();

        MeshView[] meshViews = loadMeshViews();
        for (int i = 0; i < meshViews.length; i++) {
            PhongMaterial stlMaterial = new PhongMaterial(stlColor);
            stlMaterial.setSpecularColor(lightColor);
            stlMaterial.setSpecularPower(16);
            meshViews[i].setMaterial(stlMaterial);
            meshViews[i].getTransforms().setAll(stlTranslate,stlRotateX,stlRotateY,stlRotateZ);
        }
        root = new Group(meshViews);

        return root;
    }

    @Override
    public void start(Stage stage) throws Exception{

        root = buildScene();
        //環境光
        ambientLight = new AmbientLight(Color.LIGHTGREY);
        root.getChildren().add(ambientLight);
        //点光
        pointLight = new PointLight(Color.WHITESMOKE);
        pointLight.getTransforms().addAll(cameraTranslate,cameraRotateX,cameraRotateY,cameraRotateZ);
        root.getChildren().add(pointLight);

        //カメラの設定、セット
        cam = new PerspectiveCamera(true);
        cam.setFieldOfView(45);
        cam.setFarClip(1000);
        cam.getTransforms().addAll(cameraTranslate,cameraRotateX,cameraRotateY,cameraRotateZ);

        scene = new Scene(root,800,800,Color.BLACK);
        scene.setCamera(cam);
        stage.setScene(scene);
        stage.setTitle("Rolling STL");
        stage.show();

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update(now);
            }
        };
        timer.start();



    }


    public void update(long now){
        //最初の読み込み部分のスクリーンショットをしないために飛ばしています。
        if (count<4) {
            count++;
            return;
        }
        if (count==103) System.exit(0);
        count++;
        stlRotateX.setAngle(stlRotateX.getAngle()+3);
        stlRotateY.setAngle(stlRotateY.getAngle()+5);
        stlRotateZ.setAngle(stlRotateZ.getAngle()+6);
        if (stlRotateX.getAngle()==360.0) stlRotateX.setAngle(0);
        if (stlRotateY.getAngle()==360.0) stlRotateY.setAngle(0);
        if (stlRotateZ.getAngle()==360.0) stlRotateZ.setAngle(0);
        try {
            Thread.sleep(30);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        screenshot();
    }




    public static void main(String[] args) {
        launch(args);
    }
}
