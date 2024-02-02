package com.girlkun.models.npc;

import com.girlkun.consts.ConstMap;
import com.girlkun.models.boss.list_boss.nappa.Kuku;
import com.girlkun.server.ServerManager;
import com.girlkun.server.io.MySession;
import com.girlkun.services.*;
import com.girlkun.consts.ConstNpc;
import com.girlkun.consts.ConstPlayer;
import com.girlkun.consts.ConstTask;
import com.girlkun.jdbc.daos.PlayerDAO;
import com.girlkun.models.boss.Boss;
import com.girlkun.models.boss.BossData;
import com.girlkun.models.boss.BossID;
import com.girlkun.models.boss.BossManager;
import com.girlkun.models.boss.list_boss.NhanBan;
import com.girlkun.models.clan.Clan;
import com.girlkun.models.clan.ClanMember;
import server.io.LogHistory;
import java.util.HashMap;
import java.util.List;

import com.girlkun.services.func.ChangeMapService;
import com.girlkun.services.func.SummonDragon;

import static com.girlkun.services.func.SummonDragon.SHENRON_1_STAR_WISHES_1;
import static com.girlkun.services.func.SummonDragon.SHENRON_1_STAR_WISHES_2;
import static com.girlkun.services.func.SummonDragon.SHENRON_SAY;

import com.girlkun.models.player.Player;
import com.girlkun.models.item.Item;
import com.girlkun.models.map.Map;
import com.girlkun.models.map.Zone;
import com.girlkun.models.map.blackball.BlackBallWar;
import com.girlkun.models.map.MapMaBu.MapMaBu;
import com.girlkun.models.map.doanhtrai.DoanhTrai;
import com.girlkun.models.map.doanhtrai.DoanhTraiService;
import com.girlkun.models.player.Inventory;
import com.girlkun.models.player.NPoint;
import com.girlkun.models.matches.PVPService;
import com.girlkun.models.shop.ShopServiceNew;
import com.girlkun.models.skill.Skill;
import com.girlkun.server.Client;
import com.girlkun.server.Maintenance;
import com.girlkun.server.Manager;
import com.girlkun.services.func.ChonAiDay;
import com.girlkun.services.func.CombineServiceNew;
import com.girlkun.services.func.Input;
import com.girlkun.services.func.LuckyRound;
import com.girlkun.services.func.TopService;
import com.girlkun.utils.Logger;
import com.girlkun.utils.TimeUtil;
import com.girlkun.utils.Util;
import java.util.ArrayList;
import java.util.Random;

public class NpcFactory {

    private static final int COST_HD = 50000000;

    private static boolean nhanVang = false;
    private static boolean nhanDeTu = false;


    //playerid - object
    public static final java.util.Map<Long, Object> PLAYERID_OBJECT = new HashMap<Long, Object>();

    private NpcFactory() {

    }

    private static Npc poTaGe(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 140) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Đa vũ trụ song song \b|7|Con muốn gọi con trong đa vũ trụ \b|1|Với giá 200tr vàng không?", "Gọi Boss\nNhân bản", "Từ chối");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 140) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0: {
                                    Boss oldBossClone = BossManager.gI().getBossById(Util.createIdBossClone((int) player.id));
                                    if (oldBossClone != null) {
                                        this.npcChat(player, "Nhà ngươi hãy tiêu diệt Boss lúc trước gọi ra đã, con boss đó đang ở khu " + oldBossClone.zone.zoneId);
                                    } else if (player.inventory.gold < 200_000_000) {
                                        this.npcChat(player, "Nhà ngươi không đủ 200 Triệu vàng ");
                                    } else if (player.maxpvpclone >= 5) {
                                        this.npcChat(player, "đã đủ 5 lần trong hôm nay");

                                    } else if (!player.getSession().actived) {
                                        this.npcChat(player, "|5|VUI LÒNG KÍCH HOẠT TÀI KHOẢN \n|7|tại npc trong nhà\n|5|ĐỂ MỞ KHÓA TÍNH NĂNG");
                                    } else {
                                        player.maxpvpclone++;
                                        List<Skill> skillList = new ArrayList<>();
                                        for (byte i = 0; i < player.playerSkill.skills.size(); i++) {
                                            Skill skill = player.playerSkill.skills.get(i);
                                            if (skill.point > 0) {
                                                skillList.add(skill);
                                            }
                                        }
                                        int[][] skillTemp = new int[skillList.size()][3];
                                        for (byte i = 0; i < skillList.size(); i++) {
                                            Skill skill = skillList.get(i);
                                            if (skill.point > 0) {
                                                skillTemp[i][0] = skill.template.id;
                                                skillTemp[i][1] = skill.point;
                                                skillTemp[i][2] = skill.coolDown;
                                            }
                                        }

                                        BossData bossDataClone = new BossData(
                                                "Boss Clone " + player.name,
                                                player.gender,
                                                new short[]{player.getHead(), player.getBody(), player.getLeg(), player.getFlagBag(), player.getAura(), player.getEffFront()},
                                                player.nPoint.dame,
                                                new int[]{player.nPoint.hpMax},
                                                new int[]{140},
                                                skillTemp,
                                                new String[]{"|-2|Boss nhân bản đã xuất hiện rồi"}, //text chat 1
                                                new String[]{"|-1|Ta sẽ chiếm lấy thân xác của ngươi hahaha!"}, //text chat 2
                                                new String[]{"|-1|Lần khác ta sẽ xử đẹp ngươi"}, //text chat 3
                                                60
                                        );

                                        try {
                                            new NhanBan(Util.createIdBossClone((int) player.id), bossDataClone, player.zone);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        //trừ vàng khi gọi boss
                                        player.inventory.gold -= 200_000_000;
                                        Service.getInstance().sendMoney(player);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        };
    }

