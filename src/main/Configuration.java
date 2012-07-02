/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package main;
import java.util.*;
import java.io.*;
/**
 *
 * @author testi
 */
public class Configuration {
private Map<String, String> entries;

    public String get(String key) throws EntryException {
    String value = entries.get(key);
        
    if (value == null) {throw new EntryException("Config file entry " + key +" is missing");}
    return value;
    }
public Configuration() {
entries = new HashMap<String, String>();
}

public void read(File file) throws IOException {
read(new FileInputStream(file));
}
public void read(InputStream is) throws IOException {
BufferedReader reader = new BufferedReader(new InputStreamReader(is));
String line;
while ((line = reader.readLine()) != null) {
SimpleStringTokenizer tokenizer = new SimpleStringTokenizer(line,'=');
if (tokenizer.hasMoreTokens()) {
String key = tokenizer.nextToken().trim();
String value="";
if (tokenizer.hasMoreTokens()) {
value=tokenizer.nextToken();
}
entries.put(key, value);
}
    
    
}
    
}
public Map<String, String> getEntries() {
return entries;
}

}
