/***************************************************************
* file: MineCraft.java
* author: Zhen Liu, Gabriel Talavera, Joshua Chau
* class: CS 445.01: Computer Graphics
*
* assignment: Quarter Project Final Checkpoint
* date last modified: 5/25/2017
*
* purpose: This program creates a cube and allows for control over
* the camera in 3d space using the wasd, shift, mouse, and space keys
*
****************************************************************/
import java.io.File;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.glu.GLU;

public class MineCraft {
    private CameraController fp;// = new CameraController(0f,0f,0f);
  //  private DisplayMode displayMode;
    
    private FloatBuffer lightPosition;
    private FloatBuffer whiteLight;
    
    //method: start()
    //purpose: This method is called once the program begins and it
    //sets up the window and renders everything
    public void start(){
        try{
            createWindow();       
            Keyboard.create();
            initGL();
            fp = new CameraController(0f,0f,0f);
            fp.gameLoop();
        }catch(Exception e){
            System.out.println(e);
        }
    }
    //metho: createWindow()
    //purpose: this method will create the window and set up the
    //display settings
    private void createWindow() throws Exception {
         Display.setFullscreen(false);
       /*  DisplayMode d[] = Display.getAvailableDisplayModes();
         for (int i = 0; i < d.length; i++) {
             if (d[i].getWidth() == 640
                 && d[i].getHeight() == 480
                 && d[i].getBitsPerPixel() == 32) {
                 displayMode = d[i];
                 break;
             }
         }
*/
         Display.setDisplayMode(new DisplayMode(640, 480));//displayMode);
         Display.setTitle("MineCraft");
         Display.create();
     }
    //method: initGL()
    //purpose: This method sets up the openGL attributes
    //and render settings;
    private void initGL() 
    {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        GLU.gluPerspective(100.0f, 640/480, 0.1f, 300.0f);
      /*  GLU.gluPerspective(100.0f, (float)displayMode.getWidth()/(float)
        displayMode.getHeight(), 0.1f, 300.0f);
*/
        glMatrixMode(GL_MODELVIEW);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_COLOR_ARRAY);
        glEnable(GL_DEPTH_TEST);
        
        glEnable(GL_TEXTURE_2D);
        glEnableClientState (GL_TEXTURE_COORD_ARRAY);
        
        initLightArrays();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition); //sets our light’s position
        glLight(GL_LIGHT0, GL_SPECULAR, whiteLight);//sets our specular light
        glLight(GL_LIGHT0, GL_DIFFUSE, whiteLight);//sets our diffuse light
        glLight(GL_LIGHT0, GL_AMBIENT, whiteLight);//sets our ambient light
        glEnable(GL_LIGHTING);//enables our lighting
        glEnable(GL_LIGHT0);//enables light0
    }
    
    //method: initLightArrays
    //purose: this method initalizes the attributes for the light source
    private void initLightArrays() {
        lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(0.0f).put(0.0f).put(0.0f).put(1.0f).flip();
        whiteLight = BufferUtils.createFloatBuffer(4);
        whiteLight.put(1.0f).put(1.0f).put(1.0f).put(0.0f).flip();
    }

    //method: main(String[] args)
    //purpose: this method creates a new instance of the program
    //and runs start.
    public static void main(String[] args) {
        File JGLLib = null;
        switch(LWJGLUtil.getPlatform())
        {
            case LWJGLUtil.PLATFORM_WINDOWS:
            {
                JGLLib = new File("./native/windows/");
            }
            break;

            case LWJGLUtil.PLATFORM_LINUX:
            {
                JGLLib = new File("./native/linux/");
            }
            break;

            case LWJGLUtil.PLATFORM_MACOSX:
            {
                JGLLib = new File("./native/macosx/");
            }
            break;
        }
        if(JGLLib == null){
            System.out.println("This program only supports Windows/Linux/Mac");
            System.exit(0);
        }
        System.setProperty("org.lwjgl.librarypath", JGLLib.getAbsolutePath());
        MineCraft mc = new MineCraft();
        mc.start();
    }
    
}