    private static Npc quyLaoKame(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                        if (player.getSession().is_gift_box) {
                            this.createOtherMenu(player, ConstNpc.BASE_MENU, "Chào con, con muốn ta giúp gì nào?", "Top\nSức mạnh", "Giải tán bang hội");
                        } else {
                            this.createOtherMenu(player, ConstNpc.BASE_MENU, "Chào con, con muốn ta giúp gì nào?", "Top\nSức mạnh", "Giải tán bang hội");
                        }
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0:
                                Util.showListTop(player, (byte) 0);
                                break;
                            case 1:
                                Clan clan = player.clan;
                                if (clan != null) {
                                    ClanMember cm = clan.getClanMember((int) player.id);
                                    if (cm != null) {
                                        if (clan.members.size() > 1) {
                                            Service.getInstance().sendThongBao(player, "Bang phải còn một người");
                                            break;
                                        }
                                        if (!clan.isLeader(player)) {
                                            Service.getInstance().sendThongBao(player, "Phải là bảng chủ");
                                            break;
                                        }
//
                                        NpcService.gI().createMenuConMeo(player, ConstNpc.CONFIRM_DISSOLUTION_CLAN, -1, "Con có chắc chắn muốn giải tán bang hội không? Ta cho con 2 lựa chọn...",
                                                "Yes you do!", "Từ chối!");
                                    }
                                    break;
                                }
                                Service.getInstance().sendThongBao(player, "Có bang hội đâu ba!!!");
                                break;
                            case 2:
                                if (player.getSession().is_gift_box) {
                                    if (PlayerDAO.setIs_gift_box(player)) {
                                        player.getSession().is_gift_box = false;
                                        player.inventory.coupon += 5;
                                        Service.getInstance().sendThongBao(player, "Bạn vừa nhận được 5 điểm Coupon");
                                        Service.getInstance().sendMoney(player);
                                    }
                                }
                                break;
                        }
                    }
                }
            }
        };
    }

    public static Npc duongtang(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    switch (this.mapId) {
                        case 0:
                            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                    "A mi phò phò, thí chủ hãy giúp giải cứu đồ đệ của bần tăng tại ngũ hành sơn",
                                    "Đồng ý", "Từ chối", "Nhận thưởng", "Hộ tống\nThỉnh kinh");
                            break;
                        case 122:
                            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                    "A mi phò phò, thí chủ hãy giúp thu thập bùa'giải phong ấn' mỗi chữ 99 cái",
                                    "Giải\nPhong ấn", "Về Làng Aru", "Top Hoa quả");
                            break;
                        case 123:
                            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                    "A mi phò phò....",
                                    "Làng Aru");
                            break;
                    }

                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    switch (this.mapId) {
                        case 0:
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMapInYard(player, 123, -1, -1);
                                    break;
                                case 2:
                                    ShopServiceNew.gI().opendShop(player, "NGOKHONG_SHOP", true);
                                    break;
                                case 3:

                                    if (player.petevent == null) {
                                        //      player.isNewPet = false;
                                        if (player.newpet != null) {
                                            player.newpet.dispose();
                                            player.newpet = null;
                                        }
                                        PetService.Pet3(player, 467, 468, 469);
                                        ChangeMapService.gI().changeMapInYard(player, 0, -1, player.location.x);
                                        Service.getInstance().point(player);
                                        Service.getInstance().sendThongBao(player, "Mau hộ tống Đường Tăng đến Đông Karin");
                                    } else {
                                        Service.getInstance().chat(player.petevent, "mau đưa ta tới Đông Karin");
                                    }
                                    break;
                            }
                            break;
                        case 122:
                            switch (select) {
                                case 0:
                                try {
                                    Item[] items = new Item[4];
                                    items[0] = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 537);
                                    items[1] = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 538);
                                    items[2] = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 539);
                                    items[3] = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 540);
                                    for (Item item : items) {
                                        if (item != null && item.quantity >= 99) {
                                            Item caitrang = ItemService.gI().createNewItem((short) (player.gender + 544));
                                            caitrang.itemOptions.add(new Item.ItemOption(93, Util.nextInt(2, 7)));
                                            caitrang.itemOptions.add(new Item.ItemOption(50, Util.nextInt(16, 23)));
                                            caitrang.itemOptions.add(new Item.ItemOption(94, Util.nextInt(5, 22)));
                                            caitrang.itemOptions.add(new Item.ItemOption(103, Util.nextInt(30, 55)));
                                            caitrang.itemOptions.add(new Item.ItemOption(77, Util.nextInt(50, 95)));
                                            caitrang.itemOptions.add(new Item.ItemOption(101, 54));
                                            caitrang.itemOptions.add(new Item.ItemOption(100, Util.nextInt(50, 88)));
                                            caitrang.itemOptions.add(new Item.ItemOption(114, Util.nextInt(50, 70)));
                                            caitrang.itemOptions.add(new Item.ItemOption(106, 0));
                                            InventoryServiceNew.gI().addItemBag(player, caitrang);
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, item, 99);
                                            InventoryServiceNew.gI().sendItemBags(player);
                                            this.npcChat(player, "Ami phò phò, đa tạ thí chủ tương trợ, xin hãy món quà này, bần tăng sẽ niệm chú giải thoát cho Ngộ Không");
                                            Thread.sleep(3000);
                                            ChangeMapService.gI().changeMapInYard(player, 0, -1, -1);
                                            return;
                                        } else {
                                            Service.getInstance().sendThongBao(player, "Không đủ x99 4 chữ");
                                        }
                                    }

                                } catch (Exception ex) {
                                    Service.getInstance().sendThongBao(player, "Không đủ x99 4 chữ");
                                }

                                break;
                                case 1:
                                    ChangeMapService.gI().changeMapInYard(player, 0, -1, -1);
                                    break;
                                case 2:
                                    Util.showListTop(player, (byte) 2);
                                    break;

                            }
                            break;
                        case 123:
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 0, -1, -1);
                                    break;

                            }
                            break;
                    }
                }
            }
        };
    }

    public static Npc ngokhong(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    switch (this.mapId) {
                        case 122:
                            Item caitrang = InventoryServiceNew.gI().findItemBody(player, player.gender + 544);
                            int soLuong = 0;
                            if (caitrang != null) {
                                soLuong = caitrang.quantity;
                            }
                            if (soLuong >= 1) {
                                this.createOtherMenu(player, ConstNpc.MENU_NHAN_DE_TU_NGO_KHONG,
                                        "Đừng hòng ta sẽ là đệ tử của ngươi.",
                                        "Ami\nphò phò", "Trao vòng\nKim cô", "Tặng quả\nHồng đào");
                            } else {
                                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                        "Chu mi nga",
                                        "Tặng qủa\nHồng đào", "Tặng quả\nHồng đào\nChín");
                            }
                            break;
                    }

                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    switch (this.mapId) {
                        case 122:
                            if (player.iDMark.isBaseMenu()) {
                                switch (select) {
                                    case 0:
                                        try {
                                            Item hongdao = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 541);
                                            int soLuong = 0;
                                            int[] itemDos = new int[]{537, 538, 539, 540};
                                            int randomDo = new Random().nextInt(itemDos.length);
                                            if (hongdao != null) {
                                                soLuong = hongdao.quantity;
                                            }
                                            if (soLuong >= 1 && InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
                                                InventoryServiceNew.gI().subQuantityItemsBag(player, hongdao, 1);
                                                Service.getInstance().dropItemMap(player.zone, Util.ratiItem(player.zone, itemDos[randomDo], 1, player.location.x + Util.nextInt(20, 50), player.location.y, player.id));
                                                player.inventory.event++;
                                            } else {
                                                this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                                        "Không đủ vật phẩm",
                                                        "Đóng");
                                            }

                                        } catch (Exception ex) {
                                            this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                                    "Không đủ vật phẩm",
                                                    "Đóng");
                                        }
                                        break;

                                }
                                break;
                            } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_NHAN_DE_TU_NGO_KHONG) {
                                switch (select) {
                                    case 0:
                                        if (player.pet.typePet != 2) {
                                            this.npcChat(player, "Không có tác dụng đâu tên ngốc");
                                        }
                                        break;
                                    case 1:
                                        if (player.pet != null) {
                                            Item[] items = new Item[2];
                                            items[0] = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 543);
                                            items[1] = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 457);
                                            for (Item item : items) {
                                                if (item != null && item.quantity >= 1 && player.inventory.ruby >= 30) {
                                                    if (Util.isTrue(1, 100)) {
                                                        PetService.gI().changeNgoKhong(player);
                                                        InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
                                                        player.inventory.ruby -= 30;
                                                    } else {
                                                        player.inventory.ruby -= 30;
                                                        InventoryServiceNew.gI().subQuantityItemsBag(player, items[1], 1);
                                                        Service.getInstance().sendThongBao(player, "Chết tiệt, trượt rồi, làm lại xem sao.");
                                                    }
                                                    InventoryServiceNew.gI().sendItemBags(player);
                                                    Service.getInstance().sendMoney(player);
                                                } else {
                                                    Service.getInstance().sendThongBao(player, "Không đủ 1 thỏi vàng, 1 vòng kim cô và 30 hồng ngọc");
                                                }
                                                return;
                                            }
                                        } else {
                                            Service.getInstance().sendThongBao(player, "yêu cầu có đệ tử");
                                        }

                                        break;
                                    case 2:
                                        Item hongdao = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 541);
                                        int soLuong = 0;
                                        if (hongdao != null) {
                                            soLuong = hongdao.quantity;
                                        }

                                        if (player.pet != null && player.pet.typePet == 2) {
                                            int tnup = player.nPoint.dame / 10;
                                            if (soLuong >= 1 && InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
                                                player.pet.nPoint.power += tnup;
                                                player.pet.nPoint.tiemNang += tnup;
                                                InventoryServiceNew.gI().subQuantityItemsBag(player, hongdao, 1);
                                                Service.getInstance().sendThongBao(player, "Đệ tử NgộKhông đã nhận được " + Util.formatNumber(tnup) + " TNSM");
                                            }
                                        }
                                        break;

                                }
                            }

                    }
                }
            }
        };
    }

    public static Npc truongLaoGuru(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                        super.openBaseMenu(player);
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {

                }
            }
        };
    }

    public static Npc vuaVegeta(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                        super.openBaseMenu(player);
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {

                }
            }
        };
    }

    public static Npc ongGohan_ongMoori_ongParagus(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Con cố gắng theo %1 học thành tài, đừng lo lắng cho ta.\n"
                                        .replaceAll("%1", player.gender == ConstPlayer.TRAI_DAT ? "Quy lão Kamê"
                                                : player.gender == ConstPlayer.NAMEC ? "Trưởng lão Guru" : "Vua Vegeta") + "Ta đang giữ tiền tiết kiệm của con\n hiện tại con đang có: " + Util.formatNumber(player.getSession().balance) + " VNĐ\nNạp thẻ tại https://nrovn.xyz",
                                "Nhận 200k ngọc xanh", "Nhận đệ tử", "Mua vàng","Mua hồng\nngọc");

                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0:
                                if (player.inventory.gem == 200000) {
                                    this.npcChat(player, "vui lòng xài hết");
                                    break;
                                }
                                player.inventory.gem = 200000;
                                Service.getInstance().sendMoney(player);
                                Service.getInstance().sendThongBao(player, "Bạn vừa nhận được 200K ngọc xanh");
                                break;
                            case 1:
                                if (player.pet == null) {
                                    PetService.gI().createNormalPet(player);
                                    Service.getInstance().sendThongBao(player, "Bạn vừa nhận được đệ tử");
                                } else {
                                    this.npcChat(player, "vui lòng xài hết");
                                }
                                break;
                            case 2:
                                this.createOtherMenu(player, ConstNpc.BUY_VANG,
                                        "Ta đang giữ tiền tiết kiệm của con\n hiện tại con đang có: " + Util.formatNumber(player.getSession().balance) + " VNĐ\n Nếu con mua vàng lần đầu thì tài khoản của con sẽ được kích hoạt", "1,000 VNĐ đổi 1 thỏi", "10,000 VNĐ đổi 15 thỏi", "20,000 VNĐ đổi 30 thỏi", "30,000 VNĐ đổi 50 thỏi", "50,000 VNĐ đổi 80 thỏi", "100,000 VNĐ đổi 200 thỏi",
                                        "Đóng");
                                break;
                            case 3:
                                this.createOtherMenu(player, ConstNpc.BUY_HONHG_NGOC,
                                        "Ta đang giữ tiền tiết kiệm của con\n hiện tại con đang có: " + Util.formatNumber(player.getSession().balance) + " VNĐ\n hồng ngọc cũng có thể kiếm bằng cách fram quái", "1,000 VNĐ đổi 1000 HN", "10,000 VNĐ đổi 15,000 HN", "20,000 VNĐ đổi 30,000 HN", "30,000 VNĐ đổi 50,000 HN", "50,000 VNĐ đổi 80,000 HN", "100,000 VNĐ đổi 130,000 HN",
                                        "Đóng");
                                break;
                            case 4:
                                if (player.inventory.ruby == 200000) {
                                    this.npcChat(player, "Bú ít thôi con");
                                    break;
                                }
                                player.inventory.ruby += 10000;
                                Service.getInstance().sendMoney(player);
                                Service.getInstance().sendThongBao(player, "Bạn vừa nhận được 10k ruby");
                                break;
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.BUY_VANG) {
                        Item thoivang = ItemService.gI().createNewItem((short) (457));
                        int sodu1 = 1000 - player.getSession().balance;
                        int sodu10 = 10000 - player.getSession().balance;
                        int sodu20 = 20000 - player.getSession().balance;
                        int sodu30 = 30000 - player.getSession().balance;
                        int sodu50 = 50000 - player.getSession().balance;
                        int sodu100 = 100000 - player.getSession().balance;
                        switch (select) {
                            case 0:
                                if (player.getSession().balance >= 1000) {
                                    player.getSession().actived = true;
                                    thoivang.quantity = 1;
                                    InventoryServiceNew.gI().addItemBag(player, thoivang);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    PlayerDAO.subVnd(player, 1000);
                                    Service.getInstance().sendThongBao(player, "bạn vừa nhận được 1 " + thoivang.template.name);
                                } else {
                                    Service.getInstance().sendThongBao(player, "Bạn còn thiếu " + sodu1 + " VNĐ");
                                }
                                break;
                            case 1:
                                if (player.getSession().balance >= 10000) {
                                    player.getSession().actived = true;
                                    thoivang.quantity = 15;
                                    InventoryServiceNew.gI().addItemBag(player, thoivang);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    PlayerDAO.subVnd(player, 10000);
                                    Service.getInstance().sendThongBao(player, "bạn vừa nhận được 15 " + thoivang.template.name);
                                } else {
                                    Service.getInstance().sendThongBao(player, "Bạn còn thiếu " + sodu1 + " VNĐ");
                                }
                                break;
                            case 2:
                                if (player.getSession().balance >= 20000) {
                                    player.getSession().actived = true;
                                    thoivang.quantity = 30;
                                    InventoryServiceNew.gI().addItemBag(player, thoivang);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    PlayerDAO.subVnd(player, 20000);
                                    Service.getInstance().sendThongBao(player, "bạn vừa nhận được 30 " + thoivang.template.name);
                                } else {
                                    Service.getInstance().sendThongBao(player, "Bạn còn thiếu " + sodu1 + " VNĐ");
                                }
                                break;
                            case 3:
                                if (player.getSession().balance >= 30000) {
                                    player.getSession().actived = true;
                                    thoivang.quantity = 50;
                                    InventoryServiceNew.gI().addItemBag(player, thoivang);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    PlayerDAO.subVnd(player, 30000);
                                    Service.getInstance().sendThongBao(player, "bạn vừa nhận được 50 " + thoivang.template.name);
                                } else {
                                    Service.getInstance().sendThongBao(player, "Bạn còn thiếu " + sodu1 + " VNĐ");
                                }
                                break;
                            case 4:
                                if (player.getSession().balance >= 50000) {
                                    player.getSession().actived = true;
                                    thoivang.quantity = 80;
                                    InventoryServiceNew.gI().addItemBag(player, thoivang);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    PlayerDAO.subVnd(player, 50000);
                                    Service.getInstance().sendThongBao(player, "bạn vừa nhận được 80 " + thoivang.template.name);
                                } else {
                                    Service.getInstance().sendThongBao(player, "Bạn còn thiếu " + sodu1 + " VNĐ");
                                }
                                break;
                            case 5:
                                if (player.getSession().balance >= 100000) {
                                    player.getSession().actived = true;
                                    thoivang.quantity = 200;
                                    player.inventory.coupon+= 2000;
                                    InventoryServiceNew.gI().addItemBag(player, thoivang);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    PlayerDAO.subVnd(player, 100000);
                                    Service.getInstance().sendThongBao(player, "bạn vừa nhận được 200 " + thoivang.template.name);
                                } else {
                                    Service.getInstance().sendThongBao(player, "Bạn còn thiếu " + sodu1 + " VNĐ");
                                }
                                break;
                        }
                    }else if (player.iDMark.getIndexMenu() == ConstNpc.BUY_HONHG_NGOC) {
                        //        Item thoivang = ItemService.gI().createNewItem((short) (457));
                        int sodu1 = 1000 - player.getSession().balance;
                        int sodu10 = 10000 - player.getSession().balance;
                        int sodu20 = 20000 - player.getSession().balance;
                        int sodu30 = 30000 - player.getSession().balance;
                        int sodu50 = 50000 - player.getSession().balance;
                        int sodu100 = 100000 - player.getSession().balance;
                        switch (select) {
                            case 0:
                                if (player.getSession().balance >= 1000) {
                                    player.inventory.ruby += 1000;
                                    Service.getInstance().sendMoney(player);
                                    PlayerDAO.subVnd(player, 1000);
                                    Service.getInstance().sendThongBao(player, "Xong");
                                } else {
                                    Service.getInstance().sendThongBao(player, "Bạn còn thiếu " + sodu1 + " VNĐ");
                                }
                                break;
                            case 1:
                                if (player.getSession().balance >= 10000) {
                                    player.inventory.ruby += 15000;
                                    Service.getInstance().sendMoney(player);
                                    PlayerDAO.subVnd(player, 10000);
                                    Service.getInstance().sendThongBao(player, "Xong");
                                } else {
                                    Service.getInstance().sendThongBao(player, "Bạn còn thiếu " + sodu1 + " VNĐ");
                                }
                                break;
                            case 2:
                                if (player.getSession().balance >= 20000) {
                                    player.inventory.ruby += 30000;
                                    Service.getInstance().sendMoney(player);
                                    PlayerDAO.subVnd(player, 20000);
                                    Service.getInstance().sendThongBao(player, "Xong");
                                } else {
                                    Service.getInstance().sendThongBao(player, "Bạn còn thiếu " + sodu1 + " VNĐ");
                                }
                                break;
                            case 3:
                                if (player.getSession().balance >= 30000) {
                                    player.inventory.ruby += 50000;
                                    Service.getInstance().sendMoney(player);
                                    PlayerDAO.subVnd(player, 30000);
                                    Service.getInstance().sendThongBao(player, "Xong");
                                } else {
                                    Service.getInstance().sendThongBao(player, "Bạn còn thiếu " + sodu1 + " VNĐ");
                                }
                                break;
                            case 4:
                                if (player.getSession().balance >= 50000) {
                                    player.inventory.ruby += 80000;
                                    Service.getInstance().sendMoney(player);
                                    PlayerDAO.subVnd(player, 50000);
                                    Service.getInstance().sendThongBao(player, "Xong");
                                } else {
                                    Service.getInstance().sendThongBao(player, "Bạn còn thiếu " + sodu1 + " VNĐ");
                                }
                                break;
                            case 5:
                                if (player.getSession().balance >= 100000) {
                                    player.inventory.ruby += 130000;
                                    Service.getInstance().sendMoney(player);
                                    PlayerDAO.subVnd(player, 100000);
                                    player.inventory.coupon+= 2000;
                                    Service.getInstance().sendThongBao(player, "Xong");
                                } else {
                                    Service.getInstance().sendThongBao(player, "Bạn còn thiếu " + sodu1 + " VNĐ");
                                }
                                break;
                        }
                    }



            }else if (player.iDMark.getIndexMenu() == ConstNpc.QUA_TAN_THU) {
                    switch (select) {
                        case 0:
//                                        if (!player.gift.gemTanThu) {
                            if (true) {
                                player.inventory.gem = 100000;
                                Service.getInstance().sendMoney(player);
                                Service.getInstance().sendThongBao(player, "Bạn vừa nhận được 100K ngọc xanh");
                                player.gift.gemTanThu = true;
                            } else {
                                this.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Con đã nhận phần quà này rồi mà",
                                        "Đóng");
                            }
                            break;
//                            case 1:
//                                if (nhanVang) {
//                                    player.inventory.gold = Inventory.LIMIT_GOLD;
//                                    Service.getInstance().sendMoney(player);
//                                    Service.getInstance().sendThongBao(player, "Bạn vừa nhận được 2 tỉ vàng");
//                                } else {
//                                    this.npcChat("");
//                                }
//                                break;
                        case 1:
                            if (nhanDeTu) {
                                if (player.pet == null) {
                                    PetService.gI().createNormalPet(player);
                                    Service.getInstance().sendThongBao(player, "Bạn vừa nhận được đệ tử");
                                } else {
                                    this.npcChat("Con đã nhận đệ tử rồi");
                                }
                            }
                            break;
                    }
                } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_PHAN_THUONG) {
                    switch (select) {
                        case 0:
                            ShopServiceNew.gI().opendShop(player, "ITEMS_REWARD", true);
                            break;
//                            case 1:
//                                if (player.getSession().goldBar > 0) {
//                                    if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
//                                        int quantity = player.getSession().goldBar;
//                                        Item goldBar = ItemService.gI().createNewItem((short) 457, quantity);
//                                        InventoryServiceNew.gI().addItemBag(player, goldBar);
//                                        InventoryServiceNew.gI().sendItemBags(player);
//                                        this.npcChat(player, "Ông đã để " + quantity + " thỏi vàng vào hành trang con rồi đấy");
//                                        PlayerDAO.subGoldBar(player, quantity);
//                                        player.getSession().goldBar = 0;
//                                    } else {
//                                        this.npcChat(player, "Con phải có ít nhất 1 ô trống trong hành trang ông mới đưa cho con được");
//                                    }
//                                }
//                                break;
                    }
                } else if (player.iDMark.getIndexMenu() == ConstNpc.NAP_THE) {
                    Input.gI().createFormNapThe(player, (byte) select);
                }
            }
        };
    }

    public static Npc bulmaQK(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Cậu cần trang bị gì cứ đến chỗ tôi nhé", "Cửa\nhàng");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0://Shop
                                if (player.gender == ConstPlayer.TRAI_DAT) {
                                    ShopServiceNew.gI().opendShop(player, "BUNMA", true);
                                } else {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Xin lỗi cưng, chị chỉ bán đồ cho người Trái Đất", "Đóng");
                                }
                                break;
                        }
                    }
                }
            }
        };
    }

    public static Npc dende(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Anh cần trang bị gì cứ đến chỗ em nhé", "Cửa\nhàng");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0://Shop
                                if (player.gender == ConstPlayer.NAMEC) {
                                    ShopServiceNew.gI().opendShop(player, "DENDE", true);
                                } else {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Xin lỗi anh, em chỉ bán đồ cho dân tộc Namếc", "Đóng");
                                }
                                break;
                        }
                    }
                }
            }
        };
    }

    public static Npc appule(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Ngươi cần trang bị gì cứ đến chỗ ta nhé", "Cửa\nhàng");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0://Shop
                                if (player.gender == ConstPlayer.XAYDA) {
                                    ShopServiceNew.gI().opendShop(player, "APPULE", true);
                                } else {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Về hành tinh hạ đẳng của ngươi mà mua đồ cùi nhé. Tại đây ta chỉ bán đồ cho người Xayda thôi", "Đóng");
                                }
                                break;
                        }
                    }
                }
            }
        };
    }

    public static Npc drDrief(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player pl) {
                if (canOpenNpc(pl)) {
                    if (this.mapId == 84) {
                        this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                                "Tàu Vũ Trụ của ta có thể đưa cậu đến hành tinh khác chỉ trong 3 giây. Cậu muốn đi đâu?",
                                pl.gender == ConstPlayer.TRAI_DAT ? "Đến\nTrái Đất" : pl.gender == ConstPlayer.NAMEC ? "Đến\nNamếc" : "Đến\nXayda");
                    } else if (!TaskService.gI().checkDoneTaskTalkNpc(pl, this)) {
                        if (pl.playerTask.taskMain.id == 7) {
                            NpcService.gI().createTutorial(pl, this.avartar, "Hãy lên đường cứu đứa bé nhà tôi\n"
                                    + "Chắc bây giờ nó đang sợ hãi lắm rồi");
                        } else {
                            this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                                    "Tàu Vũ Trụ của ta có thể đưa cậu đến hành tinh khác chỉ trong 3 giây. Cậu muốn đi đâu?",
                                    "Đến\nNamếc", "Đến\nXayda", "Siêu thị");
                        }
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 84) {
                        ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 24, -1, -1);
                    } else if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0:
                                ChangeMapService.gI().changeMapBySpaceShip(player, 25, -1, -1);
                                break;
                            case 1:
                                ChangeMapService.gI().changeMapBySpaceShip(player, 26, -1, -1);
                                break;
                            case 2:
                                ChangeMapService.gI().changeMapBySpaceShip(player, 84, -1, -1);
                                break;
                        }
                    }
                }
            }
        };
    }

    public static Npc cargo(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player pl) {
                if (canOpenNpc(pl)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(pl, this)) {
                        if (pl.playerTask.taskMain.id == 7) {
                            NpcService.gI().createTutorial(pl, this.avartar, "Hãy lên đường cứu đứa bé nhà tôi\n"
                                    + "Chắc bây giờ nó đang sợ hãi lắm rồi");
                        } else {
                            this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                                    "Tàu Vũ Trụ của ta có thể đưa cậu đến hành tinh khác chỉ trong 3 giây. Cậu muốn đi đâu?",
                                    "Đến\nTrái Đất", "Đến\nXayda", "Siêu thị");
                        }
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0:
                                ChangeMapService.gI().changeMapBySpaceShip(player, 24, -1, -1);
                                break;
                            case 1:
                                ChangeMapService.gI().changeMapBySpaceShip(player, 26, -1, -1);
                                break;
                            case 2:
                                ChangeMapService.gI().changeMapBySpaceShip(player, 84, -1, -1);
                                break;
                        }
                    }
                }
            }
        };
    }

    public static Npc cui(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {

            private final int COST_FIND_BOSS = 50000000;

            @Override
            public void openBaseMenu(Player pl) {
                if (canOpenNpc(pl)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(pl, this)) {
                        if (pl.playerTask.taskMain.id == 7) {
                            NpcService.gI().createTutorial(pl, this.avartar, "Hãy lên đường cứu đứa bé nhà tôi\n"
                                    + "Chắc bây giờ nó đang sợ hãi lắm rồi");
                        } else {
                            if (this.mapId == 19) {

                                int taskId = TaskService.gI().getIdTask(pl);
                                switch (taskId) {
                                    case ConstTask.TASK_19_0:
                                        this.createOtherMenu(pl, ConstNpc.MENU_FIND_KUKU,
                                                "Đội quân của Fide đang ở Thung lũng Nappa, ta sẽ đưa ngươi đến đó",
                                                "Đến chỗ\nKuku\n(" + Util.numberToMoney(COST_FIND_BOSS) + " vàng)", "Đến Cold", "Đến\nNappa", "Từ chối");
                                        break;
                                    case ConstTask.TASK_19_1:
                                        this.createOtherMenu(pl, ConstNpc.MENU_FIND_MAP_DAU_DINH,
                                                "Đội quân của Fide đang ở Thung lũng Nappa, ta sẽ đưa ngươi đến đó",
                                                "Đến chỗ\nMập đầu đinh\n(" + Util.numberToMoney(COST_FIND_BOSS) + " vàng)", "Đến Cold", "Đến\nNappa", "Từ chối");
                                        break;
                                    case ConstTask.TASK_19_2:
                                        this.createOtherMenu(pl, ConstNpc.MENU_FIND_RAMBO,
                                                "Đội quân của Fide đang ở Thung lũng Nappa, ta sẽ đưa ngươi đến đó",
                                                "Đến chỗ\nRambo\n(" + Util.numberToMoney(COST_FIND_BOSS) + " vàng)", "Đến Cold", "Đến\nNappa", "Từ chối");
                                        break;
                                    default:
                                        this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                                                "Đội quân của Fide đang ở Thung lũng Nappa, ta sẽ đưa ngươi đến đó",
                                                "Đến Cold", "Đến\nNappa", "Từ chối");

                                        break;
                                }
                            } else if (this.mapId == 68) {
                                this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                                        "Ngươi muốn về Thành Phố Vegeta", "Đồng ý", "Từ chối");
                            } else {
                                this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                                        "Tàu vũ trụ Xayda sử dụng công nghệ mới nhất, "
                                        + "có thể đưa ngươi đi bất kỳ đâu, chỉ cần trả tiền là được.",
                                        "Đến\nTrái Đất", "Đến\nNamếc", "Siêu thị");
                            }
                        }
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 26) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 24, -1, -1);
                                    break;
                                case 1:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 25, -1, -1);
                                    break;
                                case 2:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 84, -1, -1);
                                    break;
                            }
                        }
                    }
                    if (this.mapId == 19) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 109, -1, 295);
                                    break;
                                case 1:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 68, -1, 90);
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_FIND_KUKU) {
                            switch (select) {
                                case 0:
                                    Boss boss = BossManager.gI().getBossById(BossID.KUKU);
                                    if (boss != null && !boss.isDie()) {
                                        if (player.inventory.gold >= COST_FIND_BOSS) {
                                            Zone z = MapService.gI().getMapCanJoin(player, boss.zone.map.mapId, boss.zone.zoneId);
                                            if (z.getNumOfPlayers() < z.maxPlayer) {
                                                player.inventory.gold -= COST_FIND_BOSS;
                                                ChangeMapService.gI().changeMap(player, boss.zone, boss.location.x, boss.location.y);
                                                Service.getInstance().sendMoney(player);
                                            } else {
                                                Service.getInstance().sendThongBao(player, "Khu vực đang full.");
                                            }
                                        } else {
                                            Service.getInstance().sendThongBao(player, "Không đủ vàng, còn thiếu "
                                                    + Util.numberToMoney(COST_FIND_BOSS - player.inventory.gold) + " vàng");
                                        }
                                        break;
                                    }
                                    Service.getInstance().sendThongBao(player, "Chết rồi ba...");
                                    break;
                                case 1:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 109, -1, 295);
                                    break;
                                case 2:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 68, -1, 90);
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_FIND_MAP_DAU_DINH) {
                            switch (select) {
                                case 0:
                                    Boss boss = BossManager.gI().getBossById(BossID.MAP_DAU_DINH);
                                    if (boss != null && !boss.isDie()) {
                                        if (player.inventory.gold >= COST_FIND_BOSS) {
                                            Zone z = MapService.gI().getMapCanJoin(player, boss.zone.map.mapId, boss.zone.zoneId);
                                            if (z.getNumOfPlayers() < z.maxPlayer) {
                                                player.inventory.gold -= COST_FIND_BOSS;
                                                ChangeMapService.gI().changeMap(player, boss.zone, boss.location.x, boss.location.y);
                                                Service.getInstance().sendMoney(player);
                                            } else {
                                                Service.getInstance().sendThongBao(player, "Khu vực đang full.");
                                            }
                                        } else {
                                            Service.getInstance().sendThongBao(player, "Không đủ vàng, còn thiếu "
                                                    + Util.numberToMoney(COST_FIND_BOSS - player.inventory.gold) + " vàng");
                                        }
                                        break;
                                    }
                                    Service.getInstance().sendThongBao(player, "Chết rồi ba...");
                                    break;
                                case 1:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 109, -1, 295);
                                    break;
                                case 2:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 68, -1, 90);
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_FIND_RAMBO) {
                            switch (select) {
                                case 0:
                                    Boss boss = BossManager.gI().getBossById(BossID.RAMBO);
                                    if (boss != null && !boss.isDie()) {
                                        if (player.inventory.gold >= COST_FIND_BOSS) {
                                            Zone z = MapService.gI().getMapCanJoin(player, boss.zone.map.mapId, boss.zone.zoneId);
                                            if (z.getNumOfPlayers() < z.maxPlayer) {
                                                player.inventory.gold -= COST_FIND_BOSS;
                                                ChangeMapService.gI().changeMap(player, boss.zone, boss.location.x, boss.location.y);
                                                Service.getInstance().sendMoney(player);
                                            } else {
                                                Service.getInstance().sendThongBao(player, "Khu vực đang full.");
                                            }
                                        } else {
                                            Service.getInstance().sendThongBao(player, "Không đủ vàng, còn thiếu "
                                                    + Util.numberToMoney(COST_FIND_BOSS - player.inventory.gold) + " vàng");
                                        }
                                        break;
                                    }
                                    Service.getInstance().sendThongBao(player, "Chết rồi ba...");
                                    break;
                                case 1:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 109, -1, 295);
                                    break;
                                case 2:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 68, -1, 90);
                                    break;
                            }
                        }
                    }
                    if (this.mapId == 68) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 19, -1, 1100);
                                    break;
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc santa(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Xin chào, ta có một số vật phẩm đặt biệt cậu có muốn xem không?",
                            "Cửa hàng", "Tiệm\nhồng ngọc", "Hộp Quà\nEvent 20/11");
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5 || this.mapId == 13 || this.mapId == 20) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0: //shop
                                    ShopServiceNew.gI().opendShop(player, "SANTA", false);
                                    break;
                                case 1: //tiệm hồng ngọc
                                    ShopServiceNew.gI().opendShop(player, "SANTA_RUBY", false);
                                    break;
                                case 2:
