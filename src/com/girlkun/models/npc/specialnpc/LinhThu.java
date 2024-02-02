package com.girlkun.models.npc.specialnpc;

import com.girlkun.models.item.Item;
import com.girlkun.services.func.ChangeMapService;
import com.girlkun.services.PetService;
import com.girlkun.models.player.Player;
import com.girlkun.utils.Util;
import com.girlkun.network.io.Message;
import com.girlkun.services.InventoryServiceNew;
import com.girlkun.services.ItemService;
import com.girlkun.services.Service;
import com.girlkun.services.func.CombineServiceNew;
import com.girlkun.utils.Logger;
import com.girlkun.models.item.Item;

public class LinhThu {

//    private static final long DEFAULT_TIME_DONE = 7776000000L;
    private static final long DEFAULT_TIME_DONE = 604800000L;

    private Player player;
    public long lastTimeCreate;
    public long timeDone;

    private final short id = 50;

    public LinhThu(Player player, long lastTimeCreate, long timeDone) {
        this.player = player;
        this.lastTimeCreate = lastTimeCreate;
        this.timeDone = timeDone;
    }

    public static void createLinhThuEgg(Player player) {
        player.linhthuegg = new LinhThu(player, System.currentTimeMillis(), DEFAULT_TIME_DONE);
    }

    public void sendLinhThuEgg() {
        Message msg;
        try {
//            Message msg = new Message(-117);
//            msg.writer().writeByte(100);
//            player.sendMessage(msg);
//            msg.cleanup();

            msg = new Message(-122);
            msg.writer().writeShort(this.id);
            msg.writer().writeByte(1);
            msg.writer().writeShort(15073);
            msg.writer().writeByte(0);
            msg.writer().writeInt(this.getSecondDone());
            this.player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(LinhThu.class, e);
        }
    }

    public int getSecondDone() {
        int seconds = (int) ((lastTimeCreate + timeDone - System.currentTimeMillis()) / 1000);
        return seconds > 0 ? seconds : 0;
    }

    public void openEgg(Player pl) {
        try {
            // Thread.sleep(4000);
            Item Hlt = InventoryServiceNew.gI().findItemBag(pl, 2029);
            Item Dns = InventoryServiceNew.gI().findItemBag(pl, 674);

            if (Hlt.quantity >= 99 && Dns.quantity >= 5 && InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {

                Item linhthu = ItemService.gI().createNewItem((short) (Util.nextInt(2019, 2026)));
                short[] icon = new short[2];
                icon[0] = linhthu.template.iconID;
                linhthu.itemOptions.add(new Item.ItemOption(50, Util.nextInt(5, 15)));
                linhthu.itemOptions.add(new Item.ItemOption(77, Util.nextInt(5, 15)));
                linhthu.itemOptions.add(new Item.ItemOption(103, Util.nextInt(4, 15)));
                linhthu.itemOptions.add(new Item.ItemOption(108, Util.nextInt(2, 6)));
                linhthu.itemOptions.add(new Item.ItemOption(101, Util.nextInt(5, 15)));
                InventoryServiceNew.gI().addItemBag(player, linhthu);
                InventoryServiceNew.gI().subQuantityItemsBag(player, Hlt, 99);
                InventoryServiceNew.gI().subQuantityItemsBag(player, Dns, 5);
                InventoryServiceNew.gI().sendItemBags(player);
                CombineServiceNew.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
                Service.getInstance().sendThongBao(player, "Bạn vừa nhận được linh thú");
                ChangeMapService.gI().changeMapInYard(this.player, this.player.gender * 7, -1, Util.nextInt(300, 500));
                destroyEgg();
            } else {
                Service.getInstance().sendThongBao(player, "không đủ vật phẩm!");
                return;
            }
        } catch (Exception e) {
            Service.getInstance().sendThongBao(player, "không đủ vật phẩm!");
        }

    }

    public void destroyEgg() {
        try {
            Message msg = new Message(-117);
            msg.writer().writeByte(101);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
        this.player.linhthuegg = null;
    }

    public void subTimeDone(int d, int h, int m, int s) {
        this.timeDone -= ((d * 24 * 60 * 60 * 1000) + (h * 60 * 60 * 1000) + (m * 60 * 1000) + (s * 1000));
        this.sendLinhThuEgg();
    }

    public void dispose() {
        this.player = null;
    }
}
