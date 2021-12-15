package com.sxtanna.mc.mb.data;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public record BlockDropChange(@NotNull Mode mode,

                              double chance,

                              @NotNull List<Drop> match,
                              @NotNull Drop drops) {


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


    public boolean applicable(@NotNull final ItemStack item) {
        return this.match().stream().anyMatch(drop -> drop.applicable(item));
    }


    public enum Mode {
        ADD,
        SET,
    }

    public record Drop(@NotNull Material type, int amount) {


        @Contract("_ -> new")
        public static @NotNull Drop of(@NotNull final Material type) {
            return of(type, -1);
        }

        @Contract("_, _ -> new")
        public static @NotNull Drop of(@NotNull final Material type, int amount) {
            return new Drop(type, amount);
        }


        @Contract(" -> new")
        public @NotNull ItemStack toBukkit() {
            return new ItemStack(this.type(), this.amount());
        }

        public boolean applicable(@NotNull final ItemStack item) {
            return item.getType() == this.type() && (this.amount() == -1 || item.getAmount() == this.amount());
        }

    }

}