//                                    if (player.getSession().actived) {
                                    ShopServiceNew.gI().opendShop(player, "SANTA_EVENT", false);
//                                    } 
//                                    else {
//                                        Service.getInstance().sendThongBao(player, "Vui lòng kích hoạt tài khoản để sử dụng chức năng này");
//                                    }
                                    break;

//                                case 2: //tiệm hớt tóc
//                                    ShopServiceNew.gI().opendShop(player, "SANTA_HEAD", false);
//                                    break;
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc uron(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player pl) {
                if (canOpenNpc(pl)) {
                    ShopServiceNew.gI().opendShop(pl, "URON", false);
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {

                }
            }
        };
    }

    public static Npc baHatMit(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Ngươi tìm ta có việc gì?",
                                "Ép sao\ntrang bị", "Pha lê\nhóa\ntrang bị");
                    } else if (this.mapId == 121) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Ngươi tìm ta có việc gì?",
                                "Về đảo\nrùa");

                    } else {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Ngươi tìm ta có việc gì?",
                                "Cửa hàng\nBùa", "Nâng cấp\nVật phẩm",
                                "Nâng cấp\nBông tai\nPorata", "Làm phép\nNhập đá",
                                "Nhập\nNgọc Rồng", "Phân Rã\nĐồ Thần Linh", "Nâng Cấp \nĐồ Thiên Sứ");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
//                                                CombineService.gI().openTabCombine(player, CombineService.EP_SAO_TRANG_BI);
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.EP_SAO_TRANG_BI);
                                    break;
                                case 1:
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.PHA_LE_HOA_TRANG_BI);
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE) {
                            switch (player.combineNew.typeCombine) {
                                case CombineServiceNew.EP_SAO_TRANG_BI:
                                case CombineServiceNew.PHA_LE_HOA_TRANG_BI:
                                case CombineServiceNew.CHUYEN_HOA_TRANG_BI:
                                    if (select == 0) {
                                        CombineServiceNew.gI().startCombine(player);
                                    }
                                    break;
                            }
                        }
                    } else if (this.mapId == 112) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 5, -1, 1156);
                                    break;
                            }
                        }
                    } else if (this.mapId == 42 || this.mapId == 43 || this.mapId == 44) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0: //shop bùa
                                    createOtherMenu(player, ConstNpc.MENU_OPTION_SHOP_BUA,
                                            "Bùa của ta rất lợi hại, nhìn ngươi yếu đuối thế này, chắc muốn mua bùa để "
                                            + "mạnh mẽ à, mua không ta bán cho, xài rồi lại thích cho mà xem.",
                                            "Bùa\n1 giờ", "Bùa\n8 giờ", "Bùa\n1 tháng", "Đóng");
                                    break;
                                case 1:
//                                                CombineService.gI().openTabCombine(player, CombineService.NANG_CAP_TRANG_BI);
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_VAT_PHAM);
                                    break;
                                case 2: //nâng cấp bông tai
                                    break;
                                case 3: //làm phép nhập đá
                                    break;
                                case 4:
//                                                CombineService.gI().openTabCombine(player, CombineService.NHAP_NGOC_RONG);
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NHAP_NGOC_RONG);
                                    break;
                                case 5: //phân rã đồ thần linh
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.PHAN_RA_DO_THAN_LINH);
                                    break;
                                case 6:
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_DO_TS);
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPTION_SHOP_BUA) {
                            switch (select) {
                                case 0:
                                    ShopServiceNew.gI().opendShop(player, "BUA_1H", true);
                                    break;
                                case 1:
                                    ShopServiceNew.gI().opendShop(player, "BUA_8H", true);
                                    break;
                                case 2:
                                    ShopServiceNew.gI().opendShop(player, "BUA_1M", true);
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE) {
                            switch (player.combineNew.typeCombine) {
                                case CombineServiceNew.NANG_CAP_VAT_PHAM:
                                case CombineServiceNew.NANG_CAP_BONG_TAI:
                                case CombineServiceNew.LAM_PHEP_NHAP_DA:
                                case CombineServiceNew.NHAP_NGOC_RONG:
                                case CombineServiceNew.PHAN_RA_DO_THAN_LINH:
                                case CombineServiceNew.NANG_CAP_DO_TS:
                                case CombineServiceNew.NANG_CAP_SKH_VIP:
                                    if (select == 0) {
                                        CombineServiceNew.gI().startCombine(player);
                                    }
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_PHAN_RA_DO_THAN_LINH) {
                            if (select == 0) {
                                CombineServiceNew.gI().startCombine(player);
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_NANG_CAP_DO_TS) {
                            if (select == 0) {
                                CombineServiceNew.gI().startCombine(player);
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc ruongDo(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {

            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    InventoryServiceNew.gI().sendItemBox(player);
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {

                }
            }
        };
    }

    public static Npc dauThan(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    player.magicTree.openMenuTree();
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    TaskService.gI().checkDoneTaskConfirmMenuNpc(player, this, (byte) select);
                    switch (player.iDMark.getIndexMenu()) {
                        case ConstNpc.MAGIC_TREE_NON_UPGRADE_LEFT_PEA:
                            if (select == 0) {
                                player.magicTree.harvestPea();
                            } else if (select == 1) {
                                if (player.magicTree.level == 10) {
                                    player.magicTree.fastRespawnPea();
                                } else {
                                    player.magicTree.showConfirmUpgradeMagicTree();
                                }
                            } else if (select == 2) {
                                player.magicTree.fastRespawnPea();
                            }
                            break;
                        case ConstNpc.MAGIC_TREE_NON_UPGRADE_FULL_PEA:
                            if (select == 0) {
                                player.magicTree.harvestPea();
                            } else if (select == 1) {
                                player.magicTree.showConfirmUpgradeMagicTree();
                            }
                            break;
                        case ConstNpc.MAGIC_TREE_CONFIRM_UPGRADE:
                            if (select == 0) {
                                player.magicTree.upgradeMagicTree();
                            }
                            break;
                        case ConstNpc.MAGIC_TREE_UPGRADE:
                            if (select == 0) {
                                player.magicTree.fastUpgradeMagicTree();
                            } else if (select == 1) {
                                player.magicTree.showConfirmUnuppgradeMagicTree();
                            }
                            break;
                        case ConstNpc.MAGIC_TREE_CONFIRM_UNUPGRADE:
                            if (select == 0) {
                                player.magicTree.unupgradeMagicTree();
                            }
                            break;
                    }
                }
            }
        };
    }

    public static Npc calick(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            private final byte COUNT_CHANGE = 50;
            private int count;

            private void changeMap() {
                if (this.mapId != 102) {
                    count++;
                    if (this.count >= COUNT_CHANGE) {
                        count = 0;
                        this.map.npcs.remove(this);
                        Map map = MapService.gI().getMapForCalich();
                        this.mapId = map.mapId;
                        this.cx = Util.nextInt(100, map.mapWidth - 100);
                        this.cy = map.yPhysicInTop(this.cx, 0);
                        this.map = map;
                        this.map.npcs.add(this);
                    }
                }
            }

            @Override
            public void openBaseMenu(Player player) {
                player.iDMark.setIndexMenu(ConstNpc.BASE_MENU);
                if (TaskService.gI().getIdTask(player) < ConstTask.TASK_20_0) {
                    Service.getInstance().hideWaitDialog(player);
                    Service.getInstance().sendThongBao(player, "Không thể thực hiện");
                    return;
                }
                if (this.mapId != player.zone.map.mapId) {
                    Service.getInstance().sendThongBao(player, "Calích đã rời khỏi map!");
                    Service.getInstance().hideWaitDialog(player);
                    return;
                }

                if (this.mapId == 102) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Chào chú, cháu có thể giúp gì?",
                            "Kể\nChuyện", "Quay về\nQuá khứ");
                } else {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Chào chú, cháu có thể giúp gì?", "Kể\nChuyện", "Đi đến\nTương lai", "Từ chối");
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (this.mapId == 102) {
                    if (player.iDMark.isBaseMenu()) {
                        if (select == 0) {
                            //kể chuyện
                            NpcService.gI().createTutorial(player, this.avartar, ConstNpc.CALICK_KE_CHUYEN);
                        } else if (select == 1) {
                            //về quá khứ
                            ChangeMapService.gI().goToQuaKhu(player);
                        }
                    }
                } else if (player.iDMark.isBaseMenu()) {
                    if (select == 0) {
                        //kể chuyện
                        NpcService.gI().createTutorial(player, this.avartar, ConstNpc.CALICK_KE_CHUYEN);
                    } else if (select == 1) {
                        //đến tương lai
//                                    changeMap();
                        if (TaskService.gI().getIdTask(player) >= ConstTask.TASK_20_0) {
                            ChangeMapService.gI().goToTuongLai(player);
                        }
                    } else {
                        Service.getInstance().sendThongBao(player, "Không thể thực hiện");
                    }
                }
            }
        };
    }

    public static Npc jaco(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 24 || this.mapId == 25 || this.mapId == 26) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Gô Tên, Calich và Monaka đang gặp chuyện ở hành tinh Potaufeu \n Hãy đến đó ngay", "Đến \nPotaufeu");
                    } else if (this.mapId == 139) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Người muốn trở về?", "Quay về", "Từ chối");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 24 || this.mapId == 25 || this.mapId == 26) {
                        if (player.iDMark.isBaseMenu()) {
                            if (select == 0) {
                                //đến potaufeu
                                ChangeMapService.gI().goToPotaufeu(player);
                            }
                        }
                    } else if (this.mapId == 139) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                //về trạm vũ trụ
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 24 + player.gender, -1, -1);
                                    break;
                            }
                        }
                    }
                }
            }
        };
    }

//public static Npc Potage(int mapId, int status, int cx, int cy, int tempId, int avartar) {
//        return new Npc(mapId, status, cx, cy, tempId, avartar) {
//            @Override
//            public void openBaseMenu(Player player) {
//                if (canOpenNpc(player)) {
//                    if (this.mapId == 149) {
//                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
//                                "tét", "Gọi nhân bản");
//                    }
//                }
//            }
//            @Override
//            public void confirmMenu(Player player, int select) {
//                if (canOpenNpc(player)) {
//                   if (select == 0){
//                        BossManager.gI().createBoss(-214);
//                   }
//                }
//            }
//        };
//    }
    public static Npc vados(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5) {
                        this.createOtherMenu(player, 0,
                                "Con muốn gì nào?", "Map New", "Hông :3");
                    }
                    if (this.mapId == 164) {
                        this.createOtherMenu(player, 0,
                                "Con muốn gì nào?", "Quay ve");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    switch (player.iDMark.getIndexMenu()) {
                        case ConstNpc.BASE_MENU:
                            if (this.mapId == 5) {
                                if (player.iDMark.getIndexMenu() == 0) { // 
                                    switch (select) {
                                        case 0:
                                            ChangeMapService.gI().changeMapBySpaceShip(player, 164, -1, 354);
                                            Service.getInstance().changeFlag(player, Util.nextInt(8));
                                            break; // qua dhvt
                                    }
                                }
                            }
                            if (this.mapId == 164) {
                                switch (select) {
                                    case 0: // quay ve
                                        ChangeMapService.gI().changeMapBySpaceShip(player, 5, -1, 354);
                                        break;
                                }
                            }
                    }
                }
            }
        };
    }
        public static Npc bunmatocnau(int mapId, int status, int cx, int cy, int tempId, int avartar) {
            return new Npc(mapId, status, cx, cy, tempId, avartar) {
                @Override
                public void openBaseMenu(Player player) {
                    if (canOpenNpc(player)) {
                        switch (this.mapId) {

                            case 5:
                                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                        "Quay thưởng sẽ có tỉ lệ 1% ra bộ cải trang băng hải tặc mũ rơm \nKhi quay 200 lần chắc chắn sẽ nhận được cải trang \n Chỉ số sẽ ra ngẫu nhiên từ 25% đổ lên\n(Bộ cải trang sẽ mở quay đến ngày 23/3/2023)\n Hồng ngọc có thể kiếm từ nhiệm vụ hằng ngày hoặc fram quái\nĐiểm pity hiện tại: " + player.pity, "Quay thưởng\nx1\n(200 HN)", "Quay thưởng\nx10\n(1900 HN)");
                                break;
                        }

                    }
                }

                @Override
                public void confirmMenu(Player player, int select) {
                    if (canOpenNpc(player)) {
                        switch (this.mapId) {
                            case 5:
                                int rdGold = Util.nextInt(1000000, 10000000);

                                switch (select) {
                                    case 0:
                                        int Rate = Util.nextInt(1, 100);
                                        player.inventory.ruby -= 200;
                                        if (player.inventory.ruby >= 200) {
                                            if (Rate == 1 || player.pity >= 199) {  //neu random ra so 1 thi thuc hien cau lenh
                                                Item caitrang = ItemService.gI().createNewItem((short) (Util.nextInt(618, 626)));
                                                caitrang.itemOptions.add(new Item.ItemOption(50, Util.nextInt(25, 33)));
                                                caitrang.itemOptions.add(new Item.ItemOption(94, Util.nextInt(5, 22)));
                                                caitrang.itemOptions.add(new Item.ItemOption(103, Util.nextInt(25, 35)));
                                                caitrang.itemOptions.add(new Item.ItemOption(77, Util.nextInt(25, 35)));
                                                caitrang.itemOptions.add(new Item.ItemOption(101, 54));
                                                caitrang.itemOptions.add(new Item.ItemOption(100, Util.nextInt(50, 88)));
                                                caitrang.itemOptions.add(new Item.ItemOption(114, Util.nextInt(50, 70)));
                                                InventoryServiceNew.gI().addItemBag(player, caitrang);
                                                InventoryServiceNew.gI().sendItemBags(player);
                                                Service.getInstance().sendThongBao(player, "Bạn vừa nhận được cải trang");
                                                player.pity = 0;
                                            } else {
                                                player.inventory.gold += rdGold;
                                                Service.getInstance().sendThongBao(player, "Bạn nhận được " + Util.formatNumber(rdGold) + " Vàng");
                                                Service.getInstance().sendMoney(player);
                                                player.pity++;
                                            }
                                        }
                                        break;
                                    case 1:

                                        if (player.inventory.ruby >= 1900) {
                                            player.inventory.ruby -= 1900;
                                            for (int i = 1; i <= 10; i++) {
                                                if (Util.isTrue(1, 100) || player.pity >= 199) {
                                                    Item caitrang = ItemService.gI().createNewItem((short) (Util.nextInt(618, 626)));

                                                    caitrang.itemOptions.add(new Item.ItemOption(50, Util.nextInt(25, 33)));
                                                    caitrang.itemOptions.add(new Item.ItemOption(94, Util.nextInt(5, 22)));
                                                    caitrang.itemOptions.add(new Item.ItemOption(103, Util.nextInt(25, 35)));
                                                    caitrang.itemOptions.add(new Item.ItemOption(77, Util.nextInt(25, 35)));
                                                    caitrang.itemOptions.add(new Item.ItemOption(101, 54));
                                                    caitrang.itemOptions.add(new Item.ItemOption(100, Util.nextInt(50, 88)));
                                                    caitrang.itemOptions.add(new Item.ItemOption(114, Util.nextInt(50, 70)));
                                                    //  caitrang.itemOptions.add(new Item.ItemOption(106, 0));
                                                    InventoryServiceNew.gI().addItemBag(player, caitrang);
                                                    InventoryServiceNew.gI().sendItemBags(player);
                                                    Service.getInstance().sendThongBao(player, "Bạn vừa nhận được cải trang");
                                                    player.pity = 0;
                                                } else {
                                                    player.inventory.gold += rdGold;
                                                    Service.getInstance().sendThongBao(player, "Bạn nhận được " + Util.formatNumber(rdGold) + " Vàng");
                                                    Service.getInstance().sendMoney(player);
                                                    player.pity++;
                                                }
                                            }

                                        }
                                        break;
                                }
                                break;

                        }
                    }
                }
            };
        }
