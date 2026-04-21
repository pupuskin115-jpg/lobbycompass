package com.lobbycompass;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.Arrays;

public final class Main extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("§a[LobbyCompass] Плагин включен!");
    }

    @Override
    public void onDisable() {
        getLogger().info("§c[LobbyCompass] Плагин выключен.");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        giveCompass(player);
    }

    private void giveCompass(Player player) {
        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta meta = compass.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text("Выбор режима")
                .color(NamedTextColor.GOLD)
                .decoration(TextDecoration.ITALIC, false));
            meta.lore(Arrays.asList(
                Component.text("Правый клик для выбора мира").color(NamedTextColor.GRAY)
            ));
            compass.setItemMeta(meta);
        }
        player.getInventory().setItem(4, compass);
    }

    @EventHandler
    public void onCompassClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        ItemStack item = event.getItem();

        if (item == null || item.getType() != Material.COMPASS) return;
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

        event.setCancelled(true);
        openWorldMenu(player);
    }

    private void openWorldMenu(Player player) {
        player.sendMessage("");
        player.sendMessage(Component.text("========== ВЫБОР РЕЖИМА ==========").color(NamedTextColor.GOLD));
        player.sendMessage("");
        player.sendMessage(Component.text("1. ").color(NamedTextColor.GREEN)
            .append(Component.text("Анархия").color(NamedTextColor.RED))
            .append(Component.text(" — полный хаос, всё можно").color(NamedTextColor.GRAY)));
        player.sendMessage(Component.text("2. ").color(NamedTextColor.GREEN)
            .append(Component.text("Гриф").color(NamedTextColor.YELLOW))
            .append(Component.text(" — сражения и разрушения").color(NamedTextColor.GRAY)));
        player.sendMessage("");
        player.sendMessage(Component.text("Напиши в чат: /join anarchy или /join grief").color(NamedTextColor.GOLD));
        player.sendMessage("");
        player.sendMessage(Component.text("=====================================").color(NamedTextColor.GOLD));
        
        // Регистрируем команды, если ещё не зарегистрированы
        registerCommands();
    }

    private boolean commandsRegistered = false;
    
    private void registerCommands() {
        if (commandsRegistered) return;
        
        getCommand("join").setExecutor((sender, command, label, args) -> {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Только для игроков!");
                return true;
            }
            Player p = (Player) sender;
            if (args.length == 0) {
                p.sendMessage(Component.text("Использование: /join <anarchy|grief>").color(NamedTextColor.RED));
                return true;
            }
            
            String world = args[0].toLowerCase();
            if (world.equals("anarchy")) {
                p.teleport(Bukkit.getWorld("anarchy").getSpawnLocation());
                p.sendMessage(Component.text("Ты телепортирован в мир Анархии!").color(NamedTextColor.GREEN));
            } else if (world.equals("grief")) {
                p.teleport(Bukkit.getWorld("grief").getSpawnLocation());
                p.sendMessage(Component.text("Ты телепортирован в мир Грифа!").color(NamedTextColor.GREEN));
            } else {
                p.sendMessage(Component.text("Неизвестный мир! Доступны: anarchy, grief").color(NamedTextColor.RED));
            }
            return true;
        });
        
        commandsRegistered = true;
    }
}
