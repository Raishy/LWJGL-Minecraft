/***************************************************************
* file: Chunk.java
* author: Zhen Liu, Gabriel Talavera, Joshua Chau
* class: CS 445.01: Computer Graphics
*
* assignment: Quarter Project Final Checkpoint
* date last modified: 5/25/2017
*
* purpose: This class is responsible for chunks
*
****************************************************************/
import java.nio.FloatBuffer;
import java.util.Random;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Chunk {
    static final int CHUNK_SIZE = 60;
    static final int CUBE_LENGTH = 2;
    private Block[][][] Blocks;
    private int VBOVertexHandle;
    private int VBOColorHandle;
    private int StartX, StartY, StartZ;
    private Random r;
    
    private int VBOTextureHandle;
    private Texture texture;

    // method: render
    // purpose: render this chunk as a single mesh
    public void render() {
        glPushMatrix();
        glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
        glVertexPointer(3, GL_FLOAT, 0, 0L);
        glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
        glColorPointer(3, GL_FLOAT, 0, 0L);
        
        glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
        glBindTexture(GL_TEXTURE_2D, 1);
        glTexCoordPointer(2, GL_FLOAT, 0, 0L);

        glDrawArrays(GL_QUADS, 0, CHUNK_SIZE *CHUNK_SIZE*CHUNK_SIZE * 24);
        glPopMatrix();
    }
    // method: rebuildMesh
    // purpose: rebuild mesh of the chunk to reflect changes
    public void rebuildMesh(float startX, float startY, float startZ) {
        SimplexNoise noise = new SimplexNoise(CHUNK_SIZE, 0.3f, (int) System.currentTimeMillis());
        
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();
        
        FloatBuffer VertexPositionData = BufferUtils.createFloatBuffer((CHUNK_SIZE 
                            * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexColorData = BufferUtils.createFloatBuffer((CHUNK_SIZE
                            * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexTextureData = BufferUtils.createFloatBuffer((CHUNK_SIZE
                            * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
  
        for (float x = 0; x < CHUNK_SIZE; x++) {
            for (float z = 0; z < CHUNK_SIZE; z++) {  
                float height = (float) (8 *(1 + noise.getNoise((int) x, (int) z) * CUBE_LENGTH));
                if(height > CHUNK_SIZE)
                    height = CHUNK_SIZE;
                //Changed height <= 0 to <=1 and height = 1 to = 2
                //to prevent single layer of bedrock.
                if(height <= 1)
                    height = 2;
                for (float y = 0; y < height; y++) {           
                    VertexPositionData.put(createCube((float) (startX + x * CUBE_LENGTH), 
                            (float) (startY + y * CUBE_LENGTH + (int) (CHUNK_SIZE * .8)),
                            (float) (startZ + z * CUBE_LENGTH)));
                    
                    VertexColorData.put(createCubeVertexCol(getCubeColor(
                            Blocks[(int) x][(int) y][(int) z])));
                    //Bottom level bedrock
                    if(y == 0){
                        VertexTextureData.put(createTexCube((float) 0, (float) 0, 5));
                    } else if(y >= height-1){
                    //top level grass bottom level water
                        if(height<=7){
                              VertexTextureData.put(createTexCube((float) 0, (float) 0, 2));
                              while((++y)<7){
                                  VertexPositionData.put(createCube((float) (startX + x * CUBE_LENGTH), 
                                  (float) (startY + y * CUBE_LENGTH + (int) (CHUNK_SIZE * .8)),
                                  (float) (startZ + z * CUBE_LENGTH)));
                                  VertexColorData.put(createCubeVertexCol(getCubeColor(
                                  Blocks[(int) x][(int) y][(int) z])));
                                  VertexTextureData.put(createTexCube((float) 0, (float) 0, 2));
                              }
                    
                        }else{       
                            VertexTextureData.put(createTexCube((float) 0, (float) 0, 
                                0));                                
                        }
                    }
                    else{
                        //Dirt or stone in between.
                        if(r.nextFloat()<.5){
                            VertexTextureData.put(createTexCube((float) 0, (float) 0, 
                                4));
                        }else{
                            VertexTextureData.put(createTexCube((float) 0, (float) 0, 
                                3));
                        }
                    }
                } 
            }
        }
        VertexColorData.flip();
        VertexPositionData.flip();
        VertexTextureData.flip();

        glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexPositionData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexColorData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexTextureData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
    
    // method: createCubeVertexCol
    // purpose: return array with the contents of the cubecolorArray
    // repeats 6 times for each face of the cube
    private float[] createCubeVertexCol(float[] CubeColorArray) {
        float[] cubeColors = new float[CubeColorArray.length * 4 * 6];
        for( int i = 0; i < cubeColors.length; i++) {
            cubeColors[i] = CubeColorArray[i % CubeColorArray.length];
        }
        return cubeColors;
    }
    // method: createCube
    // purpose: returns array with vertex data of a cube (center at x, y, z)
    public static float[] createCube(float x, float y, float z) {
        int offset = CUBE_LENGTH / 2;
        return new float[] {
            // Top Quad
            x + offset, y + offset, z, 
            x - offset, y + offset, z, 
            x - offset, y + offset, z - CUBE_LENGTH,
            x + offset, y + offset, z - CUBE_LENGTH,
            // Bottom Quad
            x + offset, y - offset, z - CUBE_LENGTH,
            x - offset, y - offset, z - CUBE_LENGTH,
            x - offset, y - offset, z, 
            x + offset, y - offset, z,
            // Front Quad
            x + offset, y + offset, z - CUBE_LENGTH, 
            x - offset, y + offset, z - CUBE_LENGTH,
            x - offset, y - offset, z - CUBE_LENGTH,
            x + offset, y - offset, z - CUBE_LENGTH,
            // Back Quad
            x + offset, y - offset, z,
            x - offset, y - offset, z,
            x - offset, y + offset, z,
            x + offset, y + offset, z,
            // Left Quad
            x - offset, y + offset, z - CUBE_LENGTH,
            x - offset, y + offset, z,
            x - offset, y - offset, z,
            x - offset, y - offset, z - CUBE_LENGTH,
            // Right Quad
            x + offset, y + offset, z,
            x + offset, y + offset, z - CUBE_LENGTH,
            x + offset, y - offset, z - CUBE_LENGTH,
            x + offset, y - offset, z 
        };   
    }
    
    //method: createTexCube
    //purpose: returns array with coordinate value of textures of block based
    // on its blocktype
    public static float[] createTexCube(float x, float y, int texture) {
        float offset = (1024f/16)/1024f;
        
        switch (texture) {
            case 0: //grass
                return new float[] {
                // TOP QUAD
                x + offset*3, y + offset*10,
                x + offset*2, y + offset*10,
                x + offset*2, y + offset*9,
                x + offset*3, y + offset*9,
                // BOTTOM QUAD
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                // FRONT QUAD
                x + offset*3, y + offset*0,
                x + offset*4, y + offset*0,
                x + offset*4, y + offset*1, 
                x + offset*3, y + offset*1,
                // BACK QUAD
                x + offset*4, y + offset*1,
                x + offset*3, y + offset*1,
                x + offset*3, y + offset*0,
                x + offset*4, y + offset*0,
                // LEFT QUAD
                x + offset*3, y + offset*0,
                x + offset*4, y + offset*0,
                x + offset*4, y + offset*1,
                x + offset*3, y + offset*1,
                // RIGHT QUAD
                x + offset*3, y + offset*0,
                x + offset*4, y + offset*0,
                x + offset*4, y + offset*1,
                x + offset*3, y + offset*1
                };
                
            case 1: //sand
                return new float[] {
                // TOP QUAD
                x + offset*3, y + offset*2,
                x + offset*2, y + offset*2,
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1,
                // BOTTOM SQUAD
                x + offset*3, y + offset*2,
                x + offset*2, y + offset*2,
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1,
                // FRONT QUAD
                x + offset*3, y + offset*2,
                x + offset*2, y + offset*2,
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1,
                // BACK QUAD
                x + offset*3, y + offset*2,
                x + offset*2, y + offset*2,
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1,
                // LEFT QUAD
                x + offset*3, y + offset*2,
                x + offset*2, y + offset*2,
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1,
                // RIGHT QUAD
                x + offset*3, y + offset*2,
                x + offset*2, y + offset*2,
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1
                };
            
            case 2: //water
                return new float[] {
                //TOP
                x + offset*1, y + offset*10,
                x + offset*0, y + offset*10,
                x + offset*0, y + offset*9,
                x + offset*1, y + offset*9,
                //BOTTOM
                x + offset*1, y + offset*10,
                x + offset*0, y + offset*10,
                x + offset*0, y + offset*9,
                x + offset*1, y + offset*9,
                //FRONT
                x + offset*1, y + offset*10,
                x + offset*0, y + offset*10,
                x + offset*0, y + offset*9,
                x + offset*1, y + offset*9,
                //BACK
                x + offset*1, y + offset*10,
                x + offset*0, y + offset*10,
                x + offset*0, y + offset*9,
                x + offset*1, y + offset*9,
                //LEFT
                x + offset*1, y + offset*10,
                x + offset*0, y + offset*10,
                x + offset*0, y + offset*9,
                x + offset*1, y + offset*9,
                //RIGHT
                x + offset*1, y + offset*10,
                x + offset*0, y + offset*10,
                x + offset*0, y + offset*9,
                x + offset*1, y + offset*9,
                };
                
            case 3: //dirt
                return new float[] {
                //TOP
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                //BOTTOM
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                //FRONT
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                //BACK
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                //LEFT
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                //RIGHT
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0
                };
                
            case 4: //stone
                return new float[] {
                //TOP
                x + offset*1, y + offset*2,
                x + offset*0, y + offset*2,
                x + offset*0, y + offset*1,
                x + offset*1, y + offset*1,
                //BOTTOM
                x + offset*1, y + offset*2,
                x + offset*0, y + offset*2,
                x + offset*0, y + offset*1,
                x + offset*1, y + offset*1,
                //FRONT
                x + offset*1, y + offset*2,
                x + offset*0, y + offset*2,
                x + offset*0, y + offset*1,
                x + offset*1, y + offset*1,
                //BACK
                x + offset*1, y + offset*2,
                x + offset*0, y + offset*2,
                x + offset*0, y + offset*1,
                x + offset*1, y + offset*1,
                //LEFT
                x + offset*1, y + offset*2,
                x + offset*0, y + offset*2,
                x + offset*0, y + offset*1,
                x + offset*1, y + offset*1,
                //RIGHT
                x + offset*1, y + offset*2,
                x + offset*0, y + offset*2,
                x + offset*0, y + offset*1,
                x + offset*1, y + offset*1
                };
                
            case 5: //bedrock
                return new float[] {
                //TOP
                x + offset*2, y + offset*2,
                x + offset*1, y + offset*2,
                x + offset*1, y + offset*1,
                x + offset*2, y + offset*1,
                //BOTTOM
                x + offset*2, y + offset*2,
                x + offset*1, y + offset*2,
                x + offset*1, y + offset*1,
                x + offset*2, y + offset*1,
                //FRONT
                x + offset*2, y + offset*2,
                x + offset*1, y + offset*2,
                x + offset*1, y + offset*1,
                x + offset*2, y + offset*1,
                //BACK
                x + offset*2, y + offset*2,
                x + offset*1, y + offset*2,
                x + offset*1, y + offset*1,
                x + offset*2, y + offset*1,
                //LEFT
                x + offset*2, y + offset*2,
                x + offset*1, y + offset*2,
                x + offset*1, y + offset*1,
                x + offset*2, y + offset*1,
                //RIGHT
                x + offset*2, y + offset*2,
                x + offset*1, y + offset*2,
                x + offset*1, y + offset*1,
                x + offset*2, y + offset*1
                };
                
            default:
                return new float[] {};
        }
    }
    
    // method: getCubeColor
    // purpose: returns array with color value of faces of block based
    // on its blocktype
    private float[] getCubeColor(Block block) {
//        switch (block.GetID()) {
//            case 0:
//                return new float[] {0f, 1f, 0f}; // Grass
//            case 1: 
//                return new float[] {1f, 250/255f, 180/255f}; // Sand
//            case 2:
//                return new float[] {0f, 0f, 1f}; // Water 
//            case 3:
//                return new float[] {139/255f, 90/255f, 43/255f}; // Dirt
//            case 4:
//                return new float[] {128/255f, 128/255f, 128/255f}; //Rock
//            case 5:
//                return new float[] {0f, 0f, 0f}; // Bedrock
//            default:
//                return new float[] {1f, 1f, 1f};
//        }
        
        return new float[] {1f, 1f, 1f};
    }
    
    // Constructor for chunks
    // purpose: initialize variables and creates a chunk starting at startX, Y, Z
    public Chunk(int startX, int startY, int startZ) {
        try {
            texture = TextureLoader.getTexture("PNG",
            ResourceLoader.getResourceAsStream("terrain.png"));
        } catch(Exception e) {
            System.out.print("ER-ROAR!");
        }
        
        r = new Random();
        Blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int y = 0; y < CHUNK_SIZE; y++) {
                for (int z = 0; z < CHUNK_SIZE; z++) {
              //      Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Grass);
                    if (r.nextFloat() > 0.83f) {
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Grass);
                    } else if (r.nextFloat() > 0.66f) {
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Dirt);
                    } else if (r.nextFloat() > 0.49f) {
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Water);
                    } else if (r.nextFloat() > 0.32f) {
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Stone);
                    } else if (r.nextFloat() > 0.15f) {
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Sand);
                    } else {
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Bedrock);
                    }
                }
            }
        }
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers(); 
        StartX = startX;
        StartY = startY;
        StartZ = startZ;
        rebuildMesh(startX, startY, startZ);
    }
}