//8/
//    public static Npc npclytieunuong54(int mapId, int status, int cx, int cy, int tempId, int avartar) {
//        return new Npc(mapId, status, cx, cy, tempId, avartar) {
//            @Override
//            public void openBaseMenu(Player player) {
//                if (canOpenNpc(player)) {
//                    switch (this.mapId) {
//
//                        case 5:
//                            this.createOtherMenu(player, ConstNpc.BASE_MENU,
//                                    "hôm nay nhân nhịm 8/3 cậu có gì muốn tặng tôi à?\nTâm trạng tôi đang vui nếu cậu tặng hoa thì may mắn sẽ nhận được cải trang đấy>_<", "1\nBông hoa");
//                            break;
//                    }
//
//                }
//            }
//
//            @Override
//            public void confirmMenu(Player player, int select) {
//                if (canOpenNpc(player)) {
//                    switch (this.mapId) {
//                        case 5:
//                            switch (select) {
//                                case 0: 
//                                    try {
//                                    Item vpsk = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 589);
//                                    int soLuong = 0;
//                                    int random = Util.nextInt(5, 100);
//                                    if (vpsk != null) {
//                                        soLuong = vpsk.quantity;
//                                    }
//                                    if (soLuong >= 1 && InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
//                                        this.npcChat(player, "cám ơn " + player.name + " nhé");
//                                        Item caitrang = ItemService.gI().createNewItem((short) (1091));
//                                        if (random <= 5) {
//                                            caitrang.itemOptions.add(new Item.ItemOption(50, 28));
//                                            caitrang.itemOptions.add(new Item.ItemOption(77, 26));
//                                            caitrang.itemOptions.add(new Item.ItemOption(103, 25));
//                                            caitrang.itemOptions.add(new Item.ItemOption(168, 0));
//                                            caitrang.itemOptions.add(new Item.ItemOption(78, 6));
//                                            caitrang.itemOptions.add(new Item.ItemOption(93, 7));
//                                            InventoryServiceNew.gI().addItemBag(player, caitrang);
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, vpsk, 1);
//                                            InventoryServiceNew.gI().sendItemBags(player);
//                                            Service.getInstance().sendThongBao(player, "Bạn vừa nhận được cải trang");
//                                        } else {
//                                            player.inventory.coupon += 20;
//                                            Service.getInstance().sendThongBao(player, "bạn vừa nhận được 20 điểm sự kiện. Điểm hiện tại: " + player.inventory.coupon);
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, vpsk, 1);
//                                        }
//                                        //     InventoryServiceNew.gI().subQuantityItemsBag(player, vpsk, 1);
//                                    }
//                                    break;
//                                } catch (Exception ex) {
//                                }
//                                break;
//                            }
//                            break;
//                        case 122:
//                            switch (select) {
//                                case 0:
//                            try {
//                                    Item[] items = new Item[4];
//                                    items[0] = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 537);
//                                    items[1] = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 538);
//                                    items[2] = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 539);
//                                    items[3] = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 540);
//                                    for (Item item : items) {
//                                        if (item != null && item.quantity >= 99) {
//                                            Item caitrang = ItemService.gI().createNewItem((short) (player.gender + 544));
//                                            caitrang.itemOptions.add(new Item.ItemOption(93, Util.nextInt(2, 7)));
//                                            caitrang.itemOptions.add(new Item.ItemOption(50, Util.nextInt(16, 23)));
//                                            caitrang.itemOptions.add(new Item.ItemOption(94, Util.nextInt(5, 22)));
//                                            caitrang.itemOptions.add(new Item.ItemOption(103, Util.nextInt(30, 55)));
//                                            caitrang.itemOptions.add(new Item.ItemOption(77, Util.nextInt(50, 95)));
//                                            caitrang.itemOptions.add(new Item.ItemOption(101, 54));
//                                            caitrang.itemOptions.add(new Item.ItemOption(100, Util.nextInt(50, 88)));
//                                            caitrang.itemOptions.add(new Item.ItemOption(114, Util.nextInt(50, 70)));
//                                            caitrang.itemOptions.add(new Item.ItemOption(106, 0));
//                                            InventoryServiceNew.gI().addItemBag(player, caitrang);
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
//                                            InventoryServiceNew.gI().sendItemBags(player);
//                                            this.npcChat(player, "Ami phò phò, đa tạ thí chủ tương trợ, xin hãy món quà này, bần tăng sẽ niệm chú giải thoát cho Ngộ Không");
//                                            Thread.sleep(3000);
//                                            ChangeMapService.gI().changeMapInYard(player, 0, -1, -1);
//                                            return;
//                                        } else {
//                                            Service.getInstance().sendThongBao(player, "Không đủ 4 chữ");
//                                        }
//                                    }
//
//                                } catch (Exception ex) {
//                                    Service.getInstance().sendThongBao(player, "Không đủ 4 chữ");
//                                }
//
//                                break;
//                                case 1:
//                                    ChangeMapService.gI().changeMapInYard(player, 0, -1, -1);
//                                    break;
//                                case 2:
//                                    Util.showListTop(player, (byte) 2);
//                                    break;
//
//                            }
//                            break;
//                        case 123:
//                            switch (select) {
//                                case 0:
//                                    ChangeMapService.gI().changeMapBySpaceShip(player, 0, -1, -1);
//                                    break;
//
//                            }
//                            break;
//                    }
//                }
//            }
//        };
//    }

    public static Npc npclytieunuong54(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                createOtherMenu(player, 0, "Trò chơi Chọn ai đây đang được diễn ra, nếu bạn tin tưởng mình đang tràn đầy may mắn thì có thể tham gia thử", "Thể lệ", "Chọn\nThỏi vàng");
            }

            @Override
            public void confirmMenu(Player pl, int select) {
                if (canOpenNpc(pl)) {
                    String time = ((ChonAiDay.gI().lastTimeEnd - System.currentTimeMillis()) / 1000) + " giây";
                    int previousRandomNumber = ChonAiDay.getPreviousRandomNumber();
                    if (pl.iDMark.getIndexMenu() == 0) {
                        if (select == 0) {
                            createOtherMenu(pl, ConstNpc.IGNORE_MENU, "Thời gian giữa các giải là 5 phút\nKhi hết giờ, hệ thống sẽ ngẫu nhiên chọn ra 1 người may mắn.\nLưu ý: Số thỏi vàng nhận được sẽ bị nhà cái lụm đi 5%!Trong quá trình diễn ra khi đặt cược nếu thoát game mọi phần đặt đều sẽ bị hủy", "Ok");
                        } else if (select == 1) {
                            createOtherMenu(pl, 1, "Tổng giải thường: " + ChonAiDay.gI().goldNormar + " thỏi vàng, cơ hội trúng của bạn là: " + pl.percentGold(0) + "%\nTổng giải VIP: " + ChonAiDay.gI().goldVip + " thỏi vàng, cơ hội trúng của bạn là: " + pl.percentGold(1) + "%\nSố thỏi vàng đặt thường: " + pl.goldNormar + "\nSố thỏi vàng đặt VIP: " + pl.goldVIP + "\n Thời gian còn lại: " + time + "\nID: " + previousRandomNumber, "Cập nhập", "Thường\n20 thỏi\nvàng", "VIP\n200 thỏi\nvàng", "Đóng");
                        }
                    } else if (pl.iDMark.getIndexMenu() == 1) {
                        if (((ChonAiDay.gI().lastTimeEnd - System.currentTimeMillis()) / 1000) > 0) {
                            switch (select) {

                                case 0:
                                    createOtherMenu(pl, 1, "Tổng giải thường: " + ChonAiDay.gI().goldNormar + " thỏi vàng, cơ hội trúng của bạn là: " + pl.percentGold(0) + "%\nTổng giải VIP: " + ChonAiDay.gI().goldVip + " thỏi vàng, cơ hội trúng của bạn là: " + pl.percentGold(1) + "%\nSố thỏi vàng đặt thường: " + pl.goldNormar + "\nSố thỏi vàng đặt VIP: " + pl.goldVIP + "\n Thời gian còn lại: " + time + "\nID: " + previousRandomNumber, "Cập nhập", "Thường\n20 thỏi\nvàng", "VIP\n200 thỏi\nvàng", "Đóng");
                                    break;
                                case 1: 
try {
                                    if (InventoryServiceNew.gI().findItemBag(pl, 457).isNotNullItem() && InventoryServiceNew.gI().findItemBag(pl, 457).quantity >= 20) {
                                        InventoryServiceNew.gI().subQuantityItemsBag(pl, InventoryServiceNew.gI().findItemBag(pl, 457), 20);
                                        InventoryServiceNew.gI().sendItemBags(pl);
                                        pl.goldNormar += 20;
                                        ChonAiDay.gI().goldNormar += 20;
                                        ChonAiDay.gI().addPlayerNormar(pl);
                                        createOtherMenu(pl, 1, "Tổng giải thường: " + ChonAiDay.gI().goldNormar + " thỏi vàng, cơ hội trúng của bạn là: " + pl.percentGold(0) + "%\nTổng giải VIP: " + ChonAiDay.gI().goldVip + " thỏi vàng, cơ hội trúng của bạn là: " + pl.percentGold(1) + "%\nSố thỏi vàng đặt thường: " + pl.goldNormar + "\nSố thỏi vàng đặt VIP: " + pl.goldVIP + "\n Thời gian còn lại: " + time + "\nID: " + previousRandomNumber, "Cập nhập", "Thường\n20 thỏi\nvàng", "VIP\n200 thỏi\nvàng", "Đóng");
                                    } else {
                                        Service.getInstance().sendThongBao(pl, "Bạn không đủ thỏi vàng");
                                    }
                                } catch (Exception ex) {
                                }
                                break;
                                case 2: 
try {
                                    if (InventoryServiceNew.gI().findItemBag(pl, 457).isNotNullItem() && InventoryServiceNew.gI().findItemBag(pl, 457).quantity >= 200) {
                                        InventoryServiceNew.gI().subQuantityItemsBag(pl, InventoryServiceNew.gI().findItemBag(pl, 457), 200);
                                        InventoryServiceNew.gI().sendItemBags(pl);
                                        pl.goldVIP += 200;
                                        ChonAiDay.gI().goldVip += 200;
                                        ChonAiDay.gI().addPlayerVIP(pl);
                                        createOtherMenu(pl, 1, "Tổng giải thường: " + ChonAiDay.gI().goldNormar + " thỏi vàng, cơ hội trúng của bạn là: " + pl.percentGold(0) + "%\nTổng giải VIP: " + ChonAiDay.gI().goldVip + " thỏi vàng, cơ hội trúng của bạn là: " + pl.percentGold(1) + "%\nSố thỏi vàng đặt thường: " + pl.goldNormar + "\nSố thỏi vàng đặt VIP: " + pl.goldVIP + "\n Thời gian còn lại: " + time + "\nID: " + previousRandomNumber, "Cập nhập", "Thường\n20 thỏi\nvàng", "VIP\n200 thỏi\nvàng", "Đóng");
                                    } else {
                                        Service.getInstance().sendThongBao(pl, "Bạn không đủ thỏi vàng");
                                    }
                                } catch (Exception ex) {
                                }
                                break;

                            }
                        }
                    }
                }
            }
        };
    }

