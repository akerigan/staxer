package comtech.util.adapter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Matyunin Nikolai (matyugan@yahoo.com)
 * Date: 07.06.2010
 * Time: 17:40:52
 */
public class ClientDirectionClassAdapter extends XmlAdapter<ClientDirectionClassAdapterItem[], Map<Integer, String>> {

    @Override
    public Map<Integer, String> unmarshal(ClientDirectionClassAdapterItem[] clientDirectionClassAdapterItems) throws Exception {
        Map<Integer, String> r = new HashMap<Integer, String>();
        for(ClientDirectionClassAdapterItem mapelement : clientDirectionClassAdapterItems)
          r.put(mapelement.key, mapelement.value);
        return r;
    }

    @Override
    public ClientDirectionClassAdapterItem[] marshal(Map<Integer, String> integerStringMap) throws Exception {

        ClientDirectionClassAdapterItem[] mapElements = new ClientDirectionClassAdapterItem[integerStringMap.size()];
        int i = 0;
        for (Map.Entry<Integer, String> entry : integerStringMap.entrySet())
          mapElements[i++] = new ClientDirectionClassAdapterItem(entry.getKey(), entry.getValue());

        return mapElements;
    }
}

class ClientDirectionClassAdapterItem {

    @XmlElement
    public Integer key;
    @XmlElement
    public String value;

    private ClientDirectionClassAdapterItem() {} //Required by JAXB

    public ClientDirectionClassAdapterItem(Integer key, String value) {
        this.key   = key;
        this.value = value;
    }
}
