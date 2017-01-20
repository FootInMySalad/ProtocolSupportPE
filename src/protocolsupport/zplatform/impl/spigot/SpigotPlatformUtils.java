package protocolsupport.zplatform.impl.spigot;

import java.security.KeyPair;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import org.bukkit.Achievement;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.craftbukkit.v1_11_R1.CraftServer;
import org.bukkit.craftbukkit.v1_11_R1.CraftStatistic;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_11_R1.util.CraftIconCache;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.CachedServerIcon;
import org.spigotmc.SpigotConfig;

import com.mojang.authlib.minecraft.MinecraftSessionService;

import io.netty.channel.Channel;
import net.minecraft.server.v1_11_R1.EnumProtocol;
import net.minecraft.server.v1_11_R1.Item;
import net.minecraft.server.v1_11_R1.LocaleI18n;
import net.minecraft.server.v1_11_R1.MinecraftServer;
import net.minecraft.server.v1_11_R1.MobEffectList;
import net.minecraft.server.v1_11_R1.NBTTagCompound;
import net.minecraft.server.v1_11_R1.NetworkManager;
import net.minecraft.server.v1_11_R1.SoundEffect;
import protocolsupport.zplatform.PlatformUtils;
import protocolsupport.zplatform.impl.spigot.itemstack.SpigotNBTTagCompoundWrapper;
import protocolsupport.zplatform.itemstack.NBTTagCompoundWrapper;
import protocolsupport.zplatform.network.NetworkListenerState;

public class SpigotPlatformUtils implements PlatformUtils {

	public static NetworkListenerState netStateFromEnumProtocol(EnumProtocol state) {
		switch (state) {
			case HANDSHAKING: {
				return NetworkListenerState.HANDSHAKING;
			}
			case PLAY: {
				return NetworkListenerState.PLAY;
			}
			case LOGIN: {
				return NetworkListenerState.LOGIN;
			}
			case STATUS: {
				return NetworkListenerState.STATUS;
			}
			default: {
				throw new IllegalArgumentException("Unknown state " + state);
			}
		}
	}

	public static NetworkListenerState getNetStateFromChannel(Channel channel) {
		return netStateFromEnumProtocol(channel.attr(NetworkManager.c).get());
	}

	public static MinecraftServer getServer() {
		return ((CraftServer) Bukkit.getServer()).getServer();
	}

	public String localize(String key, Object... args) {
		return LocaleI18n.a(key, args);
	}

	public ItemStack createItemStackFromNBTTag(NBTTagCompoundWrapper tag) {
		return CraftItemStack.asCraftMirror(new net.minecraft.server.v1_11_R1.ItemStack(((SpigotNBTTagCompoundWrapper) tag).unwrap()));
	}

	public NBTTagCompoundWrapper createNBTTagFromItemStack(ItemStack itemstack) {
		net.minecraft.server.v1_11_R1.ItemStack nmsitemstack = CraftItemStack.asNMSCopy(itemstack);
		NBTTagCompound compound = new NBTTagCompound();
		nmsitemstack.save(compound);
		return SpigotNBTTagCompoundWrapper.wrap(compound);
	}

	public Integer getItemIdByName(String registryname) {
		Item item = Item.b(registryname);
		if (item != null) {
			return Item.getId(item);
		}
		return null;
	}

	public String getOutdatedServerMessage() {
		return SpigotConfig.outdatedServerMessage;
	}

	public boolean isBungeeEnabled() {
		return SpigotConfig.bungee;
	}

	public boolean isDebugging() {
		return SpigotPlatformUtils.getServer().isDebugging();
	}

	public void enableDebug() {
		SpigotPlatformUtils.getServer().getPropertyManager().setProperty("debug", Boolean.TRUE);
	}

	public void disableDebug() {
		SpigotPlatformUtils.getServer().getPropertyManager().setProperty("debug", Boolean.FALSE);
	}

	public int getCompressionThreshold() {
		return SpigotPlatformUtils.getServer().aG();
	}

	public KeyPair getEncryptionKeyPair() {
		return SpigotPlatformUtils.getServer().O();
	}

	public MinecraftSessionService getSessionService() {
		return SpigotPlatformUtils.getServer().az();
	}

	public <V> FutureTask<V> callSyncTask(Callable<V> call) {
		FutureTask<V> task = new FutureTask<>(call);
		SpigotPlatformUtils.getServer().processQueue.add(task);
		return task;
	}

	public String getModName() {
		return SpigotPlatformUtils.getServer().getServerModName();
	}

	public String getVersionName() {
		return SpigotPlatformUtils.getServer().getVersion();
	}

	public Statistic getStatisticByName(String value) {
		return CraftStatistic.getBukkitStatisticByName(value);
	}

	public String getStatisticName(Statistic stat) {
		return CraftStatistic.getNMSStatistic(stat).name;
	}

	public Achievement getAchievmentByName(String value) {
		return CraftStatistic.getBukkitAchievementByName(value);
	}

	public String getAchievmentName(Achievement achievement) {
		return CraftStatistic.getNMSAchievement(achievement).name;
	}

	public String convertBukkitIconToBase64(CachedServerIcon icon) {
		if (icon == null) {
			return null;
		}
		if (!(icon instanceof CraftIconCache)) {
			throw new IllegalArgumentException(icon + " was not created by " + CraftServer.class);
		}
		return ((CraftIconCache) icon).value;
	}

	public String getSoundNameById(int soundId) {
		return SoundEffect.a.b(SoundEffect.a.getId(soundId)).a();
	}

	public String getPotionEffectNameById(int id) {
		return MobEffectList.REGISTRY.b(MobEffectList.fromId(id)).toString();
	}

}
