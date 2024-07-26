package cn.ussshenzhou.tellmewhere.util;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @author USS_Shenzhou
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AlwaysZeroRandomSource implements RandomSource {

    @Override
    public RandomSource fork() {
        return new AlwaysZeroRandomSource();
    }

    @Override
    public PositionalRandomFactory forkPositional() {
        return new AlwaysZeroRandomSourceFactory();
    }

    @Override
    public void setSeed(long seed) {

    }

    @Override
    public int nextInt() {
        return 0;
    }

    @Override
    public int nextInt(int bound) {
        return 0;
    }

    @Override
    public long nextLong() {
        return 0;
    }

    @Override
    public boolean nextBoolean() {
        return false;
    }

    @Override
    public float nextFloat() {
        return 0;
    }

    @Override
    public double nextDouble() {
        return 0;
    }

    @Override
    public double nextGaussian() {
        return 0;
    }

    public static class AlwaysZeroRandomSourceFactory implements PositionalRandomFactory{

        @Override
        public RandomSource fromHashOf(String name) {
            return new AlwaysZeroRandomSource();
        }

        @Override
        public RandomSource fromSeed(long seed) {
            return new AlwaysZeroRandomSource();
        }

        @Override
        public RandomSource at(int x, int y, int z) {
            return new AlwaysZeroRandomSource();
        }

        @Override
        public void parityConfigString(StringBuilder builder) {
        }
    }
}
