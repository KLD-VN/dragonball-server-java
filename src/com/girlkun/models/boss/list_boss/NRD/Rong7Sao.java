package com.girlkun.models.boss.list_boss.NRD;

import com.girlkun.models.boss.Boss;
import com.girlkun.models.boss.BossesData;
import com.girlkun.models.map.ItemMap;
import com.girlkun.models.player.Player;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;


public class Rong7Sao extends Boss {

    public Rong7Sao() throws Exception {
        super(Util.randomBossId(), BossesData.Rong_7Sao);
    }

    @Override
    public void reward(Player plKill) {
        ItemMap it = new ItemMap(this.zone, 378, 1, this.location.x, this.location.y, -1);
        Service.getInstance().dropItemMap(this.zone, it);
    }

}


