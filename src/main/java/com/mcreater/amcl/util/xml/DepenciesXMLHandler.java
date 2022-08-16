package com.mcreater.amcl.util.xml;

import com.google.gson.Gson;
import com.mcreater.amcl.nativeInterface.ResourceGetter;
import com.mcreater.amcl.patcher.ClassPathInjector;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Vector;

public class DepenciesXMLHandler {
    public static Vector<DepencyItem> load() throws ParserConfigurationException, SAXException, IOException {
        return load(new ResourceGetter().get("assets/depencies.json"));
    }
    public static Vector<DepencyItem> load(InputStream is) throws ParserConfigurationException, SAXException, IOException {
        DepencyModel model = new Gson().fromJson(new InputStreamReader(is), DepencyModel.class);
        Vector<DepencyItem> items = new Vector<>();
        for (DepencyModel.ItemModel item : model.depencies){
            if (ClassPathInjector.version < 9 && item.old != null) {
                items.add(new DepencyItem(item.old, model.maven));
            }
            else {
                items.add(new DepencyItem(item.name, model.maven));
            }
        }
        return items;
    }
    public static class DepencyModel {
        public String maven;
        public List<ItemModel> depencies;
        public static class ItemModel {
            public String name;
            public String old;
        }
    }
}
