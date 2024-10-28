package com.ngocrong.item;

import com.ngocrong.server.DragonBall;
import com.ngocrong.server.Server;
import com.ngocrong.util.Utils;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ItemOption {

    private transient byte active;
    private transient byte activeCard;
    private transient ItemOptionTemplate optionTemplate;
    private int param;
    private int id;

    public ItemOption(int optionTemplateId, int param) {
        this.param = param;
        this.id = optionTemplateId;
        Server server = DragonBall.getInstance().getServer();
        this.optionTemplate = server.iOptionTemplates.get(optionTemplateId);
    }

    public String getOptionString() {
        return Utils.replace(this.optionTemplate.getName(), "#", this.param + "");
    }

    public int[] format() {
        int id = optionTemplate.getId();
        int param = this.param;
        if (param > 65535) {
            switch (id) {
                case 6:
                    id = 22;
                    param /= 1000;
                    break;

                case 7:
                    id = 23;
                    param /= 1000;
                    break;

                case 31:
                    id = 171;
                    param /= 1000;
                    break;

                case 48:
                    id = 2;
                    param /= 1000;
                    break;
            }
        }
        return new int[]{id, param};
    }
}
