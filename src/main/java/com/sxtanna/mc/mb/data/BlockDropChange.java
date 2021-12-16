package com.sxtanna.mc.mb.data;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public final class BlockDropChange {

    @Contract("_, _ -> new")
    public static @NotNull BlockDropChange add(@NotNull final Drop drop, @NotNull final Drop... match) {
        return new BlockDropChange(Mode.ADD, 100.0, List.of(match), drop);
    }

    @Contract("_, _, _ -> new")
    public static @NotNull BlockDropChange add(final double chance, @NotNull final Drop drop, @NotNull final Drop... match) {
        return new BlockDropChange(Mode.ADD, chance, List.of(match), drop);
    }

    @Contract("_, _, _ -> new")
    public static @NotNull BlockDropChange add(final double chance, @NotNull final Drop drop, @NotNull final Collection<Drop> match) {
        return new BlockDropChange(Mode.ADD, chance, List.copyOf(match), drop);
    }

    @Contract("_, _ -> new")
    public static @NotNull BlockDropChange set(@NotNull final Drop drop, @NotNull final Drop... match) {
        return new BlockDropChange(Mode.SET, 100.0, List.of(match), drop);
    }

    @Contract("_, _, _ -> new")
    public static @NotNull BlockDropChange set(final double chance, @NotNull final Drop drop, @NotNull final Drop... match) {
        return new BlockDropChange(Mode.SET, chance, List.of(match), drop);
    }

    @Contract("_, _, _ -> new")
    public static @NotNull BlockDropChange set(final double chance, @NotNull final Drop drop, @NotNull final Collection<Drop> match) {
        return new BlockDropChange(Mode.SET, chance, List.copyOf(match), drop);
    }


    private Mode       mode;
    private double     chance;
    private List<Drop> match;
    private Drop       drops;


    @Contract(pure = true)
    public BlockDropChange() {
    }

    @Contract(pure = true)
    public BlockDropChange(@NotNull final Mode mode, final double chance, @NotNull final List<Drop> match, @NotNull final Drop drops) {
        this.mode   = mode;
        this.chance = chance;
        this.match  = match;
        this.drops  = drops;
    }


    public boolean applicable(@NotNull final ItemStack item) {
        return this.getMatch().stream().anyMatch(drop -> drop.applicable(item));
    }


    @Contract(pure = true)
    public Mode getMode() {
        return this.mode;
    }

    @Contract(mutates = "this")
    public void setMode(@NotNull final Mode mode) {
        this.mode = mode;
    }

    @Contract(pure = true)
    public double getChance() {
        return this.chance;
    }

    @Contract(mutates = "this")
    public void setChance(final double chance) {
        this.chance = chance;
    }

    @Contract(pure = true)
    public List<Drop> getMatch() {
        return this.match;
    }

    @Contract(mutates = "this")
    public void setMatch(@NotNull final List<Drop> match) {
        this.match = match;
    }

    @Contract(pure = true)
    public Drop getDrops() {
        return this.drops;
    }

    @Contract(mutates = "this")
    public void setDrops(@NotNull final Drop drops) {
        this.drops = drops;
    }


    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BlockDropChange that)) return false;

        return Double.compare(that.getChance(), getChance()) == 0 &&
               getMode() == that.getMode() &&
               Objects.equals(getMatch(), that.getMatch()) &&
               Objects.equals(getDrops(), that.getDrops());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMode(), getChance(), getMatch(), getDrops());
    }


    @Override
    public @NotNull String toString() {
        return "BlockDropChange[mode=%s, chance=%s, match=%s, drops=%s]".formatted(getMode(),
                                                                                   getChance(),
                                                                                   getMatch(),
                                                                                   getDrops());
    }


    public enum Mode {
        ADD,
        SET,
    }


    public static final class Drop {

        @Contract("_ -> new")
        public static @NotNull Drop of(@NotNull final Material type) {
            return of(type, -1);
        }

        @Contract("_, _ -> new")
        public static @NotNull Drop of(@NotNull final Material type, int amount) {
            return new Drop(type, amount);
        }

        private Material type;
        private int      amount;


        @Contract(pure = true)
        public Drop() {
        }

        @Contract(pure = true)
        public Drop(@NotNull final Material type, int amount) {
            this.type   = type;
            this.amount = amount;
        }


        @Contract(pure = true)
        public Material getType() {
            return this.type;
        }

        @Contract(mutates = "this")
        public void setType(@NotNull final Material type) {
            this.type = type;
        }

        @Contract(pure = true)
        public int getAmount() {
            return this.amount;
        }

        @Contract(mutates = "this")
        public void setAmount(final int amount) {
            this.amount = amount;
        }


        @Contract(" -> new")
        public @NotNull ItemStack toBukkit() {
            return new ItemStack(this.getType(), this.getAmount());
        }

        public boolean applicable(@NotNull final ItemStack item) {
            return item.getType() == this.getType() && (this.getAmount() == -1 || item.getAmount() == this.getAmount());
        }


        @Contract(value = "null -> false", pure = true)
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Drop drop)) return false;

            return getAmount() == drop.getAmount() &&
                   getType() == drop.getType();
        }

        @Override
        public int hashCode() {
            return Objects.hash(getType(), getAmount());
        }


        @Override
        public String toString() {
            return "Drop[type=%s, amount=%d]".formatted(getType(),
                                                        getAmount());
        }

    }

}
