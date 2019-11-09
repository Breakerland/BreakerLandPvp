package fr.breakerland.breakerlandpvp;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class BreakerLandPvp extends JavaPlugin implements Listener {
	private final Set<UUID> pvp = new HashSet<>();

	@Override
	public void onEnable() {
		saveDefaultConfig();
		getCommand("pvp").setExecutor(this);
		getServer().getPluginManager().registerEvents(this, this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (! (sender instanceof Player))
			return false;

		UUID uuid = ((Player) sender).getUniqueId();
		if (args.length > 0)
			if (args[0].equalsIgnoreCase("on"))
				if (pvp.add(uuid))
					sender.sendMessage(getMessage("pvpEnable", "&cAttention, vous avec activé(e) le PVP."));
				else
					sender.sendMessage(parseColor(getConfig().getString("pvpNotDisable", "&cVous avez déjà activé(e) le PVP.")));
			else if (args[0].equalsIgnoreCase("off"))
				if (pvp.remove(uuid))
					sender.sendMessage(parseColor(getConfig().getString("pvpDisable", "&cVous avez désactivé(e) le PVP.")));
				else
					sender.sendMessage(parseColor(getConfig().getString("pvpNotEnable", "&cLe PVP est déjà désactivé.")));
			else
				return false;
		else if (pvp.add(uuid))
			sender.sendMessage(parseColor(getConfig().getString("pvpEnable", "&cAttention, vous avez activé(e) le PVP.")));
		else if (pvp.remove(uuid))
			sender.sendMessage(parseColor(getConfig().getString("pvpDisable", "&cVous avez désactivé(e) le PVP.")));

		return true;
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onAttack(EntityDamageByEntityEvent e) {
		if (e.getEntityType() == EntityType.PLAYER) {
			Player damager;
			EntityType type = e.getDamager().getType();
			if (type == EntityType.PLAYER)
				damager = (Player) e.getDamager();
			else if ( (type == EntityType.ARROW || type == EntityType.TRIDENT || type == EntityType.SPECTRAL_ARROW) && ((Projectile) e.getDamager()).getShooter() instanceof Player)
				damager = (Player) ((Projectile) e.getDamager()).getShooter();
			else
				return;

			if (!pvp.contains(e.getEntity().getUniqueId()))
				damager.sendMessage(getMessage("attackerNoPvp", "&cCe joueur doit activer le PVP pour combattre."));
			else if (!pvp.contains(damager.getUniqueId()))
				damager.sendMessage(getMessage("defenderNoPvp", "&cVous devez activer le PVP pour combattre."));
			else
				return;

			e.setCancelled(true);
		}
	}

	private String getMessage(String node, String def) {
		return parseColor(getConfig().getString(node, def));
	}

	private String parseColor(String input) {
		return ChatColor.translateAlternateColorCodes('&', input);
	}
}