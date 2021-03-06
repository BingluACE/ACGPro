package shrbox.github.acgpro;

import net.mamoe.mirai.console.command.BlockingCommand;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.command.JCommandManager;
import net.mamoe.mirai.console.plugins.Config;
import net.mamoe.mirai.console.plugins.PluginBase;
import net.mamoe.mirai.message.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

class Main extends PluginBase {
    public static Config config;
    public static boolean ispulling = false;
    public static int version = 20200929;
    short count = 0;
    public void load_Config() {
        config = loadConfig("config.yml");
        config.setIfAbsent("apikey", "");
        config.setIfAbsent("r18", false);
        List<Long> r18_groups = new ArrayList<>();
        Collections.addAll(r18_groups, 1145141919L, 123123123L);
        config.setIfAbsent("r18-groups", r18_groups);
        config.setIfAbsent("limit-mode", false);
        config.save();
        r18_groups.clear();
    }
    public void onEnable() {
        load_Config();
        JCommandManager.getInstance().register(this, new BlockingCommand(
                "acgreload", new ArrayList<>(), "重载ACGPro配置文件", "/acghreload"
        ) {
            @Override
            public boolean onCommandBlocking(@NotNull CommandSender commandSender, @NotNull List<String> list) {
                load_Config();
                commandSender.sendMessageBlocking("重载成功");
                return true;
            }
        });
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                count = 0;
            }
        }, 60 * 1000, 60 * 1000);
        System.setProperty("http.agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E; QQBrowser/7.0.3698.400)");
        getEventListener().subscribeAlways(GroupMessageEvent.class, (GroupMessageEvent e) -> {
            if (e.getMessage().contentToString().toLowerCase().contains("acg")) {
                if (config.getBoolean("limit-mode") && Main.ispulling) {
                    e.getGroup().sendMessage("[ACGPro] 正在下载图片，请稍后再试");
                    return;
                }
                if (count > 15) {
                    e.getGroup().sendMessage(MessageUtils.newChain(new At(e.getSender())).plus("[ACGPro] 请先喝口水再尝试"));
                    return;
                }
                count++;
                new Thread().boot(e);
            }
        });
    }
}