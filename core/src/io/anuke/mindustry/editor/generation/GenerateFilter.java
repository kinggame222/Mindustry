package io.anuke.mindustry.editor.generation;

import io.anuke.arc.Core;
import io.anuke.arc.math.Mathf;
import io.anuke.arc.util.Pack;
import io.anuke.arc.util.noise.RidgedPerlin;
import io.anuke.arc.util.noise.Simplex;
import io.anuke.mindustry.editor.MapEditor;
import io.anuke.mindustry.editor.MapGenerateDialog.DummyTile;
import io.anuke.mindustry.world.Block;
import io.anuke.mindustry.world.blocks.Floor;

public abstract class GenerateFilter{
    protected float o = (float)(Math.random()*10000000.0);
    protected long seed;
    protected GenerateInput in;

    public FilterOption[] options;

    protected abstract void apply();

    protected float noise(float x, float y, float scl, float mag){
        return (float)in.noise.octaveNoise2D(1f, 0f, 1f/scl, x + o, y + o)*mag;
    }

    protected float noise(float x, float y, float scl, float mag, float octaves, float persistence){
        return (float)in.noise.octaveNoise2D(octaves, persistence, 1f/scl, x + o, y + o)*mag;
    }

    protected float rnoise(float x, float y, float scl, float mag){
        return in.pnoise.getValue((int)(x + o), (int)(y + o), 1f/scl)*mag;
    }

    public void randomize(){
        seed = Mathf.random(99999999);
    }

    protected float chance(){
        return Mathf.randomSeed(Pack.longInt(in.x, in.y + (int)o));
    }

    public void options(FilterOption... options){
        this.options = options;
    }

    public String name(){
        return Core.bundle.get("filter." + getClass().getSimpleName().toLowerCase().replace("filter", ""), getClass().getSimpleName().replace("Filter", ""));
    }

    public final void apply(GenerateInput in){
        this.in = in;
        apply();
    }

    public static class GenerateInput{
        public Floor srcfloor;
        public Block srcblock;
        public Block srcore;
        public int x, y;

        public MapEditor editor;
        public Block floor, block, ore;

        Simplex noise = new Simplex();
        RidgedPerlin pnoise = new RidgedPerlin(0, 1);
        TileProvider buffer;

        public void begin(MapEditor editor, int x, int y, Block floor, Block block, Block ore){
            this.editor = editor;
            this.floor = this.srcfloor = (Floor)floor;
            this.block = this.srcblock = block;
            this.ore = srcore = ore;
            this.x = x;
            this.y = y;
        }

        public void setFilter(GenerateFilter filter, TileProvider buffer){
            this.buffer = buffer;
            noise.setSeed(filter.seed);
            pnoise.setSeed((int)(filter.seed + 1));
        }

        DummyTile tile(float x, float y){
            return buffer.get(Mathf.clamp((int)x, 0, editor.width() - 1), Mathf.clamp((int)y, 0, editor.height() - 1));
        }

        public interface TileProvider{
            DummyTile get(int x, int y);
        }
    }
}