//    public static Npc npclytieunuong54(int mapId, int status, int cx, int cy, int tempId, int avartar) {
//        return new Npc(mapId, status, cx, cy, tempId, avartar) { tempId, int avartar) {
//       
//            @Override
//            public void openBaseMenu(Player player) {
//                if (this.mapId == 5) {
//                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
//                            "Con cần đổi gì", "Đổi đồ\nhuy diệt\ntrái đất", "Đổi đồ\nhuy diệt\nnamec", "Đổi đồ\nhuy diệt\nxayda", "Đổi đồ\nthiên sú\ntrái đất", "Đổi đồ\nthiên sú\nnamec", "Đổi đồ\nthiên sú\nxayda");
//                }
//            }
//
//            @Override
//            public void confirmMenu(Player player, int select) {
//                if (canOpenNpc(player)) {
//                    if (this.mapId == 5) {
//                        if (player.iDMark.isBaseMenu()) {
//                            switch (select) {
//                                case 0:
//                                    this.createOtherMenu(player, 1,
//                                            "Bạn muốn đổi 1 món đồ thần linh \ntrái đất cùng loại và x30 đá ngũ sắc \n|6|Để đổi lấy 1 món đồ húy diệt có tý lệ rà skh ko", "áo\nhúy diệt", "quần\nhúy diệt", "găng\nhúy diệt", "giày\nhúy diệt", "nhận\nhúy diệt", "Tu choi");
//                                    break;
//                                case 1:
//                                    this.createOtherMenu(player, 2,
//                                            "Bạn muốn đổi 1 món đồ thần linh \nnamec cùng loại và x30 đá ngũ sắc \n|6|Để đổi lấy 1 món đồ húy diệt có tý lệ rà skh ko", "áo\nhúy diệt", "quần\nhúy diệt", "găng\nhúy diệt", "giày\nhúy diệt", "nhận\nhúy diệt", "Tu choi");
//                                    break;
//                                case 2:
//                                    this.createOtherMenu(player, 3,
//                                            "Bạn muốn đổi 1 món đồ thần linh \nxayda cùng loại và x30 đá ngũ sắc \n|6|Để đổi lấy 1 món đồ húy diệt có tý lệ rà skh ko", "áo\nhúy diệt", "quần\nhúy diệt", "găng\nhúy diệt", "giày\nhúy diệt", "nhận\nhúy diệt", "Tu choi");
//                                    break;
//                                case 3:
//                                    this.createOtherMenu(player, 4,
//                                            "Bạn muốn đổi 1 món đồ húy diệt \ntrái đất cùng loại và x99 đá ngũ sắc \n|6|Để đổi lấy 1 món đồ thiên sứ có tý lệ rà skh ko", "áo\nthiên sứ", "quần\nthiên sứ", "găng\nthiên sứ", "giày\nthiên sứ", "nhận\nthiên sứ", "Tu choi");
//                                    break;
//                                case 4:
//                                    this.createOtherMenu(player, 5,
//                                            "Bạn muốn đổi 1 món đồ húy diệt \nnamec cùng loại và x99 đá ngũ sắc \n|6|Để đổi lấy 1 món đồ thiên sứ có tý lệ rà skh ko", "áo\nthiên sứ", "quần\nthiên sứ", "găng\nthiên sứ", "giày\nthiên sứ", "nhận\nthiên sứ", "Tu choi");
//                                    break;
//                                case 5:
//                                    this.createOtherMenu(player, 6,
//                                            "Bạn muốn đổi 1 món đồ húy diệt \nxayda cùng loại và x99 đá ngũ sắc \n|6|Để đổi lấy 1 món đồ thiên sứ có tý lệ rà skh ko", "áo\nthiên sứ", "quần\nthiên sứ", "găng\nthiên sứ", "giày\nthiên sứ", "nhận\nthiên sứ", "Tu choi");
//                                    break;
//                            }
//                        } else if (player.iDMark.getIndexMenu() == 1) { // action đổi dồ húy diệt
//                            switch (select) {
//                                case 0: // trade
//                                try {
//                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
//                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 555);
//                                    int soLuong = 0;
//                                    if (dns != null) {
//                                        soLuong = dns.quantity;
//                                    }
//                                    for (int i = 0; i < 12; i++) {
//                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 555 + i);
//
//                                        if (InventoryServiceNew.gI().isExistItemBag(player, 555 + i) && soLuong >= 30) {
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 30);
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
//                                            CombineServiceNew.gI().GetTrangBiKichHoathuydiet(player, 650 + i);
//                                            this.npcChat(player, "Chuyển Hóa Thành Công!");
//
//                                            break;
//                                        } else {
//                                            this.npcChat(player, "Yêu cầu cần Áo Thần linh trái đất + x30 Đá Ngũ Sắc!");
//                                        }
//
//                                    }
//                                } catch (Exception e) {
//
//                                }
//                                break;
//                                case 1: // trade
//                                    try {
//                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
//                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 556);
//                                    int soLuong = 0;
//                                    if (dns != null) {
//                                        soLuong = dns.quantity;
//                                    }
//                                    for (int i = 0; i < 12; i++) {
//                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 556 + i);
//
//                                        if (InventoryServiceNew.gI().isExistItemBag(player, 556 + i) && soLuong >= 30) {
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 30);
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
//                                            CombineServiceNew.gI().GetTrangBiKichHoathuydiet(player, 651 + i);
//                                            this.npcChat(player, "Chuyển Hóa Thành Công!");
//
//                                            break;
//                                        } else {
//                                            this.npcChat(player, "Yêu cầu cần Quần Thần linh trái đất + x30 Đá Ngũ Sắc!");
//                                        }
//
//                                    }
//                                } catch (Exception e) {
//
//                                }
//                                break;
//                                case 2: // trade
//                                    try {
//                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
//                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 562);
//                                    int soLuong = 0;
//                                    if (dns != null) {
//                                        soLuong = dns.quantity;
//                                    }
//                                    for (int i = 0; i < 12; i++) {
//                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 562 + i);
//
//                                        if (InventoryServiceNew.gI().isExistItemBag(player, 562 + i) && soLuong >= 30) {
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 30);
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
//                                            CombineServiceNew.gI().GetTrangBiKichHoathuydiet(player, 657 + i);
//                                            this.npcChat(player, "Chuyển Hóa Thành Công!");
//
//                                            break;
//                                        } else {
//                                            this.npcChat(player, "Yêu cầu cần Găng Thần linh trái đất + x30 Đá Ngũ Sắc!");
//                                        }
//
//                                    }
//                                } catch (Exception e) {
//
//                                }
//                                break;
//                                case 3: // trade
//                                    try {
//                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
//                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 563);
//                                    int soLuong = 0;
//                                    if (dns != null) {
//                                        soLuong = dns.quantity;
//                                    }
//                                    for (int i = 0; i < 12; i++) {
//                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 563 + i);
//
//                                        if (InventoryServiceNew.gI().isExistItemBag(player, 563 + i) && soLuong >= 30) {
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 30);
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
//                                            CombineServiceNew.gI().GetTrangBiKichHoathuydiet(player, 658 + i);
//                                            this.npcChat(player, "Chuyển Hóa Thành Công!");
//
//                                            break;
//                                        } else {
//                                            this.npcChat(player, "Yêu cầu cần Giày Thần linh trái đất + x30 Đá Ngũ Sắc!");
//                                        }
//
//                                    }
//                                } catch (Exception e) {
//
//                                }
//                                break;
//                                case 4: // trade
//                                    try {
//                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
//                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 561);
//                                    int soLuong = 0;
//                                    if (dns != null) {
//                                        soLuong = dns.quantity;
//                                    }
//                                    for (int i = 0; i < 12; i++) {
//                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 561 + i);
//
//                                        if (InventoryServiceNew.gI().isExistItemBag(player, 561 + i) && soLuong >= 30) {
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 30);
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
//                                            CombineServiceNew.gI().GetTrangBiKichHoathuydiet(player, 656 + i);
//                                            this.npcChat(player, "Chuyển Hóa Thành Công!");
//                                            break;
//                                        } else {
//                                            this.npcChat(player, "Yêu cầu cần Nhận Thần linh trái đất + x30 Đá Ngũ Sắc!");
//                                        }
//                                    }
//                                } catch (Exception e) {
//
//                                }
//                                break;
//                                case 5: // canel
//                                    break;
//                            }
//                        } else if (player.iDMark.getIndexMenu() == 2) { // action đổi dồ húy diệt
//                            switch (select) {
//                                case 0: // trade
//                                try {
//                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
//                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 557);
//                                    int soLuong = 0;
//                                    if (dns != null) {
//                                        soLuong = dns.quantity;
//                                    }
//                                    for (int i = 0; i < 12; i++) {
//                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 557 + i);
//
//                                        if (InventoryServiceNew.gI().isExistItemBag(player, 557 + i) && soLuong >= 30) {
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 30);
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
//                                            CombineServiceNew.gI().GetTrangBiKichHoathuydiet(player, 650 + i);
//                                            this.npcChat(player, "Chuyển Hóa Thành Công!");
//
//                                            break;
//                                        } else {
//                                            this.npcChat(player, "Yêu cầu cần Áo Thần linh namec + x30 Đá Ngũ Sắc!");
//                                        }
//
//                                    }
//                                } catch (Exception e) {
//
//                                }
//                                break;
//                                case 1: // trade
//                                    try {
//                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
//                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 558);
//                                    int soLuong = 0;
//                                    if (dns != null) {
//                                        soLuong = dns.quantity;
//                                    }
//                                    for (int i = 0; i < 12; i++) {
//                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 558 + i);
//
//                                        if (InventoryServiceNew.gI().isExistItemBag(player, 558 + i) && soLuong >= 30) {
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 30);
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
//                                            CombineServiceNew.gI().GetTrangBiKichHoathuydiet(player, 651 + i);
//                                            this.npcChat(player, "Chuyển Hóa Thành Công!");
//
//                                            break;
//                                        } else {
//                                            this.npcChat(player, "Yêu cầu cần Quần Thần linh namec + x30 Đá Ngũ Sắc!");
//                                        }
//
//                                    }
//                                } catch (Exception e) {
//
//                                }
//                                break;
//                                case 2: // trade
//                                    try {
//                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
//                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 564);
//                                    int soLuong = 0;
//                                    if (dns != null) {
//                                        soLuong = dns.quantity;
//                                    }
//                                    for (int i = 0; i < 12; i++) {
//                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 564 + i);
//
//                                        if (InventoryServiceNew.gI().isExistItemBag(player, 564 + i) && soLuong >= 30) {
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 30);
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
//                                            CombineServiceNew.gI().GetTrangBiKichHoathuydiet(player, 657 + i);
//                                            this.npcChat(player, "Chuyển Hóa Thành Công!");
//
//                                            break;
//                                        } else {
//                                            this.npcChat(player, "Yêu cầu cần Găng Thần linh namec + x30 Đá Ngũ Sắc!");
//                                        }
//
//                                    }
//                                } catch (Exception e) {
//
//                                }
//                                break;
//                                case 3: // trade
//                                    try {
//                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
//                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 565);
//                                    int soLuong = 0;
//                                    if (dns != null) {
//                                        soLuong = dns.quantity;
//                                    }
//                                    for (int i = 0; i < 12; i++) {
//                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 565 + i);
//
//                                        if (InventoryServiceNew.gI().isExistItemBag(player, 565 + i) && soLuong >= 30) {
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 30);
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
//                                            CombineServiceNew.gI().GetTrangBiKichHoathuydiet(player, 658 + i);
//                                            this.npcChat(player, "Chuyển Hóa Thành Công!");
//
//                                            break;
//                                        } else {
//                                            this.npcChat(player, "Yêu cầu cần Giày Thần linh namec + x30 Đá Ngũ Sắc!");
//                                        }
//
//                                    }
//                                } catch (Exception e) {
//
//                                }
//                                break;
//                                case 4: // trade
//                                    try {
//                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
//                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 561);
//                                    int soLuong = 0;
//                                    if (dns != null) {
//                                        soLuong = dns.quantity;
//                                    }
//                                    for (int i = 0; i < 12; i++) {
//                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 561 + i);
//
//                                        if (InventoryServiceNew.gI().isExistItemBag(player, 561 + i) && soLuong >= 30) {
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 30);
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
//                                            CombineServiceNew.gI().GetTrangBiKichHoathuydiet(player, 656 + i);
//                                            this.npcChat(player, "Chuyển Hóa Thành Công!");
//                                            break;
//                                        } else {
//                                            this.npcChat(player, "Yêu cầu cần Nhận Thần linh namec + x30 Đá Ngũ Sắc!");
//                                        }
//                                    }
//                                } catch (Exception e) {
//
//                                }
//                                break;
//                                case 5: // canel
//                                    break;
//                            }
//                        } else if (player.iDMark.getIndexMenu() == 3) { // action đổi dồ húy diệt
//                            switch (select) {
//                                case 0: // trade
//                                try {
//                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
//                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 559);
//                                    int soLuong = 0;
//                                    if (dns != null) {
//                                        soLuong = dns.quantity;
//                                    }
//                                    for (int i = 0; i < 12; i++) {
//                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 559 + i);
//
//                                        if (InventoryServiceNew.gI().isExistItemBag(player, 559 + i) && soLuong >= 30) {
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 30);
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
//                                            CombineServiceNew.gI().GetTrangBiKichHoathuydiet(player, 650 + i);
//                                            this.npcChat(player, "Chuyển Hóa Thành Công!");
//
//                                            break;
//                                        } else {
//                                            this.npcChat(player, "Yêu cầu cần Áo Thần linh xayda + x30 Đá Ngũ Sắc!");
//                                        }
//
//                                    }
//                                } catch (Exception e) {
//
//                                }
//                                break;
//                                case 1: // trade
//                                    try {
//                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
//                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 560);
//                                    int soLuong = 0;
//                                    if (dns != null) {
//                                        soLuong = dns.quantity;
//                                    }
//                                    for (int i = 0; i < 12; i++) {
//                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 560 + i);
//
//                                        if (InventoryServiceNew.gI().isExistItemBag(player, 560 + i) && soLuong >= 30) {
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 30);
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
//                                            CombineServiceNew.gI().GetTrangBiKichHoathuydiet(player, 651 + i);
//                                            this.npcChat(player, "Chuyển Hóa Thành Công!");
//
//                                            break;
//                                        } else {
//                                            this.npcChat(player, "Yêu cầu cần Quần Thần linh xayda + x30 Đá Ngũ Sắc!");
//                                        }
//
//                                    }
//                                } catch (Exception e) {
//
//                                }
//                                break;
//                                case 2: // trade
//                                    try {
//                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
//                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 566);
//                                    int soLuong = 0;
//                                    if (dns != null) {
//                                        soLuong = dns.quantity;
//                                    }
//                                    for (int i = 0; i < 12; i++) {
//                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 566 + i);
//
//                                        if (InventoryServiceNew.gI().isExistItemBag(player, 566 + i) && soLuong >= 30) {
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 30);
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
//                                            CombineServiceNew.gI().GetTrangBiKichHoathuydiet(player, 657 + i);
//                                            this.npcChat(player, "Chuyển Hóa Thành Công!");
//
//                                            break;
//                                        } else {
//                                            this.npcChat(player, "Yêu cầu cần Găng Thần linh xayda + x30 Đá Ngũ Sắc!");
//                                        }
//
//                                    }
//                                } catch (Exception e) {
//
//                                }
//                                break;
//                                case 3: // trade
//                                    try {
//                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
//                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 567);
//                                    int soLuong = 0;
//                                    if (dns != null) {
//                                        soLuong = dns.quantity;
//                                    }
//                                    for (int i = 0; i < 12; i++) {
//                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 567 + i);
//
//                                        if (InventoryServiceNew.gI().isExistItemBag(player, 567 + i) && soLuong >= 30) {
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 30);
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
//                                            CombineServiceNew.gI().GetTrangBiKichHoathuydiet(player, 658 + i);
//                                            this.npcChat(player, "Chuyển Hóa Thành Công!");
//
//                                            break;
//                                        } else {
//                                            this.npcChat(player, "Yêu cầu cần Giày Thần linh xayda + x30 Đá Ngũ Sắc!");
//                                        }
//
//                                    }
//                                } catch (Exception e) {
//
//                                }
//                                break;
//                                case 4: // trade
//                                    try {
//                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
//                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 561);
//                                    int soLuong = 0;
//                                    if (dns != null) {
//                                        soLuong = dns.quantity;
//                                    }
//                                    for (int i = 0; i < 12; i++) {
//                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 561 + i);
//
//                                        if (InventoryServiceNew.gI().isExistItemBag(player, 561 + i) && soLuong >= 30) {
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 30);
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
//                                            CombineServiceNew.gI().GetTrangBiKichHoathuydiet(player, 656 + i);
//                                            this.npcChat(player, "Chuyển Hóa Thành Công!");
//                                            break;
//                                        } else {
//                                            this.npcChat(player, "Yêu cầu cần Nhận Thần linh xayde + x30 Đá Ngũ Sắc!");
//                                        }
//                                    }
//                                } catch (Exception e) {
//
//                                }
//                                break;
//                                case 5: // canel
//                                    break;
//                            }
//                        } else if (player.iDMark.getIndexMenu() == 4) { // action đổi dồ thiên sứ
//                            switch (select) {
//                                case 0: // trade
//                                try {
//                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
//                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 650);
//                                    int soLuong = 0;
//                                    if (dns != null) {
//                                        soLuong = dns.quantity;
//                                    }
//                                    for (int i = 0; i < 12; i++) {
//                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 650 + i);
//
//                                        if (InventoryServiceNew.gI().isExistItemBag(player, 650 + i) && soLuong >= 99) {
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 99);
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
//                                            CombineServiceNew.gI().GetTrangBiKichHoatthiensu(player, 1048 + i);
//                                            this.npcChat(player, "Chuyển Hóa Thành Công!");
//
//                                            break;
//                                        } else {
//                                            this.npcChat(player, "Yêu cầu cần Áo húy diệt trái đất + x99 Đá Ngũ Sắc!");
//                                        }
//
//                                    }
//                                } catch (Exception e) {
//
//                                }
//                                break;
//                                case 1: // trade
//                                    try {
//                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
//                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 651);
//                                    int soLuong = 0;
//                                    if (dns != null) {
//                                        soLuong = dns.quantity;
//                                    }
//                                    for (int i = 0; i < 12; i++) {
//                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 651 + i);
//
//                                        if (InventoryServiceNew.gI().isExistItemBag(player, 651 + i) && soLuong >= 99) {
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 99);
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
//                                            CombineServiceNew.gI().GetTrangBiKichHoatthiensu(player, 1051 + i);
//                                            this.npcChat(player, "Chuyển Hóa Thành Công!");
//
//                                            break;
//                                        } else {
//                                            this.npcChat(player, "Yêu cầu cần Quần húy diệt trái đất + x99 Đá Ngũ Sắc!");
//                                        }
//
//                                    }
//                                } catch (Exception e) {
//
//                                }
//                                break;
//                                case 2: // trade
//                                    try {
//                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
//                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 657);
//                                    int soLuong = 0;
//                                    if (dns != null) {
//                                        soLuong = dns.quantity;
//                                    }
//                                    for (int i = 0; i < 12; i++) {
//                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 657 + i);
//
//                                        if (InventoryServiceNew.gI().isExistItemBag(player, 657 + i) && soLuong >= 99) {
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 99);
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
//                                            CombineServiceNew.gI().GetTrangBiKichHoatthiensu(player, 1054 + i);
//                                            this.npcChat(player, "Chuyển Hóa Thành Công!");
//
//                                            break;
//                                        } else {
//                                            this.npcChat(player, "Yêu cầu cần Găng húy diệt trái đất + x99 Đá Ngũ Sắc!");
//                                        }
//
//                                    }
//                                } catch (Exception e) {
//
//                                }
//                                break;
//                                case 3: // trade
//                                    try {
//                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
//                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 658);
//                                    int soLuong = 0;
//                                    if (dns != null) {
//                                        soLuong = dns.quantity;
//                                    }
//                                    for (int i = 0; i < 12; i++) {
//                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 658 + i);
//
//                                        if (InventoryServiceNew.gI().isExistItemBag(player, 658 + i) && soLuong >= 99) {
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 99);
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
//                                            CombineServiceNew.gI().GetTrangBiKichHoatthiensu(player, 1057 + i);
//                                            this.npcChat(player, "Chuyển Hóa Thành Công!");
//
//                                            break;
//                                        } else {
//                                            this.npcChat(player, "Yêu cầu cần Giày húy diệt trái đất + x99 Đá Ngũ Sắc!");
//                                        }
//
//                                    }
//                                } catch (Exception e) {
//
//                                }
//                                break;
//                                case 4: // trade
//                                    try {
//                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
//                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 656);
//                                    int soLuong = 0;
//                                    if (dns != null) {
//                                        soLuong = dns.quantity;
//                                    }
//                                    for (int i = 0; i < 12; i++) {
//                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 656 + i);
//
//                                        if (InventoryServiceNew.gI().isExistItemBag(player, 656 + i) && soLuong >= 99) {
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 99);
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
//                                            CombineServiceNew.gI().GetTrangBiKichHoatthiensu(player, 1060 + i);
//                                            this.npcChat(player, "Chuyển Hóa Thành Công!");
//
//                                            break;
//                                        } else {
//                                            this.npcChat(player, "Yêu cầu cần nhận húy diệt trái đất + x99 Đá Ngũ Sắc!");
//                                        }
//
//                                    }
//                                } catch (Exception e) {
//
//                                }
//                                break;
//                                case 5: // canel
//                                    break;
//                            }
//                        } else if (player.iDMark.getIndexMenu() == 5) { // action đổi dồ thiên sứ
//                            switch (select) {
//                                case 0: // trade
//                                try {
//                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
//                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 652);
//                                    int soLuong = 0;
//                                    if (dns != null) {
//                                        soLuong = dns.quantity;
//                                    }
//                                    for (int i = 0; i < 12; i++) {
//                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 652 + i);
//
//                                        if (InventoryServiceNew.gI().isExistItemBag(player, 652 + i) && soLuong >= 99) {
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 99);
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
//                                            CombineServiceNew.gI().GetTrangBiKichHoatthiensu(player, 1049 + i);
//                                            this.npcChat(player, "Chuyển Hóa Thành Công!");
//
//                                            break;
//                                        } else {
//                                            this.npcChat(player, "Yêu cầu cần Áo húy diệt namec + x99 Đá Ngũ Sắc!");
//                                        }
//
//                                    }
//                                } catch (Exception e) {
//
//                                }
//                                break;
//                                case 1: // trade
//                                    try {
//                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
//                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 653);
//                                    int soLuong = 0;
//                                    if (dns != null) {
//                                        soLuong = dns.quantity;
//                                    }
//                                    for (int i = 0; i < 12; i++) {
//                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 653 + i);
//
//                                        if (InventoryServiceNew.gI().isExistItemBag(player, 653 + i) && soLuong >= 99) {
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 99);
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
//                                            CombineServiceNew.gI().GetTrangBiKichHoatthiensu(player, 1052 + i);
//                                            this.npcChat(player, "Chuyển Hóa Thành Công!");
//
//                                            break;
//                                        } else {
//                                            this.npcChat(player, "Yêu cầu cần Quần húy diệt namec + x99 Đá Ngũ Sắc!");
//                                        }
//
//                                    }
//                                } catch (Exception e) {
//
//                                }
//                                break;
//                                case 2: // trade
//                                    try {
//                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
//                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 659);
//                                    int soLuong = 0;
//                                    if (dns != null) {
//                                        soLuong = dns.quantity;
//                                    }
//                                    for (int i = 0; i < 12; i++) {
//                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 659 + i);
//
//                                        if (InventoryServiceNew.gI().isExistItemBag(player, 659 + i) && soLuong >= 99) {
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 99);
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
//                                            CombineServiceNew.gI().GetTrangBiKichHoatthiensu(player, 1055 + i);
//                                            this.npcChat(player, "Chuyển Hóa Thành Công!");
//
//                                            break;
//                                        } else {
//                                            this.npcChat(player, "Yêu cầu cần Găng húy diệt namec + x99 Đá Ngũ Sắc!");
//                                        }
//
//                                    }
//                                } catch (Exception e) {
//
//                                }
//                                break;
//                                case 3: // trade
//                                    try {
//                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
//                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 660);
//                                    int soLuong = 0;
//                                    if (dns != null) {
//                                        soLuong = dns.quantity;
//                                    }
//                                    for (int i = 0; i < 12; i++) {
//                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 660 + i);
//
//                                        if (InventoryServiceNew.gI().isExistItemBag(player, 660 + i) && soLuong >= 99) {
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 99);
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
//                                            CombineServiceNew.gI().GetTrangBiKichHoatthiensu(player, 1058 + i);
//                                            this.npcChat(player, "Chuyển Hóa Thành Công!");
//
//                                            break;
//                                        } else {
//                                            this.npcChat(player, "Yêu cầu cần Giày húy diệt namec + x99 Đá Ngũ Sắc!");
//                                        }
//
//                                    }
//                                } catch (Exception e) {
//
//                                }
//                                break;
//                                case 4: // trade
//                                    try {
//                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
//                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 656);
//                                    int soLuong = 0;
//                                    if (dns != null) {
//                                        soLuong = dns.quantity;
//                                    }
//                                    for (int i = 0; i < 12; i++) {
//                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 656 + i);
//
//                                        if (InventoryServiceNew.gI().isExistItemBag(player, 656 + i) && soLuong >= 99) {
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 99);
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
//                                            CombineServiceNew.gI().GetTrangBiKichHoatthiensu(player, 1061 + i);
//                                            this.npcChat(player, "Chuyển Hóa Thành Công!");
//
//                                            break;
//                                        } else {
//                                            this.npcChat(player, "Yêu cầu cần nhận húy diệt namec + x99 Đá Ngũ Sắc!");
//                                        }
//
//                                    }
//                                } catch (Exception e) {
//
//                                }
//                                break;
//                                case 5: // canel
//                                    break;
//                            }
//                        } else if (player.iDMark.getIndexMenu() == 6) { // action đổi dồ thiên sứ
//                            switch (select) {
//                                case 0: // trade
//                                try {
//                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
//                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 654);
//                                    int soLuong = 0;
//                                    if (dns != null) {
//                                        soLuong = dns.quantity;
//                                    }
//                                    for (int i = 0; i < 12; i++) {
//                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 654 + i);
//
//                                        if (InventoryServiceNew.gI().isExistItemBag(player, 654 + i) && soLuong >= 99) {
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 99);
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
//                                            CombineServiceNew.gI().GetTrangBiKichHoatthiensu(player, 1050 + i);
//                                            this.npcChat(player, "Chuyển Hóa Thành Công!");
//
//                                            break;
//                                        } else {
//                                            this.npcChat(player, "Yêu cầu cần Áo húy diệt xayda + x99 Đá Ngũ Sắc!");
//                                        }
//
//                                    }
//                                } catch (Exception e) {
//
//                                }
//                                break;
//                                case 1: // trade
//                                    try {
//                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
//                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 655);
//                                    int soLuong = 0;
//                                    if (dns != null) {
//                                        soLuong = dns.quantity;
//                                    }
//                                    for (int i = 0; i < 12; i++) {
//                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 655 + i);
//
//                                        if (InventoryServiceNew.gI().isExistItemBag(player, 655 + i) && soLuong >= 99) {
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 99);
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
//                                            CombineServiceNew.gI().GetTrangBiKichHoatthiensu(player, 1053 + i);
//                                            this.npcChat(player, "Chuyển Hóa Thành Công!");
//
//                                            break;
//                                        } else {
//                                            this.npcChat(player, "Yêu cầu cần Quần húy diệt xayda + x99 Đá Ngũ Sắc!");
//                                        }
//
//                                    }
//                                } catch (Exception e) {
//
//                                }
//                                break;
//                                case 2: // trade
//                                    try {
//                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
//                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 661);
//                                    int soLuong = 0;
//                                    if (dns != null) {
//                                        soLuong = dns.quantity;
//                                    }
//                                    for (int i = 0; i < 12; i++) {
//                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 661 + i);
//
//                                        if (InventoryServiceNew.gI().isExistItemBag(player, 661 + i) && soLuong >= 99) {
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 99);
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
//                                            CombineServiceNew.gI().GetTrangBiKichHoatthiensu(player, 1056 + i);
//                                            this.npcChat(player, "Chuyển Hóa Thành Công!");
//
//                                            break;
//                                        } else {
//                                            this.npcChat(player, "Yêu cầu cần Găng húy diệt xayda + x99 Đá Ngũ Sắc!");
//                                        }
//
//                                    }
//                                } catch (Exception e) {
//
//                                }
//                                break;
//                                case 3: // trade
//                                    try {
//                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
//                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 662);
//                                    int soLuong = 0;
//                                    if (dns != null) {
//                                        soLuong = dns.quantity;
//                                    }
//                                    for (int i = 0; i < 12; i++) {
//                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 662 + i);
//
//                                        if (InventoryServiceNew.gI().isExistItemBag(player, 662 + i) && soLuong >= 99) {
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 99);
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
//                                            CombineServiceNew.gI().GetTrangBiKichHoatthiensu(player, 1059 + i);
//                                            this.npcChat(player, "Chuyển Hóa Thành Công!");
//
//                                            break;
//                                        } else {
//                                            this.npcChat(player, "Yêu cầu cần Giày húy diệt xayda + x99 Đá Ngũ Sắc!");
//                                        }
//
//                                    }
//                                } catch (Exception e) {
//
//                                }
//                                break;
//                                case 4: // trade
//                                    try {
//                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
//                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 656);
//                                    int soLuong = 0;
//                                    if (dns != null) {
//                                        soLuong = dns.quantity;
//                                    }
//                                    for (int i = 0; i < 12; i++) {
//                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 656 + i);
//
//                                        if (InventoryServiceNew.gI().isExistItemBag(player, 656 + i) && soLuong >= 99) {
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 99);
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
//                                            CombineServiceNew.gI().GetTrangBiKichHoatthiensu(player, 1062 + i);
//                                            this.npcChat(player, "Chuyển Hóa Thành Công!");
//
//                                            break;
//                                        } else {
//                                            this.npcChat(player, "Yêu cầu cần nhận húy diệt xayda + x99 Đá Ngũ Sắc!");
//                                        }
//
//                                    }
//                                } catch (Exception e) {
//
//                                }
//                                break;
//                                case 5: // canel
//                                    break;
//                            }
//                        }
//                    }
//                }
//            }
//        };
//    }
    public static Npc thuongDe(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {

            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 45) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Con muốn làm gì nào", "Đến Kaio", "Quay số\nmay mắn");
                    }
                    if (this.mapId == 0) {
                        this.createOtherMenu(player, 0,
                                "Con muốn gì nào?\nCon đang còn : " + player.pointPvp + " điểm PvP Point", "Đến DHVT", "Đổi Cải trang sự kiên", "Top PVP");
                    }
                    if (this.mapId == 129) {
                        this.createOtherMenu(player, 0,
                                "Con muốn gì nào?", "Quay ve");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 0) {
                        if (player.iDMark.getIndexMenu() == 0) { // 
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 129, -1, 354);
                                    Service.getInstance().changeFlag(player, Util.nextInt(8));
                                    break; // qua dhvt
                                case 1:  // 
                                    this.createOtherMenu(player, 1,
                                            "Bạn có muốn đổi 500 điểm PVP lấy \n|6|Cải trang Mèo Kid Lân với tất cả chỉ số là 80%\n ", "Ok", "Tu choi");
                                    // bat menu doi item
                                    break;

                                case 2:  // 
                                    Util.showListTop(player, (byte) 3);
                                    // mo top pvp
                                    break;

                            }
                        }
                        if (player.iDMark.getIndexMenu() == 1) { // action doi item
                            switch (select) {
                                case 0: // trade
                                    if (player.pointPvp >= 500) {
                                        player.pointPvp -= 500;
                                        Item item = ItemService.gI().createNewItem((short) (1104));
                                        item.itemOptions.add(new Item.ItemOption(49, 80));
                                        item.itemOptions.add(new Item.ItemOption(77, 80));
                                        item.itemOptions.add(new Item.ItemOption(103, 50));
                                        item.itemOptions.add(new Item.ItemOption(207, 0));
                                        item.itemOptions.add(new Item.ItemOption(33, 0));
//                                      
                                        InventoryServiceNew.gI().addItemBag(player, item);
                                        Service.getInstance().sendThongBao(player, "Chúc Mừng Bạn Đổi Cải Trang Thành Công !");
                                    } else {
                                        Service.getInstance().sendThongBao(player, "Không đủ điểm bạn còn " + (500 - player.pointPvp) + " Điểm nữa");
                                    }
                                    break;
                            }
                        }
                    }
                    if (this.mapId == 129) {
                        switch (select) {
                            case 0: // quay ve
                                ChangeMapService.gI().changeMapBySpaceShip(player, 0, -1, 354);
                                break;
                        }
                    }
                    if (this.mapId == 45) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 48, -1, 354);
                                    break;
                                case 1:
                                    this.createOtherMenu(player, ConstNpc.MENU_CHOOSE_LUCKY_ROUND,
                                            "Con muốn làm gì nào?", "Quay bằng\nvàng",
                                            "Rương phụ\n("
                                            + (player.inventory.itemsBoxCrackBall.size()
                                            - InventoryServiceNew.gI().getCountEmptyListItem(player.inventory.itemsBoxCrackBall))
                                            + " món)",
                                            "Xóa hết\ntrong rương", "Đóng");
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_CHOOSE_LUCKY_ROUND) {
                            switch (select) {
                                case 0:
                                    LuckyRound.gI().openCrackBallUI(player, LuckyRound.USING_GOLD);
                                    break;
                                case 1:
                                    ShopServiceNew.gI().opendShop(player, "ITEMS_LUCKY_ROUND", true);
                                    break;
                                case 2:
                                    NpcService.gI().createMenuConMeo(player,
                                            ConstNpc.CONFIRM_REMOVE_ALL_ITEM_LUCKY_ROUND, this.avartar,
                                            "Con có chắc muốn xóa hết vật phẩm trong rương phụ? Sau khi xóa "
                                            + "sẽ không thể khôi phục!",
                                            "Đồng ý", "Hủy bỏ");
                                    break;
                            }
                        }
                    }

                }
            }
        };
    }

    public static Npc thanVuTru(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 48) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Con muốn làm gì nào", "Di chuyển");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 48) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    this.createOtherMenu(player, ConstNpc.MENU_DI_CHUYEN,
                                            "Con muốn đi đâu?", "Về\nthần điện", "Thánh địa\nKaio", "Con\nđường\nrắn độc", "Từ chối");
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_DI_CHUYEN) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 45, -1, 354);
                                    break;
                                case 1:
                                    ChangeMapService.gI().changeMap(player, 50, -1, 318, 336);
                                    break;
                                case 2:
                                    //con đường rắn độc
                                    break;
                            }
                        }
                    }
                }
            }

        };
    }

    public static Npc kibit(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 50) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?",
                                "Đến\nKaio", "Từ chối");
                    }
                    if (this.mapId == 114) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?",
                                "Từ chối");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 50) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMap(player, 48, -1, 354, 240);
                                    break;
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc osin(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 50) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?",
                                "Đến\nKaio", "Đến\nhành tinh\nBill", "Từ chối");
                    } else if (this.mapId == 154) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?",
                                "Về thánh địa", "Đến\nhành tinh\nngục tù", "Từ chối");
                    } else if (this.mapId == 155) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?",
                                "Quay về", "Từ chối");
                    } else if (this.mapId == 52) {
                        try {
                            MapMaBu.gI().setTimeJoinMapMaBu();
                            if (this.mapId == 52) {
                                long now = System.currentTimeMillis();
                                if (now > MapMaBu.TIME_OPEN_MABU && now < MapMaBu.TIME_CLOSE_MABU) {
                                    this.createOtherMenu(player, ConstNpc.MENU_OPEN_MMB, "Đại chiến Ma Bư đã mở, "
                                            + "ngươi có muốn tham gia không?",
                                            "Hướng dẫn\nthêm", "Tham gia", "Từ chối");
                                } else {
                                    this.createOtherMenu(player, ConstNpc.MENU_NOT_OPEN_MMB,
                                            "Ta có thể giúp gì cho ngươi?", "Hướng dẫn", "Từ chối");
                                }

                            }
                        } catch (Exception ex) {
                            Logger.error("Lỗi mở menu osin");
                        }

                    } else if (this.mapId >= 114 && this.mapId < 120 && this.mapId != 116) {
                        if (player.fightMabu.pointMabu >= player.fightMabu.POINT_MAX) {
                            this.createOtherMenu(player, ConstNpc.GO_UPSTAIRS_MENU, "Ta có thể giúp gì cho ngươi ?",
                                    "Lên Tầng!", "Quay về", "Từ chối");
                        } else {
                            this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?",
                                    "Quay về", "Từ chối");
                        }
                    } else if (this.mapId == 120) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?",
                                "Quay về", "Từ chối");
                    } else {
                        super.openBaseMenu(player);
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 50) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMap(player, 48, -1, 354, 240);
                                    break;
                                case 1:
                                    ChangeMapService.gI().changeMap(player, 154, -1, 200, 312);
                                    break;
                            }
                        }
                    } else if (this.mapId == 154) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMap(player, 50, -1, 318, 336);
                                    break;
                                case 1:
                                    ChangeMapService.gI().changeMap(player, 155, -1, 111, 792);
                                    break;
                            }
                        }
                    } else if (this.mapId == 155) {
                        if (player.iDMark.isBaseMenu()) {
                            if (select == 0) {
                                ChangeMapService.gI().changeMap(player, 154, -1, 200, 312);
                            }
                        }
                    } else if (this.mapId == 52) {
                        switch (player.iDMark.getIndexMenu()) {
                            case ConstNpc.MENU_REWARD_MMB:
                                break;
                            case ConstNpc.MENU_OPEN_MMB:
                                if (select == 0) {
                                    NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_MAP_MA_BU);
                                } else if (select == 1) {
//                                    if (!player.getSession().actived) {
//                                        Service.getInstance().sendThongBao(player, "Vui lòng kích hoạt tài khoản để sử dụng chức năng này");
//                                    } else
                                    ChangeMapService.gI().changeMap(player, 114, -1, 318, 336);
                                }
                                break;
                            case ConstNpc.MENU_NOT_OPEN_BDW:
                                if (select == 0) {
                                    NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_MAP_MA_BU);
                                }
                                break;
                        }
                    } else if (this.mapId >= 114 && this.mapId < 120 && this.mapId != 116) {
                        if (player.iDMark.getIndexMenu() == ConstNpc.GO_UPSTAIRS_MENU) {
                            if (select == 0) {
                                player.fightMabu.clear();
                                ChangeMapService.gI().changeMap(player, this.map.mapIdNextMabu((short) this.mapId), -1, this.cx, this.cy);
                            } else if (select == 1) {
                                ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, 0, -1);
                            }
                        } else {
                            if (select == 0) {
                                ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, 0, -1);
                            }
                        }
                    } else if (this.mapId == 120) {
                        if (player.iDMark.getIndexMenu() == ConstNpc.BASE_MENU) {
                            if (select == 0) {
                                ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, 0, -1);
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc linhCanh(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (player.clan == null) {
                        this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Chỉ tiếp các bang hội, miễn tiếp khách vãng lai", "Đóng");
                        return;
                    }
                    if (player.clan.getMembers().size() < DoanhTrai.N_PLAYER_CLAN) {
                        this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Bang hội phải có ít nhất 5 thành viên mới có thể mở", "Đóng");
                        return;
                    }
                    if (player.clan.doanhTrai != null) {
                        createOtherMenu(player, ConstNpc.MENU_JOIN_DOANH_TRAI,
                                "Bang hội của ngươi đang đánh trại độc nhãn\n"
                                + "Thời gian còn lại là "
                                + TimeUtil.getSecondLeft(player.clan.doanhTrai.getLastTimeOpen(), DoanhTrai.TIME_DOANH_TRAI / 1000)
                                + ". Ngươi có muốn tham gia không?",
                                "Tham gia", "Không", "Hướng\ndẫn\nthêm");
                        return;
                    }
                    int nPlSameClan = 0;
                    for (Player pl : player.zone.getPlayers()) {
                        if (!pl.equals(player) && pl.clan != null
                                && pl.clan.equals(player.clan) && pl.location.x >= 1285
                                && pl.location.x <= 1645) {
                            nPlSameClan++;
                        }
                    }
                    if (nPlSameClan < DoanhTrai.N_PLAYER_MAP) {
                        createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Ngươi phải có ít nhất " + DoanhTrai.N_PLAYER_MAP + " đồng đội cùng bang đứng gần mới có thể\nvào\n"
                                + "tuy nhiên ta khuyên ngươi nên đi cùng với 3-4 người để khỏi chết.\n"
                                + "Hahaha.", "OK", "Hướng\ndẫn\nthêm");
                        return;
                    }
                    if (player.clanMember.getNumDateFromJoinTimeToToday() < 1) {
                        createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Doanh trại chỉ cho phép những người ở trong bang trên 1 ngày. Hẹn ngươi quay lại vào lúc khác",
                                "OK", "Hướng\ndẫn\nthêm");
                        return;
                    }
                    if (player.clan.haveGoneDoanhTrai) {
                        createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Bang hội của ngươi đã đi trại lúc " + TimeUtil.formatTime(player.clan.lastTimeOpenDoanhTrai, "HH:mm:ss") + " hôm nay. Người mở\n"
                                + "(" + player.clan.playerOpenDoanhTrai + "). Hẹn ngươi quay lại vào ngày mai", "OK", "Hướng\ndẫn\nthêm");
                        return;
                    }
                    createOtherMenu(player, ConstNpc.MENU_JOIN_DOANH_TRAI,
                            "Hôm nay bang hội của ngươi chưa vào trại lần nào. Ngươi có muốn vào\n"
                            + "không?\nĐể vào, ta khuyên ngươi nên có 3-4 người cùng bang đi cùng",
                            "Vào\n(miễn phí)", "Không", "Hướng\ndẫn\nthêm");
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    switch (player.iDMark.getIndexMenu()) {
                        case ConstNpc.MENU_JOIN_DOANH_TRAI:
                            if (select == 0) {
                                DoanhTraiService.gI().joinDoanhTrai(player);
                            } else if (select == 2) {
                                NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_DOANH_TRAI);
                            }
                            break;
                        case ConstNpc.IGNORE_MENU:
                            if (select == 1) {
                                NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_DOANH_TRAI);
                            }
                            break;
                    }
                }
            }
        };
    }

    public static Npc quaTrunghacam(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {

            private final int COST_AP_TRUNG_NHANH = 1000000000;

            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    player.linhthuegg.sendLinhThuEgg();
                    if (player.linhthuegg.getSecondDone() != 0) {
                        this.createOtherMenu(player, ConstNpc.CAN_NOT_OPEN_EGG, "trứng sẽ nở sau: " + Util.numberToTime(player.linhthuegg.getSecondDone()) + " nữa",
                                "Hủy bỏ\ntrứng", "Ấp nhanh", "Đóng");
                    } else {
                        this.createOtherMenu(player, ConstNpc.CAN_OPEN_EGG, "trứng sẽ nở sau: " + Util.numberToTime(player.linhthuegg.getSecondDone()) + " nữa", "Nở", "Hủy bỏ\ntrứng", "Đóng");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    switch (player.iDMark.getIndexMenu()) {
                        case ConstNpc.CAN_NOT_OPEN_EGG:
                            if (select == 0) {
                                this.createOtherMenu(player, ConstNpc.CONFIRM_DESTROY_EGG,
                                        "Bạn có chắc chắn muốn hủy bỏ trứng linh thú?", "Đồng ý", "Từ chối");
                            } else if (select == 1) {
                                this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                        "Bạn có thể hoàn thành nhiệm vụ hằng ngày tại bò mộng để trứng nhanh nở. tốc độ tùy vào độ khó!",
                                        "Đóng");
//                                if (player.inventory.gold >= COST_AP_TRUNG_NHANH) {
//                                    player.inventory.gold -= COST_AP_TRUNG_NHANH;
//                                    player.linhthuegg.timeDone = 0;
//                                    Service.getInstance().sendMoney(player);
//                                    player.linhthuegg.sendLinhThuEgg();
//                                } else {
                                //       Service.getInstance().sendThongBao(player,
                                //              "Bạn không đủ vàng để thực hiện, còn thiếu "
                                //              + Util.numberToMoney((COST_AP_TRUNG_NHANH - player.inventory.gold)) + " vàng");
                                //     }
                            }
                            break;
                        case ConstNpc.CAN_OPEN_EGG:
                            switch (select) {
                                case 0:
                                    this.createOtherMenu(player, ConstNpc.CONFIRM_OPEN_EGG,
                                            "Bạn có chắc chắn cho trứng nở?\n"
                                            + "Bạn sẽ nhận ngẫu nhiên 1 linh thú và chỉ số!\n" + "Gồm 5 đá ngũ sắc, 99 hồn linh thú!",
                                            "Đồng ý", "Từ chối");
                                    break;
                                case 1:
                                    this.createOtherMenu(player, ConstNpc.CONFIRM_DESTROY_EGG,
                                            "Bạn có chắc chắn muốn hủy bỏ trứng linh thú?", "Đồng ý", "Từ chối");
                                    break;
                            }
                            break;
                        case ConstNpc.CONFIRM_OPEN_EGG:
                            switch (select) {
                                case 0:
                                    player.linhthuegg.openEgg(player);
                                    break;
//                                case 1:
//                                    player.mabuEgg.openEgg(ConstPlayer.NAMEC);
//                                    break;
//                                case 2:
//                                    player.mabuEgg.openEgg(ConstPlayer.XAYDA);
//                                    break;
//                                default:
//                                    break;
                            }
                            break;
                        case ConstNpc.CONFIRM_DESTROY_EGG:
                            if (select == 0) {
                                player.linhthuegg.destroyEgg();
                            }
                            break;
                    }
                }
            }
        };
    }

    public static Npc quaTrung(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {

            private final int COST_AP_TRUNG_NHANH = 1000000000;

            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    player.mabuEgg.sendMabuEgg();
                    if (player.mabuEgg.getSecondDone() != 0) {
                        this.createOtherMenu(player, ConstNpc.CAN_NOT_OPEN_EGG, "Bư bư bư...",
                                "Hủy bỏ\ntrứng", "Ấp nhanh\n" + Util.numberToMoney(COST_AP_TRUNG_NHANH) + " vàng", "Đóng");
                    } else {
                        this.createOtherMenu(player, ConstNpc.CAN_OPEN_EGG, "Bư bư bư...", "Nở", "Hủy bỏ\ntrứng", "Đóng");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    switch (player.iDMark.getIndexMenu()) {
                        case ConstNpc.CAN_NOT_OPEN_EGG:
                            if (select == 0) {
                                this.createOtherMenu(player, ConstNpc.CONFIRM_DESTROY_EGG,
                                        "Bạn có chắc chắn muốn hủy bỏ trứng Mabư?", "Đồng ý", "Từ chối");
                            } else if (select == 1) {
                                if (player.inventory.gold >= COST_AP_TRUNG_NHANH) {
                                    player.inventory.gold -= COST_AP_TRUNG_NHANH;
                                    player.mabuEgg.timeDone = 0;
                                    Service.getInstance().sendMoney(player);
                                    player.mabuEgg.sendMabuEgg();
                                } else {
                                    Service.getInstance().sendThongBao(player,
                                            "Bạn không đủ vàng để thực hiện, còn thiếu "
                                            + Util.numberToMoney((COST_AP_TRUNG_NHANH - player.inventory.gold)) + " vàng");
                                }
                            }
                            break;
                        case ConstNpc.CAN_OPEN_EGG:
                            switch (select) {
                                case 0:
                                    this.createOtherMenu(player, ConstNpc.CONFIRM_OPEN_EGG,
                                            "Bạn có chắc chắn cho trứng nở?\n"
                                            + "Đệ tử của bạn sẽ được thay thế bằng đệ Mabư",
                                            "Đệ mabư\nTrái Đất", "Đệ mabư\nNamếc", "Đệ mabư\nXayda", "Từ chối");
                                    break;
                                case 1:
                                    this.createOtherMenu(player, ConstNpc.CONFIRM_DESTROY_EGG,
                                            "Bạn có chắc chắn muốn hủy bỏ trứng Mabư?", "Đồng ý", "Từ chối");
                                    break;
                            }
                            break;
                        case ConstNpc.CONFIRM_OPEN_EGG:
                            switch (select) {
                                case 0:
                                    player.mabuEgg.openEgg(ConstPlayer.TRAI_DAT);
                                    break;
                                case 1:
                                    player.mabuEgg.openEgg(ConstPlayer.NAMEC);
                                    break;
                                case 2:
                                    player.mabuEgg.openEgg(ConstPlayer.XAYDA);
                                    break;
                                default:
                                    break;
                            }
                            break;
                        case ConstNpc.CONFIRM_DESTROY_EGG:
                            if (select == 0) {
                                player.mabuEgg.destroyEgg();
                            }
                            break;
                    }
                }
            }
        };
    }

    public static Npc quocVuong(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {

            @Override
            public void openBaseMenu(Player player) {
                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                        "Con muốn nâng giới hạn sức mạnh cho bản thân hay đệ tử?",
                        "Bản thân", "Đệ tử", "Từ chối");
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0:
                                if (player.nPoint.limitPower < NPoint.MAX_LIMIT) {
                                    this.createOtherMenu(player, ConstNpc.OPEN_POWER_MYSEFT,
                                            "Ta sẽ truền năng lượng giúp con mở giới hạn sức mạnh của bản thân lên "
                                            + Util.numberToMoney(player.nPoint.getPowerNextLimit()),
                                            "Nâng\ngiới hạn\nsức mạnh",
                                            "Nâng ngay\n" + Util.numberToMoney(OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER) + " vàng", "Đóng");
                                } else {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                            "Sức mạnh của con đã đạt tới giới hạn",
                                            "Đóng");
                                }
                                break;
                            case 1:
                                if (player.pet != null) {
                                    if (player.pet.nPoint.limitPower < NPoint.MAX_LIMIT) {
                                        this.createOtherMenu(player, ConstNpc.OPEN_POWER_PET,
                                                "Ta sẽ truền năng lượng giúp con mở giới hạn sức mạnh của đệ tử lên "
                                                + Util.numberToMoney(player.pet.nPoint.getPowerNextLimit()),
                                                "Nâng ngay\n" + Util.numberToMoney(OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER) + " vàng", "Đóng");
                                    } else {
                                        this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                                "Sức mạnh của đệ con đã đạt tới giới hạn",
                                                "Đóng");
                                    }
                                } else {
                                    Service.getInstance().sendThongBao(player, "Không thể thực hiện");
                                }
                                //giới hạn đệ tử
                                break;
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.OPEN_POWER_MYSEFT) {
                        switch (select) {
                            case 0:
                                OpenPowerService.gI().openPowerBasic(player);
                                break;
                            case 1:
                                if (player.inventory.gold >= OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER) {
                                    if (OpenPowerService.gI().openPowerSpeed(player)) {
                                        player.inventory.gold -= OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER;
                                        Service.getInstance().sendMoney(player);
                                    }
                                } else {
                                    Service.getInstance().sendThongBao(player,
                                            "Bạn không đủ vàng để mở, còn thiếu "
                                            + Util.numberToMoney((OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER - player.inventory.gold)) + " vàng");
                                }
                                break;
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.OPEN_POWER_PET) {
                        if (select == 0) {
                            if (player.inventory.gold >= OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER) {
                                if (OpenPowerService.gI().openPowerSpeed(player.pet)) {
                                    player.inventory.gold -= OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER;
                                    Service.getInstance().sendMoney(player);
                                }
                            } else {
                                Service.getInstance().sendThongBao(player,
                                        "Bạn không đủ vàng để mở, còn thiếu "
                                        + Util.numberToMoney((OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER - player.inventory.gold)) + " vàng");
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc bulmaTL(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {

                    switch (this.mapId) {
                        case 102:
                            if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                                this.createOtherMenu(player, ConstNpc.BASE_MENU, "Cậu bé muốn mua gì nào?", "Cửa hàng", "Đóng");
                            }
                            break;
                        case 201:
                            this.createOtherMenu(player, ConstNpc.BASE_MENU, "Cậu bé muốn mua gì nào?", "Cửa hàng", "chi tiết");
                            break;
                        case 202:
                            this.createOtherMenu(player, ConstNpc.BASE_MENU, "Cậu bé muốn mua gì nào?", "Cửa hàng", "chi tiết");
                            break;
                        case 203:
                            this.createOtherMenu(player, ConstNpc.BASE_MENU, "Cậu bé muốn mua gì nào?", "Cửa hàng", "chi tiết");
                            break;
                    }

                }
            }

            @Override
            public void confirmMenu(Player player, int select
            ) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (this.mapId) {
                            case 102:
                                if (select == 0) {
                                    ShopServiceNew.gI().opendShop(player, "BUNMA_FUTURE", true);
                                    break;
                                }
                                break;
                            case 201:
                                if (select == 0) {
                                    ShopServiceNew.gI().opendShop(player, "BUNMA_PETITEM", true);
                                    break;
                                } else if (select == 1) {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                            "Nếu bạn nhặt được 1 quả trứng hắc ám nó sẽ xuất hiện ở đây. trứng sẽ rơi ở boss tương lai. boss fide. Đã ngũ sắc sẽ rớt tương tự!", "Đóng");
                                }
                                break;
                            case 202:
                                if (select == 0) {
                                    ShopServiceNew.gI().opendShop(player, "BUNMA_PETITEM", true);
                                } else if (select == 1) {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                            "Nếu bạn nhặt được 1 quả trứng hắc ám nó sẽ xuất hiện ở đây. trứng sẽ rơi ở boss tương lai. boss fide. Đã ngũ sắc sẽ rớt tương tự!", "Đóng");
                                }
                                break;
                            case 203:
                                if (select == 0) {
                                    ShopServiceNew.gI().opendShop(player, "BUNMA_PETITEM", true);
                                    break;
                                } else if (select == 1) {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                            "Nếu bạn nhặt được 1 quả trứng hắc ám nó sẽ xuất hiện ở đây. trứng sẽ rơi ở boss tương lai. boss fide. Đã ngũ sắc sẽ rớt tương tự!", "Đóng");
                                }
                                break;

                        }
                    }
                }
            }
        };
    }

    public static Npc rongOmega(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    BlackBallWar.gI().setTime();
                    if (this.mapId == 24 || this.mapId == 25 || this.mapId == 26) {
                        try {
                            long now = System.currentTimeMillis();
                            if (now > BlackBallWar.TIME_OPEN && now < BlackBallWar.TIME_CLOSE) {
                                this.createOtherMenu(player, ConstNpc.MENU_OPEN_BDW, "Đường đến với ngọc rồng sao đen đã mở, "
                                        + "ngươi có muốn tham gia không?",
                                        "Hướng dẫn\nthêm", "Tham gia", "Từ chối");
                            } else {
                                String[] optionRewards = new String[7];
                                int index = 0;
                                for (int i = 0; i < 7; i++) {
                                    if (player.rewardBlackBall.timeOutOfDateReward[i] > System.currentTimeMillis()) {
                                        String quantily = player.rewardBlackBall.quantilyBlackBall[i] > 1 ? "x" + player.rewardBlackBall.quantilyBlackBall[i] + " " : "";
                                        optionRewards[index] = quantily + (i + 1) + " sao";
                                        index++;
                                    }
                                }
                                if (index != 0) {
                                    String[] options = new String[index + 1];
                                    for (int i = 0; i < index; i++) {
                                        options[i] = optionRewards[i];
                                    }
                                    options[options.length - 1] = "Từ chối";
                                    this.createOtherMenu(player, ConstNpc.MENU_REWARD_BDW, "Ngươi có một vài phần thưởng ngọc "
                                            + "rồng sao đen đây!",
                                            options);
                                } else {
                                    this.createOtherMenu(player, ConstNpc.MENU_NOT_OPEN_BDW,
                                            "Ta có thể giúp gì cho ngươi?", "Hướng dẫn", "Từ chối");
                                }
                            }
                        } catch (Exception ex) {
                            Logger.error("Lỗi mở menu rồng Omega");
                        }
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    switch (player.iDMark.getIndexMenu()) {
                        case ConstNpc.MENU_REWARD_BDW:
                            player.rewardBlackBall.getRewardSelect((byte) select);
                            break;
                        case ConstNpc.MENU_OPEN_BDW:
                            if (select == 0) {
                                NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_BLACK_BALL_WAR);
                            } else if (select == 1) {
//                                if (!player.getSession().actived) {
//                                    Service.getInstance().sendThongBao(player, "Vui lòng kích hoạt tài khoản để sử dụng chức năng này");
//
//                                } else
                                player.iDMark.setTypeChangeMap(ConstMap.CHANGE_BLACK_BALL);
                                ChangeMapService.gI().openChangeMapTab(player);
                            }
                            break;
                        case ConstNpc.MENU_NOT_OPEN_BDW:
                            if (select == 0) {
                                NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_BLACK_BALL_WAR);
                            }
                            break;
                    }
                }
            }

        };
    }

    public static Npc rong1_to_7s(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isHoldBlackBall()) {
                        this.createOtherMenu(player, ConstNpc.MENU_PHU_HP, "Ta có thể giúp gì cho ngươi?", "Phù hộ", "Từ chối");
                    } else {
                        if (BossManager.gI().existBossOnPlayer(player)
                                || player.zone.items.stream().anyMatch(itemMap -> ItemMapService.gI().isBlackBall(itemMap.itemTemplate.id))
                                || player.zone.getPlayers().stream().anyMatch(p -> p.iDMark.isHoldBlackBall())) {
                            this.createOtherMenu(player, ConstNpc.MENU_OPTION_GO_HOME, "Ta có thể giúp gì cho ngươi?", "Về nhà", "Từ chối");
                        } else {
                            this.createOtherMenu(player, ConstNpc.MENU_OPTION_GO_HOME, "Ta có thể giúp gì cho ngươi?", "Về nhà", "Từ chối", "Gọi BOSS");
                        }
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.getIndexMenu() == ConstNpc.MENU_PHU_HP) {
                        if (select == 0) {
                            this.createOtherMenu(player, ConstNpc.MENU_OPTION_PHU_HP,
                                    "Ta sẽ giúp ngươi tăng HP lên mức kinh hoàng, ngươi chọn đi",
                                    "x3 HP\n" + Util.numberToMoney(BlackBallWar.COST_X3) + " vàng",
                                    "x5 HP\n" + Util.numberToMoney(BlackBallWar.COST_X5) + " vàng",
                                    "x7 HP\n" + Util.numberToMoney(BlackBallWar.COST_X7) + " vàng",
                                    "Từ chối"
                            );
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPTION_GO_HOME) {
                        if (select == 0) {
                            ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, -1, 250);
                        } else if (select == 2) {
                            BossManager.gI().callBoss(player, mapId);
                        } else if (select == 1) {
                            this.npcChat(player, "Để ta xem ngươi trụ được bao lâu");
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPTION_PHU_HP) {
                        if (player.effectSkin.xHPKI > 1) {
                            Service.getInstance().sendThongBao(player, "Bạn đã được phù hộ rồi!");
                            return;
                        }
                        switch (select) {
                            case 0:
                                BlackBallWar.gI().xHPKI(player, BlackBallWar.X3);
                                break;
                            case 1:
                                BlackBallWar.gI().xHPKI(player, BlackBallWar.X5);
                                break;
                            case 2:
                                BlackBallWar.gI().xHPKI(player, BlackBallWar.X7);
                                break;
                            case 3:
                                this.npcChat(player, "Để ta xem ngươi trụ được bao lâu");
                                break;
                        }
                    }
                }
            }
        };
    }

    public static Npc npcThienSu64(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (this.mapId == 14) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta sẽ dẫn cậu tới hành tinh Berrus với điều kiện\n 2. đạt 80 tỷ sức mạnh "
                            + "\n 3. chi phí vào cổng  50 triệu vàng", "Tới ngay", "Từ chối");
                }
                if (this.mapId == 7) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta sẽ dẫn cậu tới hành tinh Berrus với điều kiện\n 2. đạt 80 tỷ sức mạnh "
                            + "\n 3. chi phí vào cổng  50 triệu vàng", "Tới ngay", "Từ chối");
                }
                if (this.mapId == 0) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta sẽ dẫn cậu tới hành tinh Berrus với điều kiện\n 2. đạt 80 tỷ sức mạnh "
                            + "\n 3. chi phí vào cổng  50 triệu vàng", "Tới ngay", "Từ chối");
                }
                if (this.mapId == 146) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Cậu không chịu nổi khi ở đây sao?\nCậu sẽ khó mà mạnh lên được", "Trốn về", "Ở lại");
                }
                if (this.mapId == 147) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Cậu không chịu nổi khi ở đây sao?\nCậu sẽ khó mà mạnh lên được", "Trốn về", "Ở lại");
                }
                if (this.mapId == 148) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Cậu không chịu nổi khi ở đây sao?\nCậu sẽ khó mà mạnh lên được", "Trốn về", "Ở lại");
                }
                if (this.mapId == 48) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Đã tìm đủ nguyên liệu cho tôi chưa?\n Tôi sẽ giúp cậu mạnh lên kha khá đấy!", "Hướng Dẫn",
                            "Đổi SKH VIP", "Từ Chối");
                }
            }

            //if (player.inventory.gold < 500000000) {
