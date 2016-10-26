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
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Main extends Application {

    private static final String stl = "mono.stl";
    private static int count = 0;
    private static int n = 0;
    private AnimationTimer timer;
    private final Rotate cameraRotateX = new Rotate(0, Rotate.X_AXIS);
    private final Rotate cameraRotateY = new Rotate(0, Rotate.Y_AXIS);
    private final Rotate cameraRotateZ = new Rotate(0, Rotate.Z_AXIS);
    private final Translate cameraTranslate = new Translate(0, 0 , -260);
    private final Rotate stlRotateX = new Rotate(0, Rotate.X_AXIS);
    private final Rotate stlRotateY = new Rotate(0, Rotate.Y_AXIS);
    private final Rotate stlRotateZ = new Rotate(0, Rotate.Z_AXIS);
    private final Translate stlTranslate = new Translate(-50, 75 ,0);
    private static Scene scene;
    private static Group root;
    private static AmbientLight ambientLight;
    private static PointLight pointLight;
    private static PerspectiveCamera cam;
    private static final Color stlColor = Color.WHITESMOKE;
    private static final String[] filename = stl.split("\\.");
    private static final File rmfile = new File("./" + filename[0] + "/" + filename[0] + n + ".png");

    //stl読み込み
    static MeshView[] loadMeshViews() {
        File file = new File(stl);
        StlMeshImporter importer = new StlMeshImporter();
        importer.read(file);
        Mesh mesh = importer.getImport();
        return new MeshView[] { new MeshView(mesh) };
    }

    //スクショ保存用のディレクトリの作成、シーンの作成
    private Group buildScene() {
        File newdir = new File(filename[0]);
        newdir.mkdir();
        MeshView[] meshViews = loadMeshViews();
        for (int i = 0; i < meshViews.length; i++) {
            PhongMaterial stlMaterial = new PhongMaterial(stlColor);
            stlMaterial.setSpecularPower(3);
            meshViews[i].setMaterial(stlMaterial);
            meshViews[i].getTransforms().addAll(stlTranslate,stlRotateX,stlRotateY,stlRotateZ);
        }

        //回転座標の更新
        stlRotateX.setPivotY(-85);
        stlRotateX.setPivotZ(-25);
        stlRotateY.setPivotX(55);
        stlRotateY.setPivotZ(-31);
        stlRotateZ.setPivotX(55);
        stlRotateZ.setPivotY(-85);
        root = new Group(meshViews);

        return root;
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
            ImageIO.write(image, "PNG", new File("./" + filename[0] + "/" + filename[0] + n + ".png"));

            //レンダリングが最初の方間に合わず真っ黒画面がスクリーンショットされるのでrmします。
            if(count<5){
                rmfile.delete();
                return;
            }
            n++;
        } catch (IOException e) {
            e.printStackTrace();
        }
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

        if (count==505) System.exit(0);
        count++;
        //回転
        stlRotateX.setAngle(stlRotateX.getAngle()+2);
        stlRotateY.setAngle(stlRotateY.getAngle()+3);
        stlRotateZ.setAngle(stlRotateZ.getAngle()+0.5);
        if (stlRotateX.getAngle()>360.0) stlRotateX.setAngle(stlRotateX.getAngle()-360);
        if (stlRotateY.getAngle()>360.0) stlRotateY.setAngle(stlRotateY.getAngle()-360);
        if (stlRotateZ.getAngle()>360.0) stlRotateZ.setAngle(stlRotateZ.getAngle()-360);
        screenshot();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
