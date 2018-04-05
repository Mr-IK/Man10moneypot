package red.man10;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class Man10moneypot extends JavaPlugin {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)){
			if(args.length == 1) {
				if(args[0].equalsIgnoreCase("reload")) {
			        getServer().getPluginManager().disablePlugin(this);
			        getServer().getPluginManager().enablePlugin(this);
			        getLogger().info(prefix+"§a設定を再読み込みしました。");
			        return true;
				}
				 getLogger().info(prefix+ChatColor.RED+"mmp reload");
			return true;
			}
		}
		Player p = (Player)sender;
		  if(args.length == 0) {
     			  p.sendMessage("=============="+prefix+"==============");
		     	  p.sendMessage(prefix+"§e /mmp new 金額 人数 …$[金額]の[人数]人で金壺ゲームを始めます");
		     	  p.sendMessage(prefix+"§e /mmp bet …$[金額]を金壺に入れてゲームに参加します");
		     	  p.sendMessage(prefix+"§e /mmp cancel …$[金額]を金壺から取り出しゲームから抜けます");
		    	  p.sendMessage("");
		      if(onebet==0) {
		      	  p.sendMessage(prefix+"§c今金壺の中身は空っぽです！");
		      }else {
		    	  p.sendMessage(prefix+"§6§l金壺が§e§l$"+onebet+"§6§l欲しそうにこちらを見ている！");
		  	     if(playerState.isEmpty()==true) {
		    	    p.sendMessage(prefix+"§c幸か不幸かまだ誰もお金を入れてないようだ…");
			     }else {
		    	    p.sendMessage(prefix+"§e§l"+playerState.size()+"§6§l人がもうすでにお金を入れている…§e§l合計"+jpnBalForm(totalbet)+"円§6§lのようだ。");
			  }
		   }
		   p.sendMessage("");
		   p.sendMessage("=============="+prefix+"==============");
		   return true;
		  }else if(args.length == 1) {
				if(args[0].equalsIgnoreCase("bet")) {
			        if(!p.hasPermission("Man10moneypot.bet")){
			            p.sendMessage(prefix + "§cあなたにはBETする権限がありません！");
			            return true;
			        }
			        if(onebet==0) {
			        	p.sendMessage(prefix+"§c現在ゲーム中ではありません！");
			        	return true;
			        }
			        if(playerState.containsKey(p.getUniqueId())) {
			        	p.sendMessage(prefix+"§cもうBETしています");
			        	return true;
			        }
			        if(playerState.size()==maxplayer){
			        	p.sendMessage(prefix+"§cもう定員です");
			        	return true;
			        }
			        if(Vault.economy.getBalance(p)<onebet) {
			        	p.sendMessage(prefix + "§cお金が足りません！");
                        return true;
			        }
			        Vault.economy.withdrawPlayer(p, onebet);
			        p.sendMessage("§e$"+onebet+"支払いました");
			        Bukkit.broadcastMessage(prefix+"§c"+p.getName()+"はBETしました!");
			        playerState.put(p.getUniqueId(), playerState.size()+1);
			        totalbet = totalbet+onebet;
			        if(playerState.size()==maxplayer){
                	 Random rnd = new Random();
                	 int random = rnd.nextInt(playerState.size()+1);
                	 for(UUID uuid:playerState.keySet()) {
                		 if(playerState.get(uuid)==random) {
                			 String name = Bukkit.getPlayer(uuid).getName();
                			 Bukkit.broadcastMessage(prefix+"§6§l"+name+"§e§lの前で§6§l"+jpnBalForm(totalbet)+"円§e§lを排出した！");
                			 Vault.economy.depositPlayer(Bukkit.getPlayer(uuid),totalbet);
                			 Bukkit.getPlayer(uuid).sendMessage("§e$"+totalbet+"入金しました");
                        	 playerState.clear();
                         	 totalbet = 0;
                         	 onebet = 0;
                         	 return true;
                		 }
                	 }
 			         Player player = getRandomPlayer();
 			         if(player!=null) {
                	 Bukkit.broadcastMessage(prefix+"§6§l金壺の§e§l"+jpnBalForm(totalbet)+"円§6§lは偶然にも§e§l"+player.getName()+"§6§lが手に入れた！");
        			 Vault.economy.depositPlayer(player,totalbet);
        			 player.sendMessage("§e$"+totalbet+"入金しました");
 			         }else {
 			         Bukkit.broadcastMessage(prefix+"§6§l金壺の§e§l"+jpnBalForm(totalbet)+"円§6§lは残念なことに§2§l奈落§6§lに落ちていった!§f§lおーまいがっ!");
 			         }
                	 playerState.clear();
                 	 totalbet = 0;
                 	 onebet = 0;
                 	 maxplayer = 0;
			        }
			        return true;
				}else if(args[0].equalsIgnoreCase("cancel")) {
			        if(!p.hasPermission("Man10moneypot.cancel")){
			            p.sendMessage(prefix + "§cあなたにはBETをキャンセルする権限がありません！");
			            return true;
			        }
			        if(onebet==0) {
			        	p.sendMessage(prefix+"§c現在ゲーム中ではありません！");
			        	return true;
			        }
			        if(!playerState.containsKey(p.getUniqueId())) {
			        	p.sendMessage(prefix+"§cまだBETしていません");
			        	return true;
			        }
		    		 Bukkit.broadcastMessage(prefix+"§c§l"+p.getName()+"はキャンセルしたためBETリストから外された");
		    		 Vault.economy.depositPlayer(p,onebet);
		    		 p.sendMessage("§e$"+onebet+"入金しました");
		    		 playerState.remove(p.getUniqueId());
		    		 totalbet = totalbet-onebet;
		    		 if(playerState.isEmpty()==true) {
		    			 Bukkit.broadcastMessage(prefix+"§4§l参加者が0人になったためゲームはキャンセルされた");
					        totalbet = 0;
					        onebet = 0;
					        maxplayer = 0;
					        return true;
		    		 }
				}else if(args[0].equalsIgnoreCase("gcancel")) {
			        if(!p.hasPermission("Man10moneypot.gcancel")){
			            p.sendMessage(prefix + "§cあなたにはゲームをキャンセルする権限がありません！");
			            return true;
			        }
			        if(onebet==0) {
			        	p.sendMessage(prefix+"§c現在ゲーム中ではありません！");
			        	return true;
			        }
			        Bukkit.broadcastMessage(prefix+"§c§lゲームがキャンセルされました");
			        for(UUID uuid:playerState.keySet()) {
			        	Player pp = Bukkit.getPlayer(uuid);
			        	Vault.economy.depositPlayer(pp,onebet);
			    		 pp.sendMessage("§e$"+onebet+"入金しました");
			    		 playerState.remove(pp.getUniqueId());
			        }
			        totalbet = 0;
			        onebet = 0;
			        maxplayer = 0;
				}
		  }else if(args.length == 3) {
				if(args[0].equalsIgnoreCase("new")) {
			        if(!p.hasPermission("Man10moneypot.new")){
			            p.sendMessage(prefix + "§cあなたにはゲームを始める権限がありません！");
			            return true;
			        }
			        if(onebet!=0) {
			        	p.sendMessage(prefix+"§c現在ゲーム中です！");
			        	return true;
			        }
			        int i = 0;
		            try {
			            i = Integer.parseInt(args[1]);
				    }catch (NumberFormatException f){
						   p.sendMessage(prefix+"§c数字で入力してください。");
						   return true;
					}
		            if (i % 10000 == 0){
		            	p.sendMessage(prefix+"§cBET金額は10000の倍数で入力してください。");
		            	return true;
		            }
			        int ii = 0;
		            try {
			            ii = Integer.parseInt(args[2]);
				    }catch (NumberFormatException f){
						   p.sendMessage(prefix+"§c数字で入力してください。");
						   return true;
					}
		            if(ii<=1) {
		            	p.sendMessage(prefix+"§c最大人数は2以上の数字で入力してください。");
		            	return true;
		            }
			        if(Vault.economy.getBalance(p)<i) {
			        	p.sendMessage(prefix + "§cお金が足りません！");
                        return true;
			        }
			        maxplayer = ii;
			        onebet = i;
			        totalbet = i;
			        playerState.put(p.getUniqueId(), 1);
			        Vault.economy.withdrawPlayer(p, i);
			        p.sendMessage("§e$"+i+"支払いました");
			        Bukkit.broadcastMessage(prefix+"§e§l"+p.getName()+"§6§lの金壺が§e§l"+jpnBalForm(onebet)+"円§6§lを§e§l"+ii+"§6§l人分欲しがっています！=>§f§l/mmp bet");
			        return true;
				}
		  }
		return true;
	}

	@Override
	public void onDisable() {
		// TODO 自動生成されたメソッド・スタブ
		super.onDisable();
	}
	public static FileConfiguration config1;
	private HashMap<UUID,Integer> playerState;
	int onebet = 0;
	int totalbet = 0;
	int maxplayer = 0;
	String prefix = "§f§l[§d§lm§f§la§a§ln10§6§l金壺§f§l]§r";
	@Override
	public void onEnable() {
		playerState = new HashMap<>();
		new Vault(this);
		new logout(this);
		getCommand("man10moneypot").setExecutor(this);
		getCommand("mmp").setExecutor(this);
		saveDefaultConfig();
	    FileConfiguration config = getConfig();
        config1 = config;
		super.onEnable();
	}
    String jpnBalForm(int val){
        long val2 = (long) val;

        String addition = "";
        String form = "万";
        long man = val2/10000;
        if(val >= 100000000){
            man = val2/100000000;
            form = "億";
            long mann = (val2 - man * 100000000) / 10000;
            addition = mann + "万";
        }
        return man + form + addition;
    }
    public Player getRandomPlayer(){
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        List<Player> newList = new ArrayList<>();
        for (Player player : players){
        	if(!playerState.containsKey(player.getUniqueId())) {
            newList.add(player);
        	}
        }
        if (newList.size() > 0){
            int size = newList.size() - 1;
            int random = new Random().nextInt(size);
            return newList.get(random);
        }
        return null;
    }
    public class logout implements Listener{
     public logout(Man10moneypot plugin) {
         plugin.getServer().getPluginManager().registerEvents(this, plugin);
     }
     @EventHandler
     public void onJoin(PlayerQuitEvent event) {
    	 UUID uuid = event.getPlayer().getUniqueId();
    	 if(playerState.containsKey(uuid)) {
    		 Bukkit.broadcastMessage(prefix+"§c§l"+event.getPlayer().getName()+"はログアウトしたためBETリストから外された");
    		 Vault.economy.withdrawPlayer(event.getPlayer(),onebet);
    		 playerState.remove(uuid);
    		 totalbet = totalbet-onebet;
    	 }
    	 return;
     }
    }
}