//                this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hết tiền rồi\nẢo ít thôi con", "Đóng");
//                return;
//            }
            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu() && this.mapId == 7) {
                        if (select == 0) {
                            if (player.getSession().player.nPoint.power >= 80000000000L && player.inventory.gold > COST_HD) {
                                player.inventory.gold -= COST_HD;
                                Service.getInstance().sendMoney(player);
                                ChangeMapService.gI().changeMapBySpaceShip(player, 146, -1, 168);
                            } else {
                                this.npcChat(player, "Bạn chưa đủ điều kiện để vào");
                            }
                        }
                        if (select == 1) {
                        }
                    }
                    if (player.iDMark.isBaseMenu() && this.mapId == 14) {
                        if (select == 0) {
                            if (player.getSession().player.nPoint.power >= 80000000000L && player.inventory.gold > COST_HD) {
                                player.inventory.gold -= COST_HD;
                                Service.getInstance().sendMoney(player);
                                ChangeMapService.gI().changeMapBySpaceShip(player, 148, -1, 168);
                            } else {
                                this.npcChat(player, "Bạn chưa đủ điều kiện để vào");
                            }
                        }
                        if (select == 1) {
                        }
                    }
                    if (player.iDMark.isBaseMenu() && this.mapId == 0) {
                        if (select == 0) {
                            if (player.getSession().player.nPoint.power >= 80000000000L && player.inventory.gold > COST_HD) {
                                player.inventory.gold -= COST_HD;
                                Service.getInstance().sendMoney(player);
                                ChangeMapService.gI().changeMapBySpaceShip(player, 147, -1, 168);
                            } else {
                                this.npcChat(player, "Bạn chưa đủ điều kiện để vào");
                            }
                        }
                        if (select == 1) {
                        }
                    }
                    if (player.iDMark.isBaseMenu() && this.mapId == 147) {
                        if (select == 0) {
                            ChangeMapService.gI().changeMapBySpaceShip(player, 0, -1, 450);
                        }
                        if (select == 1) {
                        }
                    }
                    if (player.iDMark.isBaseMenu() && this.mapId == 148) {
                        if (select == 0) {
                            ChangeMapService.gI().changeMapBySpaceShip(player, 14, -1, 450);
                        }
                        if (select == 1) {
                        }
                    }
                    if (player.iDMark.isBaseMenu() && this.mapId == 146) {
                        if (select == 0) {
                            ChangeMapService.gI().changeMapBySpaceShip(player, 7, -1, 450);
                        }
                        if (select == 1) {
                        }

                    }
                    if (player.iDMark.isBaseMenu() && this.mapId == 48) {
                        if (select == 0) {
                            NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_DOI_SKH_VIP);
                        }
                        if (select == 1) {
                            CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_SKH_VIP);
                        }

                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_NANG_DOI_SKH_VIP) {
                        if (select == 0) {
                            CombineServiceNew.gI().startCombine(player);
                        }
                    }
                }
            }

        };
    }

    //    public static Npc bill(int mapId, int status, int cx, int cy, int tempId, int avartar) {
