/***************************************************************
* file: Block.java
* author: Zhen Liu, Gabriel Talavera, Joshua Chau
* class: CS 445.01: Computer Graphics
*
* assignment: Quarter Project Final Checkpoint
* date last modified: 5/25/2017
*
* purpose: This class is responsible for making blocks, stores Block ids
* based on the enum blocktype and sets coordinates for blocks and 
* determines whether the block is active or not
****************************************************************/
public class Block {
    private boolean IsActive;
    private BlockType Type;
    private float x, y, z;
    
    // Enumeration of block type, based on their block IDs
    public enum BlockType {
        BlockType_Grass(0),
        BlockType_Sand(1),
        BlockType_Water(2),
        BlockType_Dirt(3),
        BlockType_Stone(4),
        BlockType_Bedrock(5);
        
        private int BlockID;
        
        BlockType(int i) {
            BlockID = i;
        }
        // method: GetID
        // purpose: return block's id
        public int GetID() {
            return BlockID;
        }
        
        // method: SetID
        // purpose: set block's id
        public void SetID(int i) {
            BlockID = i;
        }
    }
    
    // constructor: Block
    // purpose: initializes variables
    public Block(BlockType type) {
        Type = type;
        IsActive = true;
    }
    
    // method: setCoords
    // purpose: sets the block's coordinates in the world
    public void setCoords(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    // method: IsActive
    // purpose: returns if the block is active or not
    public boolean IsActive() {
        return IsActive;
    }
    
    // method: setActive
    // purpose: sets the block to be activated
    public void SetActive(boolean active) {
        IsActive = active;
    }
    
    // method: GetID
    // purpose: returns block's id based on the enum blocktype
    public int GetID() {
        return Type.GetID();
    }
}