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


    private @NotNull Mode       mode;
    private          double     chance;
    private @NotNull List<Drop> match;
    private @NotNull Drop       drops;

    public BlockDropChange(@NotNull Mode mode,

                           double chance,

                           @NotNull List<Drop> match,
                           @NotNull Drop drops) {
        this.mode   = mode;
        this.chance = chance;
        this.match  = match;
        this.drops  = drops;
    }


    public boolean applicable(@NotNull final ItemStack item) {
        return this.getMatch().stream().anyMatch(drop -> drop.applicable(item));
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public double getChance() {
        return chance;
    }

    public void setChance(double chance) {
        this.chance = chance;
    }

    public List<Drop> getMatch() {
        return match;
    }

    public void setMatch(List<Drop> match) {
        this.match = match;
    }

    public Drop getDrops() {
        return drops;
    }

    public void setDrops(Drop drops) {
        this.drops = drops;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (BlockDropChange) obj;
        return Objects.equals(this.mode, that.mode) &&
               Double.doubleToLongBits(this.chance) == Double.doubleToLongBits(that.chance) &&
               Objects.equals(this.match, that.match) &&
               Objects.equals(this.drops, that.drops);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mode, chance, match, drops);
    }

    @Override
    public String toString() {
        return "BlockDropChange[" +
               "mode=" + mode + ", " +
               "chance=" + chance + ", " +
               "match=" + match + ", " +
               "drops=" + drops + ']';
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


        private @NotNull Material type;
        private          int      amount;

        public Drop(@NotNull Material type, int amount) {
            this.type   = type;
            this.amount = amount;
        }


        public Material getType() {
            return type;
        }

        public void setType(Material type) {
            this.type = type;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }


        @Contract(" -> new")
        public @NotNull ItemStack toBukkit() {
            return new ItemStack(this.getType(), this.getAmount());
        }

        public boolean applicable(@NotNull final ItemStack item) {
            return item.getType() == this.getType() && (this.getAmount() == -1 || item.getAmount() == this.getAmount());
        }


        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Drop) obj;
            return Objects.equals(this.type, that.type) &&
                   this.amount == that.amount;
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, amount);
        }

        @Override
        public String toString() {
            return "Drop[" +
                   "type=" + type + ", " +
                   "amount=" + amount + ']';
        }

    }

}