//        return new Npc(mapId, status, cx, cy, tempId, avartar) {
//            @Override
//            public void openBaseMenu(Player player) {
//                if (canOpenNpc(player)) {
//                    if (this.mapId == 48) {
//                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ngươi muốn gì nào?" + player.inventory.coupon+, "Đóng");
//                    } else {
//                        super.openBaseMenu(player);
//                    }
//                }
//            }
//
//            @Override
//            public void confirmMenu(Player player, int select) {
//                if (canOpenNpc(player)) {
//                    switch (this.mapId) {
//                        case 48:
//                            switch (player.iDMark.getIndexMenu()) {
//                                case ConstNpc.BASE_MENU:
//                                    if (select == 0) {
//
//                                    }
//                                    break;
//                            }
//                            break;
//                    }
//                }
//            }
//        };
//    }
    public static Npc bill(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player) && this.mapId == 48) {
                    createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Ngươi muốn gì nào?",
                            "Xem Điểm ", "SHOP HỦY DIỆT", "Top Sức Mạnh", "Top Nhiệm Vụ", "Đóng");
                } else if(canOpenNpc(player) && this.mapId == 14) {
                    createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Nhanh tay nhận quà tân thủ duy nhấn từ 18/3 đến 20/3?",
                            "Top Sức Mạnh", "Top Nhiệm Vụ", "Nhận quà\ntân thủ");
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    switch (this.mapId) {
                        case 48:
                            switch (player.iDMark.getIndexMenu()) {
                                case ConstNpc.BASE_MENU:
                                    if (select == 0) {
                                        createOtherMenu(player, ConstNpc.IGNORE_MENU, "Ngươi đang có: " + player.inventory.coupon + " điểm", "Đóng");
                                        break;
                                    }
                                    if (select == 1) {
                                        ShopServiceNew.gI().opendShop(player, "BILL", false);
                                        break;

                                    }
                                    if (select == 2) {
                                        Util.showListTop(player, (byte) 0);
                                        break;
                                    }
                                    if (select == 3) {
                                        Util.showListTop(player, (byte) 1);
                                        break;
                                    }
                                    break;
                            }
                            break;
                        case 14:
                            switch (player.iDMark.getIndexMenu()) {
                                case ConstNpc.BASE_MENU:
                                    Item thoivang = ItemService.gI().createNewItem((short) (457));
                                    if (select == 0) {
                                        Util.showListTop(player, (byte) 0);
                                        break;
                                    }
                                    if (select == 1) {
                                        Util.showListTop(player, (byte) 1);
                                        break;
                                    }
                                    if (select == 2) {
                                        if (player.getSession().is_gift_box == false) {
                                            thoivang.quantity = 30;
                                            InventoryServiceNew.gI().addItemBag(player, thoivang);
                                            InventoryServiceNew.gI().sendItemBags(player);
                                            player.getSession().is_gift_box = true;

                                            PlayerDAO.subVnd(player, 0);
                                            Service.getInstance().sendThongBao(player, "bạn vừa nhận được 30 " + thoivang.template.name);
                                            break;
                                        }
                                    }
                                    break;
                            }
                            break;

                    }
                }
            }
        };
    }

    public static Npc boMong(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 47 || this.mapId == 84) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Xin chào, cậu muốn tôi giúp gì?", "Nhiệm vụ\nhàng ngày", "Từ chối");
                    }
