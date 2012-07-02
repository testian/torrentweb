package torrentweb;
import java.io.*;

public class PathString {
public static String GetParent(String path)
{
int i=getLastSeparatorIndex(path);
if (i<0) {return "";}
return path.substring(0,i);

}
public static String GetName(String path)
{
int i=getLastSeparatorIndex(path);
if (i<0) {return path;}
return path.substring(i);

}

public static int getLastSeparatorIndex(String path)
{
	int i=path.length()-1;
	while(i>=0 && path.charAt(i) != File.separatorChar)
	{
		i--;
	}
	return i;
}




public static String AddDirectoryExtension(String path, String extension)
{
String name = GetName(path);
    if (path.equals(name)) {return path;}
	return AddDirectoryExtension(GetParent(path), extension) + extension + name;

}

public static String MandatoryRemoveDirectoryExtension(String path, String extension) {
String name = GetName(path);
String edit;
if (path.endsWith(extension)) {
if (path.equals(name)) {return path.substring(0,path.length()-extension.length());}
edit = MandatoryRemoveDirectoryExtension(GetParent(path), extension);
if (edit != null) {
return edit + name.substring(0,name.length()-extension.length());
}
}

return null;




}

public static String RemoveDirectoryExtension(String path, String extension) {
if (path.endsWith(extension)) {
return MandatoryRemoveDirectoryExtension(path, extension);
}
if (GetName(path).equals(path)) {return path;}
return MandatoryRemoveDirectoryExtension(GetParent(path), extension) + GetName(path);

}
}