//                    if (this.mapId == 47) {
//                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
//                                "Xin chào, cậu muốn tôi giúp gì?", "Từ chối");
//                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 47) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    if (player.playerTask.sideTask.template != null) {
                                        String npcSay = "Nhiệm vụ hiện tại: " + player.playerTask.sideTask.getName() + " ("
                                                + player.playerTask.sideTask.getLevel() + ")"
                                                + "\nHiện tại đã hoàn thành: " + player.playerTask.sideTask.count + "/"
                                                + player.playerTask.sideTask.maxCount + " ("
                                                + player.playerTask.sideTask.getPercentProcess() + "%)\nSố nhiệm vụ còn lại trong ngày: "
                                                + player.playerTask.sideTask.leftTask + "/" + ConstTask.MAX_SIDE_TASK;
                                        this.createOtherMenu(player, ConstNpc.MENU_OPTION_PAY_SIDE_TASK,
                                                npcSay, "Trả nhiệm\nvụ", "Hủy nhiệm\nvụ");
                                    } else {
                                        this.createOtherMenu(player, ConstNpc.MENU_OPTION_LEVEL_SIDE_TASK,
                                                "Tôi có vài nhiệm vụ theo cấp bậc, "
                                                + "sức cậu có thể làm được cái nào?",
                                                "Dễ", "Bình thường", "Khó", "Siêu khó", "Địa ngục", "Từ chối");
                                    }
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPTION_LEVEL_SIDE_TASK) {
                            switch (select) {
                                case 0:
                                case 1:
                                case 2:
                                case 3:
                                case 4:
                                    TaskService.gI().changeSideTask(player, (byte) select);
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPTION_PAY_SIDE_TASK) {
                            switch (select) {
                                case 0:
                                    TaskService.gI().paySideTask(player);
                                    break;
                                case 1:
                                    TaskService.gI().removeSideTask(player);
                                    break;
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc gokuSSJ_1(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 80) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Xin chào, tôi có thể giúp gì cho cậu?",
                                "Tới hành tinh\nThực vật", "Tới hành tinh\nYardart", "Từ chối");
                    } else if (this.mapId == 131) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Xin chào, tôi có thể giúp gì cho cậu?",
                                "Quay về", "Từ chối");
                    } else {
                        super.openBaseMenu(player);
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    switch (player.iDMark.getIndexMenu()) {
                        case ConstNpc.BASE_MENU:
                            if (this.mapId == 80) {
                                if (select == 0) {
                                    if (TaskService.gI().getIdTask(player) >= ConstTask.TASK_24_0) {
                                        ChangeMapService.gI().changeMapBySpaceShip(player, 160, -1, 168);
                                    } else {
                                        this.npcChat(player, "Xin lỗi, tôi chưa thể đưa cậu tới nơi đó lúc này...");
                                    }
                                } else if (select == 1) {
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 131, -1, 940);
                                }
                            } else if (this.mapId == 131) {
                                if (select == 0) {
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 80, -1, 870);
                                }
                            }
                            break;
                    }
                }
            }
        };
    }

    public static Npc gokuSSJ_2(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 133) {
                        try {
                            Item biKiep = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 590);
                            int soLuong = 0;
                            if (biKiep != null) {
                                soLuong = biKiep.quantity;
                            }
                            if (soLuong >= 10000) {
                                this.createOtherMenu(player, ConstNpc.BASE_MENU, "Bạn đang có " + soLuong + " bí kiếp.\n"
                                        + "Hãy kiếm đủ 10000 bí kiếp tôi sẽ dạy bạn cách dịch chuyển tức thời của người Yardart", "Học dịch\nchuyển", "Đóng");
                            } else {
                                this.createOtherMenu(player, ConstNpc.BASE_MENU, "Bạn đang có " + soLuong + " bí kiếp.\n"
                                        + "Hãy kiếm đủ 10000 bí kiếp tôi sẽ dạy bạn cách dịch chuyển tức thời của người Yardart", "Đóng");
                            }
                        } catch (Exception ex) {

                        }
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 133) {
                        try {
                            Item biKiep = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 590);
                            int soLuong = 0;
                            if (biKiep != null) {
                                soLuong = biKiep.quantity;
                            }
                            if (soLuong >= 10000 && InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
                                Item yardart = ItemService.gI().createNewItem((short) (player.gender + 592));
                                yardart.itemOptions.add(new Item.ItemOption(47, 400));
                                yardart.itemOptions.add(new Item.ItemOption(108, 10));
                                InventoryServiceNew.gI().addItemBag(player, yardart);
                                InventoryServiceNew.gI().subQuantityItemsBag(player, biKiep, 10000);
                                InventoryServiceNew.gI().sendItemBags(player);
                                Service.getInstance().sendThongBao(player, "Bạn vừa nhận được trang phục tộc Yardart");
                            }
                        } catch (Exception ex) {

                        }
                    }
                }
            }
        };
    }

    public static Npc createNPC(int mapId, int status, int cx, int cy, int tempId) {
        int avatar = Manager.NPC_TEMPLATES.get(tempId).avatar;
        try {
            switch (tempId) {
                case ConstNpc.BUNMA_TOC_NAU:
                    return bunmatocnau(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.NGO_KHONG:
                    return ngokhong(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.TRUNG_LINH_THU:
                    return quaTrunghacam(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.DUONG_TANG:
                    return duongtang(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.POTAGE:
                    return poTaGe(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.QUY_LAO_KAME:
                    return quyLaoKame(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.TRUONG_LAO_GURU:
                    return truongLaoGuru(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.VUA_VEGETA:
                    return vuaVegeta(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.ONG_GOHAN:
                case ConstNpc.ONG_MOORI:
                case ConstNpc.ONG_PARAGUS:
                    return ongGohan_ongMoori_ongParagus(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.BUNMA:
                    return bulmaQK(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.DENDE:
                    return dende(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.APPULE:
                    return appule(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.DR_DRIEF:
                    return drDrief(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.CARGO:
                    return cargo(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.CUI:
                    return cui(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.SANTA:
                    return santa(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.URON:
                    return uron(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.BA_HAT_MIT:
                    return baHatMit(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.RUONG_DO:
                    return ruongDo(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.DAU_THAN:
                    return dauThan(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.CALICK:
                    return calick(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.JACO:
                    return jaco(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.THUONG_DE:
                    return thuongDe(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.VADOS:
                    return vados(mapId, status, cx, cy, tempId, avatar);
//                case ConstNpc.POTAGE:
//                    return Potage(mapId, status, cx, cy, tempId, avatar);    
                case ConstNpc.THAN_VU_TRU:
                    return thanVuTru(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.KIBIT:
                    return kibit(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.OSIN:
                    return osin(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.LY_TIEU_NUONG:
                    return npclytieunuong54(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.LINH_CANH:
                    return linhCanh(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.QUA_TRUNG:
                    return quaTrung(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.QUOC_VUONG:
                    return quocVuong(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.BUNMA_TL:
                    return bulmaTL(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.RONG_OMEGA:
                    return rongOmega(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.RONG_1S:
                case ConstNpc.RONG_2S:
                case ConstNpc.RONG_3S:
                case ConstNpc.RONG_4S:
                case ConstNpc.RONG_5S:
                case ConstNpc.RONG_6S:
                case ConstNpc.RONG_7S:
                    return rong1_to_7s(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.NPC_64:
                    return npcThienSu64(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.BILL:
                    return bill(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.BO_MONG:
                    return boMong(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.GOKU_SSJ:
                    return gokuSSJ_1(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.GOKU_SSJ_:
                    return gokuSSJ_2(mapId, status, cx, cy, tempId, avatar);
                default:
                    return new Npc(mapId, status, cx, cy, tempId, avatar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                super.openBaseMenu(player);
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
//                                ShopService.gI().openShopNormal(player, this, ConstNpc.SHOP_BUNMA_TL_0, 0, player.gender);
                            }
                        }
                    };
            }
        } catch (Exception e) {
            Logger.logException(NpcFactory.class,
                    e, "Lỗi load npc");
            return null;
        }
    }

    //girlkun75-mark
    public static void createNpcRongThieng() {
        Npc npc = new Npc(-1, -1, -1, -1, ConstNpc.RONG_THIENG, -1) {
            @Override
            public void confirmMenu(Player player, int select) {
                switch (player.iDMark.getIndexMenu()) {
                    case ConstNpc.IGNORE_MENU:

                        break;
                    case ConstNpc.SHENRON_CONFIRM:
                        if (select == 0) {
                            SummonDragon.gI().confirmWish();
                        } else if (select == 1) {
                            SummonDragon.gI().reOpenShenronWishes(player);
                        }
                        break;
                    case ConstNpc.SHENRON_1_1:
                        if (player.iDMark.getIndexMenu() == ConstNpc.SHENRON_1_1 && select == SHENRON_1_STAR_WISHES_1.length - 1) {
                            NpcService.gI().createMenuRongThieng(player, ConstNpc.SHENRON_1_2, SHENRON_SAY, SHENRON_1_STAR_WISHES_2);
                            break;
                        }
                    case ConstNpc.SHENRON_1_2:
                        if (player.iDMark.getIndexMenu() == ConstNpc.SHENRON_1_2 && select == SHENRON_1_STAR_WISHES_2.length - 1) {
                            NpcService.gI().createMenuRongThieng(player, ConstNpc.SHENRON_1_1, SHENRON_SAY, SHENRON_1_STAR_WISHES_1);
                            break;
                        }
                    default:
                        SummonDragon.gI().showConfirmShenron(player, player.iDMark.getIndexMenu(), (byte) select);
                        break;
                }
            }
        };
    }

    public static void createNpcConMeo() {
        Npc npc = new Npc(-1, -1, -1, -1, ConstNpc.CON_MEO, 351) {
            @Override
            public void confirmMenu(Player player, int select) {
                switch (player.iDMark.getIndexMenu()) {
                    case ConstNpc.IGNORE_MENU:

                        break;
                    case ConstNpc.MAKE_MATCH_PVP: //                        if (player.getSession().actived) 
                    {
                        if (Maintenance.isRuning) {
                            break;
                        }
                        PVPService.gI().sendInvitePVP(player, (byte) select);
                        break;
                    }
//                        else {
//                            Service.getInstance().sendThongBao(player, "|5|VUI LÒNG KÍCH HOẠT TÀI KHOẢN TẠI\n|7|NROGOD.COM\n|5|ĐỂ MỞ KHÓA TÍNH NĂNG");
//                            break;
//                        }
                    case ConstNpc.MAKE_FRIEND:
                        if (select == 0) {
                            Object playerId = PLAYERID_OBJECT.get(player.id);
                            if (playerId != null) {
                                FriendAndEnemyService.gI().acceptMakeFriend(player,
                                        Integer.parseInt(String.valueOf(playerId)));
                            }
                        }
                        break;
                    case ConstNpc.REVENGE:
                        if (select == 0) {
                            PVPService.gI().acceptRevenge(player);
                        }
                        break;
                    case ConstNpc.TUTORIAL_SUMMON_DRAGON:
                        if (select == 0) {
                            NpcService.gI().createTutorial(player, -1, SummonDragon.SUMMON_SHENRON_TUTORIAL);
                        }
                        break;
                    case ConstNpc.SUMMON_SHENRON:
                        if (select == 0) {
                            NpcService.gI().createTutorial(player, -1, SummonDragon.SUMMON_SHENRON_TUTORIAL);
                        } else if (select == 1) {
                            SummonDragon.gI().summonShenron(player);
                        }
                        break;
                    case ConstNpc.MENU_OPTION_USE_ITEM1105:
                        if (select == 0) {
                            IntrinsicService.gI().sattd(player);
                        } else if (select == 1) {
                            IntrinsicService.gI().satnm(player);
                        } else if (select == 2) {
                            IntrinsicService.gI().setxd(player);
                        }
                        break;
                    case ConstNpc.MENU_OPTION_USE_ITEM2000:
                    case ConstNpc.MENU_OPTION_USE_ITEM2001:
                    case ConstNpc.MENU_OPTION_USE_ITEM2002:
                        try {
                        ItemService.gI().OpenSKH(player, player.iDMark.getIndexMenu(), select);
                    } catch (Exception e) {
                        Logger.error("Lỗi mở hộp quà");
                    }
                    break;
                    case ConstNpc.MENU_OPTION_USE_ITEM2003:
                    case ConstNpc.MENU_OPTION_USE_ITEM2004:
                    case ConstNpc.MENU_OPTION_USE_ITEM2005:
                        try {
                        ItemService.gI().OpenDHD(player, player.iDMark.getIndexMenu(), select);
                    } catch (Exception e) {
                        Logger.error("Lỗi mở hộp quà");
                    }
                    break;
                    case ConstNpc.MENU_OPTION_USE_ITEM736:
                        try {
                        ItemService.gI().OpenDHD(player, player.iDMark.getIndexMenu(), select);
                    } catch (Exception e) {
                        Logger.error("Lỗi mở hộp quà");
                    }
                    break;
                    case ConstNpc.INTRINSIC:
                        if (select == 0) {
                            IntrinsicService.gI().showAllIntrinsic(player);
                        } else if (select == 1) {
                            IntrinsicService.gI().showConfirmOpen(player);
                        } else if (select == 2) {
                            IntrinsicService.gI().showConfirmOpenVip(player);
                        }
                        break;
                    case ConstNpc.CONFIRM_OPEN_INTRINSIC:
                        if (select == 0) {
                            IntrinsicService.gI().open(player);
                        }
                        break;
                    case ConstNpc.CONFIRM_OPEN_INTRINSIC_VIP:
                        if (select == 0) {
                            IntrinsicService.gI().openVip(player);
                        }
                        break;
                    case ConstNpc.CONFIRM_LEAVE_CLAN:
                        if (select == 0) {
                            ClanService.gI().leaveClan(player);
                        }
                        break;
                    case ConstNpc.CONFIRM_NHUONG_PC:
                        if (select == 0) {
                            ClanService.gI().phongPc(player, (int) PLAYERID_OBJECT.get(player.id));
                        }
                        break;
                    case ConstNpc.BAN_PLAYER:
                        if (select == 0) {
                            PlayerService.gI().banPlayer((Player) PLAYERID_OBJECT.get(player.id));
                            Service.getInstance().sendThongBao(player, "Ban người chơi " + ((Player) PLAYERID_OBJECT.get(player.id)).name + " thành công");
                        }
                        break;

                    case ConstNpc.BUFF_PET:
                        if (select == 0) {
                            Player pl = (Player) PLAYERID_OBJECT.get(player.id);
                            if (pl.pet == null) {
                                PetService.gI().createNormalPet(pl);
                                Service.getInstance().sendThongBao(player, "Phát đệ tử cho " + ((Player) PLAYERID_OBJECT.get(player.id)).name + " thành công");
                            }
                        }
                        break;
                    case ConstNpc.MENU_ADMIN:
                        switch (select) {
                            case 0:
                                for (int i = 14; i <= 20; i++) {
                                    Item item = ItemService.gI().createNewItem((short) i);
                                    InventoryServiceNew.gI().addItemBag(player, item);
                                }
                                InventoryServiceNew.gI().sendItemBags(player);
                                break;
                            case 1:
                                if (player.pet == null) {
                                    PetService.gI().createNormalPet(player);
                                } else {
                                    if (player.pet.typePet == 1) {
                                        PetService.gI().changeNormalPet(player);
                                    } else {
                                        PetService.gI().changeMabuPet(player);
                                    }
                                }
                                break;
                            case 2:
                                if (player.isAdmin()) {
                                    System.out.println(player.name);
//                                PlayerService.gI().baoTri();
                                    Maintenance.gI().start(15);
                                    System.out.println(player.name);
                                }
                                break;
                            case 3:
                                Input.gI().createFormFindPlayer(player);
                                break;
                            case 4:
                                BossManager.gI().showListBoss(player);
                                break;
                        }
                        break;

                    case ConstNpc.menutd:
                        switch (select) {
                            case 0:
                           try {
                                ItemService.gI().settaiyoken(player);
                            } catch (Exception e) {
                            }
                            break;
                            case 1:
                                try {
                                ItemService.gI().setgenki(player);
                            } catch (Exception e) {
                            }
                            break;
                            case 2:
                                try {
                                ItemService.gI().setkamejoko(player);
                            } catch (Exception e) {
                            }
                            break;
                        }
                        break;

                    case ConstNpc.menunm:
                        switch (select) {
                            case 0:
                           try {
                                ItemService.gI().setgodki(player);
                            } catch (Exception e) {
                            }
                            break;
                            case 1:
                                try {
                                ItemService.gI().setgoddam(player);
                            } catch (Exception e) {
                            }
                            break;
                            case 2:
                                try {
                                ItemService.gI().setsummon(player);
                            } catch (Exception e) {
                            }
                            break;
                        }
                        break;

                    case ConstNpc.menuxd:
                        switch (select) {
                            case 0:
                           try {
                                ItemService.gI().setgodgalick(player);
                            } catch (Exception e) {
                            }
                            break;
                            case 1:
                                try {
                                ItemService.gI().setmonkey(player);
                            } catch (Exception e) {
                            }
                            break;
                            case 2:
                                try {
                                ItemService.gI().setgodhp(player);
                            } catch (Exception e) {
                            }
                            break;
                        }
                        break;

                    case ConstNpc.CONFIRM_DISSOLUTION_CLAN:
                        switch (select) {
                            case 0:
                                Clan clan = player.clan;
                                clan.deleteDB(clan.id);
                                Manager.CLANS.remove(clan);
                                player.clan = null;
                                player.clanMember = null;
                                ClanService.gI().sendMyClan(player);
                                ClanService.gI().sendClanId(player);
                                Service.getInstance().sendThongBao(player, "Đã giải tán bang hội.");
                                break;
                        }
                        break;
                    case ConstNpc.CONFIRM_ACTIVE:
                        switch (select) {
                            case 0:
                                if (player.getSession().goldBar >= 20) {
                                    player.getSession().actived = true;
                                    if (PlayerDAO.subGoldBar(player, 20)) {
                                        Service.getInstance().sendThongBao(player, "Đã mở thành viên thành công!");
                                        break;
                                    } else {
                                        this.npcChat(player, "Lỗi vui lòng báo admin...");
                                    }
                                }
//                                Service.getInstance().sendThongBao(player, "Bạn không có vàng\n Vui lòng NROGOD.COM để nạp thỏi vàng");
                                break;
                        }
                        break;
                    case ConstNpc.CONFIRM_REMOVE_ALL_ITEM_LUCKY_ROUND:
                        if (select == 0) {
                            for (int i = 0; i < player.inventory.itemsBoxCrackBall.size(); i++) {
                                player.inventory.itemsBoxCrackBall.set(i, ItemService.gI().createItemNull());
                            }
                            player.inventory.itemsBoxCrackBall.clear();
                            Service.getInstance().sendThongBao(player, "Đã xóa hết vật phẩm trong rương");
                        }
                        break;
                    case ConstNpc.MENU_FIND_PLAYER:
                        Player p = (Player) PLAYERID_OBJECT.get(player.id);
                        if (p != null) {
                            switch (select) {
                                case 0:
                                    if (p.zone != null) {
                                        ChangeMapService.gI().changeMapYardrat(player, p.zone, p.location.x, p.location.y);
                                    }
                                    break;
                                case 1:
                                    if (p.zone != null) {
                                        ChangeMapService.gI().changeMap(p, player.zone, player.location.x, player.location.y);
                                    }
                                    break;
                                case 2:
                                    Input.gI().createFormChangeName(player, p);
                                    break;
                                case 3:
                                    String[] selects = new String[]{"Đồng ý", "Hủy"};
                                    NpcService.gI().createMenuConMeo(player, ConstNpc.BAN_PLAYER, -1,
                                            "Bạn có chắc chắn muốn ban " + p.name, selects, p);
                                    break;
                                case 4:
                                    Service.getInstance().sendThongBao(player, "Kik người chơi " + p.name + " thành công");
                                    Client.gI().getPlayers().remove(p);
                                    Client.gI().kickSession(p.getSession());
                                    break;
                            }
                        }
                        break;
                    case ConstNpc.MENU_EVENT:
                        switch (select) {
                            case 0:
                                Service.getInstance().sendThongBaoOK(player, "Điểm sự kiện: " + player.inventory.event + " ngon ngon...");
                                break;
                            case 1:
                                Util.showListTop(player, (byte) 2);
                                break;
                            case 2:
                                Service.getInstance().sendThongBao(player, "Sự kiện đã kết thúc...");
//                                NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_GIAO_BONG, -1, "Người muốn giao bao nhiêu bông...",
//                                        "100 bông", "1000 bông", "10000 bông");
                                break;
                            case 3:
                                Service.getInstance().sendThongBao(player, "Sự kiện đã kết thúc...");
//                                NpcService.gI().createMenuConMeo(player, ConstNpc.CONFIRM_DOI_THUONG_SU_KIEN, -1, "Con có thực sự muốn đổi thưởng?\nPhải giao cho ta 3000 điểm sự kiện đấy... ",
//                                        "Đồng ý", "Từ chối");
                                break;

                        }
                        break;
                    case ConstNpc.MENU_GIAO_BONG:
                        ItemService.gI().giaobong(player, (int) Util.tinhLuyThua(10, select + 2));
                        break;
                    case ConstNpc.CONFIRM_DOI_THUONG_SU_KIEN:
                        if (select == 0) {
                            ItemService.gI().openBoxVip(player);
                        }
                        break;
                    case ConstNpc.CONFIRM_TELE_NAMEC:
                        if (select == 0) {
                            NgocRongNamecService.gI().teleportToNrNamec(player);
                            player.inventory.subGemAndRuby(50);
                            Service.getInstance().sendMoney(player);
                        }
                        break;
                }
            }
        };
    }

}
